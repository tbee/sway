package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.ToolTipMixin;
import org.tbee.sway.mixin.ValueMixin;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * STabbedPane supports lazy loading, this means that when adding a tab a callback is registered as well,
 * which is called when the tab becomes visible.
 *
 * STabbedPane can be bound, so when a tab needs loading, it gets this value to start from.
 * For example: an STabbedPane bound to a city can load the details or crime numbers when the corresponding tab becomes visible.
 *
 * If the value changes or the visible tab changes, the visible tab's (synchronous) onActive callback is called,
 * or the (asynchronous) sequences of onLoad / onSuccess / onFailure callbacks are called.
 * The onLoad callback is called in a separate thread, so not to block the UI.
 * The other callbacks are called within the EDT.
 *
 * Example
 * <pre>{@code
 * // The tabbed pane reacts to changes in the value by binding
 * STabbedPane<City> sTabbedPane = STabbedPane.of()
 *     .bindTo(data.city$())
 *     .tab("details", STextField.ofString()  // synchronous: only value-to-component function provided
 *         , (city, sTextField) -> sTextField.setValue(city.name()))
 *     .tab("crime", new CrimeNumbersPanel() // asynchronous: value-to-value2 function, on-success value2-to-component function, and on-failure provided
 *         , city -> crimeApi.fetchNumbersFor(city.code()) // In a worker thread derived data is fetched
 *         , (crimeNumbers, crimeNumbersPanel) -> crimeNumbersPanel.setNumbers(crimeNumbers) // The derrived data is displayed
 *         , (throwable, crimeNumbersPanel) -> ... // Or something went wrong
 *     );
 * }</pre>
 *
 * @param <T> the type of the value.
 */
