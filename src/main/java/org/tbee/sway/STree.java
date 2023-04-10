package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.support.BeanMonitor;
import org.tbee.sway.tree.STreeCore;
import org.tbee.util.ExceptionUtil;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class STree<T extends Object> extends SBorderPanel { // TBEERNOT Does it make sense have a Generic STree? Usually Trees hold different classes. A single class is the exception.
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
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                          boolean leaf, int row, boolean hasFocus) {
                Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

                // Use format
                Format format = STree.this.format;
                if (format == null) {
                    format = formats.get(value.getClass());
                }
                if (format == null) {
                    format = FormatRegistry.findFor(value.getClass());
                }
                if (format != null && component instanceof JLabel jLabel) {
                    jLabel.setText(format.toString((T)value));
                    jLabel.setIcon(format.toIcon((T)value));
                    jLabel.setHorizontalAlignment(format.horizontalAlignment().getSwingConstant());
                }

                return component;
            }
        });

        // Start listening for selection changes
        sTreeCore.addTreeSelectionListener(e -> {
            var selectedItems = getSelection();
            if (selectionChangedListeners != null) {
                selectionChangedListeners.forEach(l -> l.accept(selectedItems));
            }
            firePropertyChange(SELECTION, null, selectedItems);
        });
    }

    public STreeCore<T> getSTreeCore() {
        return sTreeCore;
    }


    // =======================================================================
    // ROOT

    private T root;

    /**
     * If root is a list, then tree assumes that is children is the list itself.
     * And that the root should not be shown.
     * @param v
     */
    public void setRoot(T v) {
        this.root = v;

        // if root is a list object, then its children is the list itself
        if (root instanceof List) {
            childrenOfRoot((List<?>) root);
            rootVisible(false);
        }

        treeStructureChanged();
    }
    public T getRoot() {
        return this.root;
    }
    public STree<T> root(T v) {
        setRoot(v);
        return this;
    }

    // =======================================================================
    // CHILDREN

    final private Map<Function<T, Boolean>, Function<T, List<?>>> childrenOf = new Hashtable<>();

    /**
     *
     * @param gateFunction function that returns true if the associated childrenFunction will return a list of children for the provided node
     * @param childrenFunction function that will return the children of the provided node
     * @return
     */
    public STree<T> childrenOf(Function<T, Boolean> gateFunction, Function<T, List<?>> childrenFunction) {
        childrenOf.put(gateFunction, childrenFunction);
        treeStructureChanged();
        return this;
    }

    /**
     *
     * @param parentClazz the class for which the childrenFunction is defined
     * @param childrenFunction the function that will get the children given a node of type parentClazz
     * @return
     * @param <X>
     */
    public <X> STree<T> childrenOf(Class<X> parentClazz, Function<X, List<?>> childrenFunction) {
        return childrenOf(o -> parentClazz.isAssignableFrom(o.getClass()), (Function<T, List<?>>)childrenFunction);
    }

    /**
     *
     * @param childrenFunction function that will provide children for any node in the tree,
     *                         or nodes that are not handled by previous declared childrenOf rules.
     * @return
     */
    public STree<T> childrenOf(Function<T, List<?>> childrenFunction) {
        return childrenOf(o -> true, childrenFunction);
    }

    /**
     * This method is usually used when the root is not visible, like when the root is a list.
     * @param children list of children of the root node
     * @return
     * @param <X>
     */
    public <X> STree<T> childrenOfRoot(List<?> children) {
        return childrenOf(node -> node == root, node -> children);
    }

    /**
     *
     * @param parent
     * @return
     */
    public List<T> determineChildrenOf(T parent) {
        if (beanMonitor != null) {
            beanMonitor.monitor(parent);
        }
        for (Function<T, Boolean> gateFunction : childrenOf.keySet()) {
            if (gateFunction.apply(parent)) {
                Function<T, List<?>> childrenFunction = this.childrenOf.get(gateFunction);
                List<T> children = (List<T>)childrenFunction.apply(parent);
                return children;
            }
        }
        return List.of();
    }

    /**
     * Erase all parent to children mappings
     * @return
     */
    public STree<T> clearChildrenOf() {
        childrenOf.clear();
        treeStructureChanged();
        return this;
    }


    // =======================================================================
    // EVENTS

    public void treeStructureChanged() {
        TreePath treePath = toRoot.apply(root);
        sTreeCore.getSTreeModel().treeStructureChanged(treePath);
    }
    public void treeNodesChanged(T node) {
        TreePath treePath = toRoot.apply(node);
        System.out.println("!!!! treePath " + treePath);
        sTreeCore.getSTreeModel().treeNodesChanged(treePath);
    }


    // =======================================================================
    // TREEPATH TO ROOT

    private Function<T, TreePath> toRoot = this::findTreePathByWalkingTheTree;;

    /**
     *
     * @param v
     */
    public void setToRoot(Function<T, TreePath> v) {
        this.toRoot = v;
    }
    public Function<T, TreePath> getToRoot() {
        return this.toRoot;
    }
    public STree<T> toRoot(Function<T, TreePath> v) {
        setToRoot(v);
        return this;
    }

    public TreePath findTreePathByWalkingTheTree(T child) {
        if (child.equals(root)) {
            return new TreePath(new Object[]{root});
        }
        List<T> rootToNode = findTreePathByWalkingTheTree(child, root);
        if (rootToNode != null) {
            rootToNode.add(0, root);
            return new TreePath(rootToNode.toArray());
        }
        return null;
    }

    private List<T> findTreePathByWalkingTheTree(T child, T parent) {
        List<T> children = determineChildrenOf(parent);

        // Is it one of the parent's children?
        for (T candidateChild : children) {
            if (child.equals(candidateChild)) {
                List<T> rootToNode = new ArrayList<>();
                rootToNode.add(child);
                return rootToNode;
            }
        }

        // Look a level deeper
        for (T candidateParent : children) {
            List<T> rootToNode = findTreePathByWalkingTheTree(child, candidateParent);
            if (rootToNode != null) {
                rootToNode.add(0, candidateParent);
                return rootToNode;
            }
        }
        return null;
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

    private Map<Class<?>, Format<?>> formats = new HashMap<>();

    /**
     * Register formats only for this tree.
     *
     * @param clazz
     * @param format
     * @return
     */
    public STree<T> registerFormat(Class<?> clazz, Format<?> format) {
        formats.put(clazz, format);
        return this;
    }
    public boolean unregisterFormat(Class<?> clazz) {
        return formats.remove(clazz) != null;
    }

    // ===========================================================================
    // SELECTION

    public enum SelectionMode{ //
        SINGLE(TreeSelectionModel.SINGLE_TREE_SELECTION), //
        INTERVAL(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION), //
        MULTIPLE(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

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

    /**
     *
     * @param v
     */
    public void setSelectionMode(STree.SelectionMode v) {
        sTreeCore.getSelectionModel().setSelectionMode(v.code);
    }
    public STree.SelectionMode getSelectionMode() {
        return STree.SelectionMode.of(sTreeCore.getSelectionModel().getSelectionMode());
    }
    public STree<T> selectionMode(STree.SelectionMode v) {
        setSelectionMode(v);
        return this;
    }

    /**
     *
     * @return
     */
    public List<T> getSelection() {
        var selectedItems = new ArrayList<T>(sTreeCore.getSelectionModel().getSelectionCount());
        TreePath[] paths = sTreeCore.getSelectionPaths();
        for (TreePath path : paths != null ? paths : new TreePath[0]) {
            selectedItems.add((T) path.getLastPathComponent());
        }
        return Collections.unmodifiableList(selectedItems);
    }

    /**
     *
     */
    public void setSelection(List<T> values) {
        clearSelection();
        for (T value : values) {
            TreePath treePath = toRoot.apply(value);
            sTreeCore.addSelectionPath(treePath);
        }
    }

    /**
     *
     */
    public void clearSelection() {
        sTreeCore.clearSelection();
    }

    final static public String SELECTION = "selection";
    public BindingEndpoint<List<T>> selection$() {
        return BindingEndpoint.of(this, SELECTION, exceptionHandler);
    }

    /**
     *
     * @param listener
     */
    synchronized public void addSelectionChangedListener(Consumer<List<T>> listener) {
        if (selectionChangedListeners == null) {
            selectionChangedListeners = new ArrayList<>();
        }
        selectionChangedListeners.add(listener);
    }
    synchronized public boolean removeSelectionChangedListener(Consumer<List<T>> listener) {
        if (selectionChangedListeners == null) {
            return false;
        }
        return selectionChangedListeners.remove(listener);
    }
    private List<Consumer<List<T>>> selectionChangedListeners;

    /**
     * @param onSelectionChangedListener
     * @return
     */
    public STree<T> onSelectionChanged(Consumer<List<T>> onSelectionChangedListener) {
        addSelectionChangedListener(onSelectionChangedListener);
        return this;
    }

    // ===========================================================================
    // MONITORING

    private BeanMonitor beanMonitor = null;

    public STree<T> monitorBeans(boolean v) {
        if (this.beanMonitor != null) {
            beanMonitor.unmonitorAll();
            beanMonitor = null;
        }
        if (v) {
            beanMonitor = new BeanMonitor(beanPropertyChangeListener);
        }
        treeStructureChanged();
        return this;
    }

    final private PropertyChangeListener beanPropertyChangeListener = evt -> {
        T node = (T)evt.getSource();
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Property change event for " + node);
        System.out.println("Property change event for " + node);

        // Find the place in the tree that changed and refresh (TBEERNOT can we only repaint the node)
//        STree.this.treeStructureChanged();
        STree.this.treeNodesChanged(node);
    };

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

    public STree<T> rootVisible(boolean value) {
        sTreeCore.setRootVisible(value);
        return this;
    }

    static public <T> STree<T> of(T root) {
    	return new STree<T>().root(root);
    }

    /**
     * Binds to the default property 'selection'
     */
    public STree<T> bindTo(BindingEndpoint<List<T>> bindingEndpoint) {
        selection$().bindTo(bindingEndpoint);
        return this;
    }

    /**
     * Binds to the default property 'selection'.
     * Binding in this way is not type safe!
     */
    public STree<T> bindTo(Object bean, String propertyName) {
        return bindTo(BindingEndpoint.of(bean, propertyName));
    }

    /**
     * Binds to the default property 'selection'.
     * Binding in this way is not type safe!
     */
    public STree<T> bindTo(BeanBinder<?> beanBinder, String propertyName) {
        return bindTo(BindingEndpoint.of(beanBinder, propertyName));
    }
}
