package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.util.ExceptionUtil;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
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
 * This TabbedPane supports lazy loading, this means that data in a tab is only updated if it becomes or is visible.
 * This is done by binding or setting the value of the TabbedPane to some other data.
 * If that value changes, each tab's onActiveCallback is called with the data value.
 *
 * @param <T>
 */
public class STabbedPane<T> extends JTabbedPane {
    static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(STabbedPane.class);

    private T value = null;
    private final Map<Component, BiConsumer<T, Component>> onActiveCallbacks = new HashMap<>();
    private final Map<Component, Function<T, Object>> onLoadCallbacks = new HashMap<>();
    private final Map<Component, BiConsumer<Object, Component>> onSuccessCallbacks = new HashMap<>();
    private final Map<Component, BiConsumer<Throwable, Component>> onFailureCallbacks = new HashMap<>();
    private final List<Component> performedCallbacks = new ArrayList<>();

    private ExecutorService executorService = ForkJoinPool.commonPool();

    public STabbedPane() {
        addPropertyChangeListener(VALUE, evt -> {
            performedCallbacks.clear();
            callCallbacksForActiveTab();
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

//    public void addTab(String title, Icon icon, Component component, String tip) {
//    }
//    public void addTab(String title, Icon icon, Component component) {
//    }
//    public void addTab(String title, Component component) {
//    }
    public <C extends Component> STabbedPane<T> addTab(String title, C component, BiConsumer<T, C> onActiveCallback) {
        onActiveCallbacks.put(component, (BiConsumer<T, Component>) onActiveCallback);
        super.addTab(title, component);
        return this;
    }
    public <R, C extends Component> STabbedPane<T> addTab(String title, C component, Function<T, R> onLoadCallback, BiConsumer<R, C> onSuccessCallback, BiConsumer<Throwable, C> onFailureCallback) {
        onLoadCallbacks.put(component, (Function<T, Object>) onLoadCallback);
        onSuccessCallbacks.put(component, (BiConsumer<Object, Component>)onSuccessCallback);
        onFailureCallbacks.put(component, (BiConsumer<Throwable, Component>)onFailureCallback);
        super.addTab(title, component);
        return this;
    }

    public void removeTabAt(int index) {
        Component component = getComponentAt(index);
        super.removeTabAt(index);
        onActiveCallbacks.remove(component);
        onLoadCallbacks.remove(component);
        onSuccessCallbacks.remove(component);
        onFailureCallbacks.remove(component);
    }

    protected void fireStateChanged() {
        super.fireStateChanged();
        callCallbacksForActiveTab();
    }

    protected void callCallbacksForActiveTab() {

        Component component = getSelectedComponent();
        if (performedCallbacks.contains(component)) {
            return;
        }
        performedCallbacks.add(component);

        // Sync
        BiConsumer<T, Component> onActiveCallback = onActiveCallbacks.get(component);
        if (onActiveCallback != null) {
            onActiveCallback.accept(value, component);
        }

        // Async
        Function<T, Object> onLoadCallback = onLoadCallbacks.get(component);
        BiConsumer<Object, Component> onSuccessCallback = onSuccessCallbacks.get(component);
        BiConsumer<Throwable, Component> onFailureCallback = onFailureCallbacks.get(component);
        if (onLoadCallback != null) {
            // TBEERNOT: introduce busy icon
            executorService.submit(() -> {
                try {
                    final Object result = onLoadCallback.apply(value);
                    invokeLater(onSuccessCallback, result, component);
                } catch (Throwable e) {
                    if (!invokeLater(onFailureCallback, e, component)) {
                        handleException(e);
                    }
                }
            });
        }
    }

    private <R> boolean invokeLater(BiConsumer<R, Component> callback, R value, Component component) {
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