public class STabbedPane<T> extends JTabbedPane implements
        JComponentMixin<STabbedPane<T>>,
        BindToMixin<STabbedPane<T>, T>,
        ExceptionHandlerMixin<STabbedPane<T>>,
        ValueMixin<STabbedPane<T>, T>,
        ToolTipMixin<STabbedPane<T>> {

    public static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(STabbedPane.class);
    public static final String LOADED_COMPONENT = "loadedComponent";

    private T value = null;
    private final Map<Component, BiConsumer<T, Component>> onActiveCallbacks = new HashMap<>();
    private final Map<Component, Function<T, Object>> onLoadCallbacks = new HashMap<>();
    private final Map<Component, BiConsumer<Object, Component>> onSuccessCallbacks = new HashMap<>();
    private final Map<Component, BiConsumer<Throwable, Component>> onFailureCallbacks = new HashMap<>();
    private final List<Component> performedCallbacks = new ArrayList<>();
    private final Map<Component, OverlayData> overlayDatas = new HashMap<>();

    private record OverlayData(SLoadingOverlay overlay, int count){}

    private ExecutorService executorService = ForkJoinPool.commonPool();

    // ===========================================================================================================================
    // For Mixins

    @Override
    public BindingEndpoint<T> defaultBindingEndpoint() {
        return value$();
    }

    // ===========================================================================================================================
    // JavaBean

    /** Value */
    public void setValue(T v) {
        firePropertyChange(VALUE, this.value, this.value = v);
        reload();
    }
    public T getValue() {
        return this.value;
    }

    /** ExecutorService: use to run async tasks */
    public void setExecutorService(ExecutorService v) {
        firePropertyChange(EXECUTORSERVICE, this.executorService, this.executorService = v);
    }
    public ExecutorService getExecutorService() {
        return this.executorService;
    }
    public STabbedPane<T> executorService(ExecutorService v) {
        setExecutorService(v);
        return this;
    }
    public BindingEndpoint<ExecutorService> executorService$() {
        return BindingEndpoint.of(this, EXECUTORSERVICE, exceptionHandler);
    }
    final static public String EXECUTORSERVICE = "executorService";


    // ========================================================
    // EXCEPTION HANDLER

    /**
     * Set the ExceptionHandler used a.o. in binding
     * @param v
     */
    public void setExceptionHandler(ExceptionHandler v) {
        firePropertyChange(EXCEPTIONHANDLER, exceptionHandler, exceptionHandler = v);
    }
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
    private ExceptionHandler exceptionHandler = this::handleException;


    // ===========================================================================================================================
    // Tabs

    public STabbedPane<T> tab(String title, Icon icon, Component component, String tip) {
        super.addTab(title, icon, component, tip);
        return this;
    }

    public STabbedPane<T> tab(String title, Icon icon, Component component) {
        super.addTab(title, icon, component);
        return this;
    }

    public STabbedPane<T> tab(String title, Component component) {
        super.add(title, component);
        return this;
    }

    /**
     * Add sync-updated tab
     */
    public <C extends Component> STabbedPane<T> tab(String title, Icon icon, Component component, String tip, BiConsumer<T, C> onActiveCallback) {
        onActiveCallbacks.put(component, (BiConsumer<T, Component>) onActiveCallback);
        super.addTab(title, icon, component, tip);
        return this;
    }

    /**
     * Add async-updated tab
     */
    public <R, C extends Component> STabbedPane<T> tab(String title, Icon icon, Component component, String tip, Function<T, R> onLoadCallback, BiConsumer<R, C> onSuccessCallback, BiConsumer<Throwable, C> onFailureCallback) {
        onLoadCallbacks.put(component, (Function<T, Object>) onLoadCallback);
        onSuccessCallbacks.put(component, (BiConsumer<Object, Component>)onSuccessCallback);
        onFailureCallbacks.put(component, (BiConsumer<Throwable, Component>)onFailureCallback);
        super.addTab(title, icon, component, tip);
        return this;
    }

    /**
     * Add async-updated tab
     */
    public <R, C extends Component> STabbedPane<T> tab(String title, Icon icon, Component component, String tip, Function<T, R> onLoadCallback, BiConsumer<R, C> onSuccessCallback) {
        onLoadCallbacks.put(component, (Function<T, Object>) onLoadCallback);
        onSuccessCallbacks.put(component, (BiConsumer<Object, Component>)onSuccessCallback);
        onFailureCallbacks.put(component, (t, c) -> handleException(t));
        super.addTab(title, icon, component, tip);
        return this;
    }

    public <C extends Component> STabbedPane<T> tab(String title, Icon icon, Component component, BiConsumer<T, C> onActiveCallback) {
        return tab(title, icon, component, null, onActiveCallback);
    }
    public <R, C extends Component> STabbedPane<T> tab(String title, Icon icon, Component component, Function<T, R> onLoadCallback, BiConsumer<R, C> onSuccessCallback, BiConsumer<Throwable, C> onFailureCallback) {
        return tab(title, icon, component, null, onLoadCallback, onSuccessCallback, onFailureCallback);
    }

    public <C extends Component> STabbedPane<T> tab(String title, C component, BiConsumer<T, C> onActiveCallback) {
        return tab(title, null, component, null, onActiveCallback);
    }
    public <R, C extends Component> STabbedPane<T> tab(String title, C component, Function<T, R> onLoadCallback, BiConsumer<R, C> onSuccessCallback) {
        return tab(title, null, component, null, onLoadCallback, onSuccessCallback, null);
    }
    public <R, C extends Component> STabbedPane<T> tab(String title, C component, Function<T, R> onLoadCallback, BiConsumer<R, C> onSuccessCallback, BiConsumer<Throwable, C> onFailureCallback) {
        return tab(title, null, component, null, onLoadCallback, onSuccessCallback, onFailureCallback);
    }

    /**
     * Add nested tabbedpane
     */
    public STabbedPane<T> pane(String title, STabbedPane<?> component) {
        super.addTab(title, component);
        return this;
    }

    @Override
    public void removeTabAt(int index) {
        Component component = super.getComponentAt(index);
        super.removeTabAt(index);
        onActiveCallbacks.remove(component);
        onLoadCallbacks.remove(component);
        onSuccessCallbacks.remove(component);
        onFailureCallbacks.remove(component);
    }

    @Override
    protected void fireStateChanged() {
        super.fireStateChanged();
        loadVisibleTabIfNeeded();
    }

    /**
     * Immediately reloads the visible tab, and other tabs when they become visible.
     */
    public void reload() {
        performedCallbacks.clear();
        loadVisibleTabIfNeeded();
    }

    /**
     * Immediately reloads the visible tab only.
     */
    public void reloadVisibleTab() {
        performedCallbacks.remove(getSelectedComponent());
        loadVisibleTabIfNeeded();
    }

    protected boolean loadVisibleTabIfNeeded() {

        // If already loaded, bail out
        Component component = getSelectedComponent();
        if (component == null || performedCallbacks.contains(component)) {
            return false;
        }
        performedCallbacks.add(component);

        // Synchronous loading
        BiConsumer<T, Component> onActiveCallback = onActiveCallbacks.get(component);
        if (onActiveCallback != null) {
            onActiveCallback.accept(value, component);
            firePropertyChange("loadedComponent", null, component);
        }

        // Asynchronous loading
        Function<T, Object> onLoadCallback = onLoadCallbacks.get(component);
        BiConsumer<Object, Component> onSuccessCallback = onSuccessCallbacks.get(component);
        BiConsumer<Throwable, Component> onFailureCallback = onFailureCallbacks.get(component);
        if (onLoadCallback != null) {
            boolean isBeingShownOnTheScreen = isShowing();
            if (isBeingShownOnTheScreen) {
                showOverlay(component);
            }
            executorService.submit(() -> {
                try {
                    final Object result = onLoadCallback.apply(value);
                    if (isBeingShownOnTheScreen) {
                        hideOverlay(component);
                    }
                    invokeLater(onSuccessCallback, result, component);
                } catch (Throwable e) {
                    if (!invokeLater(onFailureCallback, e, component)) {
                        handleException(e);
                    }
                }
            });
        }
        return true;
    }

    private void showOverlay(Component component) {

        // If the overlay is already active only increase the counter
        OverlayData overlayData = this.overlayDatas.get(component);
        if (overlayData == null) {
            SLoadingOverlay overlay = new SLoadingOverlay();
            SOverlayPane.overlayWith(component, overlay);
            this.overlayDatas.put(component, new OverlayData(overlay, 1));
        } else {
            this.overlayDatas.put(component, new OverlayData(overlayData.overlay(), overlayData.count() + 1));
        }
    }

    private void hideOverlay(Component component) {
        OverlayData overlayData = this.overlayDatas.get(component);
        if (overlayData.count() == 1) {
            this.overlayDatas.remove(component);
            SwingUtilities.invokeLater(() -> {
                SOverlayPane.removeOverlay(component, overlayData.overlay());
            });
        } else {
            this.overlayDatas.put(component, new OverlayData(overlayData.overlay(), overlayData.count() - 1));
        }
    }

    private <R> boolean invokeLater(BiConsumer<R, Component> callback, R value, Component component) {
        firePropertyChange(LOADED_COMPONENT, null, component);

        if (callback == null) {
            return false;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                callback.accept(value, component);
            } catch (RuntimeException e) {
                handleException(e);
            }
        });
        return true;
    }

    // ===========================================================================================================================
    // FLUENT API

    static public <T> STabbedPane<T> of() {
        return new STabbedPane<T>();
    }
}
