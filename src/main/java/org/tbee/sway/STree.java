package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.tree.STreeCore;
import org.tbee.util.ExceptionUtil;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.util.List;
import java.util.function.Function;

public class STree<T> extends SBorderPanel {
    static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(STree.class);

    final private STreeCore<T> sTreeCore;
    final private JScrollPane jScrollPane;

    public STree() {
        super();
        sTreeCore = new STreeCore<>(this);
        jScrollPane = new JScrollPane(sTreeCore);
        center(jScrollPane);

        sTreeCore.setCellRenderer(new DefaultTreeCellRenderer(){
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                          boolean leaf, int row, boolean hasFocus) {
                Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                // Use format
                Format format = STree.this.format;
                if (format == null && root != null) {
                    format = FormatRegistry.findFor(root.getClass());
                }
                if (format != null && component instanceof JLabel jLabel) {
                    jLabel.setText(format.toString((T)value));
                    jLabel.setIcon(format.toIcon((T)value));
                    jLabel.setHorizontalAlignment(format.horizontalAlignment().getSwingConstant());
                }

                return component;
            }
        });

//        // Start listening for selection changes
//        sTreeCore.getSelectionModel().addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                var selectedItems = getSelection();
//                if (selectionChangedListeners != null) {
//                    selectionChangedListeners.forEach(l -> l.accept(selectedItems));
//                }
//                firePropertyChange(SELECTION, null, selectedItems);
//            }
//        });
    }

    public STreeCore<T> getSTreeCore() {
        return sTreeCore;
    }


    // =======================================================================
    // DATA

    private T root;

    /**
     *
     * @param v
     */
    public void setRoot(T v) {
// TBEERNOT       unregisterFromAllBeans();
        this.root = v;
        sTreeCore.treeStructureChanged();
// TBEERNOT       registerToAllBeans();
    }
    public T getRoot() {
        return this.root;
    }
    public STree<T> root(T v) {
        setRoot(v);
        return this;
    }

    private Function<T, List<T>> children = parent -> List.of(); // no children

    /**
     *
     * @param v
     */
    public void setChildren(Function<T, List<T>> v) {
        this.children = v;
        sTreeCore.treeStructureChanged();
    }
    public Function<T, List<T>> getChildren() {
        return this.children;
    }
    public STree<T> children(Function<T, List<T>> v) {
        setChildren(v);
        return this;
    }

    // ===========================================================================
    // RENDERING

    private Format<T> format = null;

    // TBEERNOT not sure how to do this, but I know this API needs to be there. TTD? :-D
    /**
     *
     * @param v
     * @return
     */
    public STree<T> render(Format<T> v) {
        this.format = v;
        return this;
    }

    // TBEERNOT not sure how to do this, but I know this API needs to be there. TTD? :-D
    /**
     *
     * @param clazz
     * @return
     */
    public STree<T> renderFor(Class<T> clazz) {
        return render((Format<T>) FormatRegistry.findFor(clazz));
    }


    // ===========================================================================
    // SELECTION

    public enum SelectionMode{ //
        SINGLE(ListSelectionModel.SINGLE_SELECTION), //
        INTERVAL(ListSelectionModel.SINGLE_INTERVAL_SELECTION), //
        MULTIPLE(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        private int code;
        private SelectionMode(int code) {
            this.code = code;
        }

        static STree.SelectionMode of(int code) {
            for (STree.SelectionMode selectionMode : values()) {
                if (selectionMode.code == code) {
                    return selectionMode;
                }
            }
            throw new IllegalArgumentException("Code does not exist " + code);
        }
    }

//    /**
//     *
//     * @param v
//     */
//    public void setSelectionMode(STree.SelectionMode v) {
//        sTreeCore.setSelectionMode(v.code);
//    }
//    public STree.SelectionMode getSelectionMode() {
//        return STree.SelectionMode.of(sTreeCore.getSelectionModel().getSelectionMode());
//    }
//    public STree<T> selectionMode(STree.SelectionMode v) {
//        setSelectionMode(v);
//        return this;
//    }
//
//    /**
//     *
//     * @return
//     */
//    public List<T> getSelection() {
//        var selectedItems = new ArrayList<T>(sTreeCore.getSelectionModel().getSelectionMode());
//        for (int rowIdx : sTreeCore.getSelectionModel().getSelectedIndices()) {
//            selectedItems.add(getData().get(rowIdx));
//        }
//        return Collections.unmodifiableList(selectedItems);
//    }
//
//    /**
//     *
//     */
//    public void setSelection(List<T> values) {
//        clearSelection();
//        List<T> data = getData();
//        for (T value : values) {
//            int index = data.indexOf(value);
//            sTreeCore.getSelectionModel().addSelectionInterval(index, index);
//        }
//    }
//
//    /**
//     *
//     */
//    public void clearSelection() {
//        sTreeCore.clearSelection();
//    }
//
//    final static public String SELECTION = "selection";
//    public BindingEndpoint<List<T>> selection$() {
//        return BindingEndpoint.of(this, SELECTION, exceptionHandler);
//    }
//
//    /**
//     *
//     * @param listener
//     */
//    synchronized public void addSelectionChangedListener(Consumer<List<T>> listener) {
//        if (selectionChangedListeners == null) {
//            selectionChangedListeners = new ArrayList<>();
//        }
//        selectionChangedListeners.add(listener);
//    }
//    synchronized public boolean removeSelectionChangedListener(Consumer<List<T>> listener) {
//        if (selectionChangedListeners == null) {
//            return false;
//        }
//        return selectionChangedListeners.remove(listener);
//    }
//    private List<Consumer<List<T>>> selectionChangedListeners;
//
//    /**
//     * @param onSelectionChangedListener
//     * @return
//     */
//    public STree<T> onSelectionChanged(Consumer<List<T>> onSelectionChangedListener) {
//        addSelectionChangedListener(onSelectionChangedListener);
//        return this;
//    }

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
    public STree<T> exceptionHandler(ExceptionHandler v) {
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


    // ===========================================================================
    // FLUENT API

    @Override
    public void setName(String v) {
        super.setName(v);
        sTreeCore.setName(v + ".sTreeCore"); // For tests we need to address the actual list
    }
    public STree<T> name(String v) {
        setName(v);
        return this;
    }

    public STree<T> visible(boolean value) {
        setVisible(value);
        return this;
    }
    
    static public <T> STree<T> of(T root) {
    	return new STree<T>().root(root);
    }

//    /**
//     * Binds to the default property 'selection'
//     */
//    public STree<T> bindTo(BindingEndpoint<List<T>> bindingEndpoint) {
//        selection$().bindTo(bindingEndpoint);
//        return this;
//    }
//
//    /**
//     * Binds to the default property 'selection'.
//     * Binding in this way is not type safe!
//     */
//    public STree<T> bindTo(Object bean, String propertyName) {
//        return bindTo(BindingEndpoint.of(bean, propertyName));
//    }
//
//    /**
//     * Binds to the default property 'selection'.
//     * Binding in this way is not type safe!
//     */
//    public STree<T> bindTo(BeanBinder<?> beanBinder, String propertyName) {
//        return bindTo(BindingEndpoint.of(beanBinder, propertyName));
//    }

    // TBEERNOT ExceptionHandler
}
