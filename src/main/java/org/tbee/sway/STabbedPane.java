package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.util.ExceptionUtil;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.beans.PropertyChangeListener;
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
 *     .addTab("details", STextField.ofString()  // synchronous: only value-to-component function provided
 *         , (city, sTextField) -> sTextField.setValue(city.name()))
 *     .addTab("crime", new CrimeNumbersPanel() // asynchronous: value-to-value2 function, on-success value2-to-component function, and on-failure provided
 *         , city -> crimeApi.fetchNumbersFor(city.code()) // In a worker thread derived data is fetched
 *         , (crimeNumbers, crimeNumbersPanel) -> crimeNumbersPanel.setNumbers(crimeNumbers) // The derrived data is displayed
 *         , (throwable, crimeNumbersPanel) -> ... // Or something went wrong
 *     );
 * }</pre>
 *
 * @param <T> the type of the value.
 */
public class STabbedPane<T> extends JTabbedPane {
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

    public STabbedPane() {
        addPropertyChangeListener(VALUE, evt -> {
            reload();
        });
    }

    // ===========================================================================================================================
    // JavaBean

    /** Value */
    public void setValue(T v) {
        firePropertyChange(VALUE, this.value, this.value = v);
    }
    public T getValue() {
        return this.value;
    }
    public STabbedPane<T> value(T value) {
        setValue(value);
        return this;
    }
    public BindingEndpoint<T> value$() {
        return BindingEndpoint.of(this, VALUE, exceptionHandler);
    }
    final static public String VALUE = "value";

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
    public STabbedPane<T> exceptionHandler(ExceptionHandler v) {
        setExceptionHandler(v);
        return this;
    }
    final static public String EXCEPTIONHANDLER = "exceptionHandler";
    ExceptionHandler exceptionHandler = this::handleException;
    public BindingEndpoint<ExceptionHandler> exceptionHandler$() {
        return BindingEndpoint.of(this, EXCEPTIONHANDLER, exceptionHandler);
    }

    private boolean handleException(Throwable e, JComponent component, Object oldValue, Object newValue) {
        return handleException(e);
    }
    private boolean handleException(Throwable e) {

        // Force focus back
        SwingUtilities.invokeLater(() -> this.grabFocus());

        // Display the error
        if (LOGGER.isDebugEnabled()) LOGGER.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }

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
            showOverlay(component);
            executorService.submit(() -> {
                try {
                    final Object result = onLoadCallback.apply(value);
                    hideOverlay(component);
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

    public STabbedPane<T> name(String v) {
        setName(v);
        return this;
    }

    public STabbedPane<T> toolTipText(String t) {
        setToolTipText(t);
        return this;
    }

    public STabbedPane<T> enabled(boolean v) {
        setEnabled(v);
        return this;
    }

    public STabbedPane<T> visible(boolean value) {
        setVisible(value);
        return this;
    }

    public STabbedPane<T> overlayWith(Component overlayComponent) {
        SOverlayPane.overlayWith(this, overlayComponent);
        return this;
    }
    public STabbedPane<T> removeOverlay(Component overlayComponent) {
        SOverlayPane.removeOverlay(this, overlayComponent);
        return this;
    }

    public STabbedPane<T> withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        return this;
    }
    public STabbedPane<T> withPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        return this;
    }

    /**
     * Binds the default property 'value'
     */
    public STabbedPane<T> bindTo(BindingEndpoint<T> bindingEndpoint) {
        value$().bindTo(bindingEndpoint);
        return this;
    }

    /**
     * Binds to the default property 'value'.
     * Binding in this way is not type safe!
     */
    public STabbedPane<T> bindTo(Object bean, String propertyName) {
        return bindTo(BindingEndpoint.of(bean, propertyName));
    }

    /**
     * Binds to the default property 'value'.
     * Binding in this way is not type safe!
     */
    public STabbedPane<T> bindTo(BeanBinder<?> beanBinder, String propertyName) {
        return bindTo(BindingEndpoint.of(beanBinder, propertyName));
    }
}
