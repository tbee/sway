package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.ExceptionHandlerDefaultMixin;
import org.tbee.sway.mixin.SelectionMixin;
import org.tbee.sway.support.BeanMonitor;
import org.tbee.sway.tree.Node;
import org.tbee.sway.tree.STreeCore;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A tree consists of tree nodes, each representing a value in some domain.
 * The tree starts at the root node, and only needs a way to get the children of any node.
 * The tree node takes care of visualizing the value it represents, for this a format is used.
 *
 * So if only one type of node is present in the tree, then a root, formatter and children-function is needed.
 * And that is exactly all that is present in the code.
 * <pre>{@code
 * var sTree = STree.of(amsterdam)
 *         .render(new CityFormat())
 *         .childrenOf(City::getPartnerCities);
 * }</pre>
 *
 * But often a tree contains different type of values as nodes.
 * In order to create such a tree, multiple formatters and children-functions are needed.
 * STree of course uses the FormatRegistry if no explicit formatter is defined.
 * <pre>{@code
 * var sTree = STree.of(cities)
 *         .childrenOf(City.class, City::getStreets)
 *         .childrenOf(Street.class, Street::getBuildings);
 * }</pre>
 *
 * If the root of the tree is not a single node but a collection (like in the example above),
 * then STree automatically creates a virtual root node and hides that.
 * Visually this results in a multi-node root.
 *
 * STree automatically includes scrollbars.
 *
 * @param <T>
 */
public class STree<T extends Object> extends JPanel implements
        JComponentMixin<STree<T>>,
        ExceptionHandlerDefaultMixin<STree<T>>,
        SelectionMixin<STree<T>, T>,
        BindToMixin<STree<T>, List<T>> {
    static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(STree.class);

    final private STreeCore<T> sTreeCore;
    final private JScrollPane jScrollPane;

    public STree() {
        setLayout(new BorderLayout());
        sTreeCore = new STreeCore<>(this);
        jScrollPane = new JScrollPane(sTreeCore);
        add(jScrollPane, BorderLayout.CENTER);

        sTreeCore.setCellRenderer(new DefaultTreeCellRenderer(){
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object node, boolean selected, boolean expanded,
                                                          boolean leaf, int row, boolean hasFocus) {
                Component component = super.getTreeCellRendererComponent(tree, node, selected, expanded, leaf, row, hasFocus);
                Object value = ((Node)node).value();

                // Use format
                Format format = STree.this.format;
                if (format == null && value != null) {
                    format = formats.get(value.getClass());
                }
                if (format == null && value != null) {
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


    // ===========================================================================================================================
    // For Mixins

    @Override
    public BindingEndpoint<List<T>> defaultBindingEndpoint() {
        return selection$();
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
    public void treeStructureChanged(T node) {
        TreePath treePath = toRoot.apply(node);
        sTreeCore.getSTreeModel().treeStructureChanged(treePath);
    }
    public void treeNodeChanged(T node) {
        TreePath treePath = toRoot.apply(node);
        sTreeCore.getSTreeModel().treeNodesChanged(treePath);
    }
    public void treeNodeInserted(T node) {
        TreePath treePath = toRoot.apply(node);
        sTreeCore.getSTreeModel().treeNodesInserted(treePath);
    }


    // =======================================================================
    // TREEPATH TO ROOT

    private Function<T, TreePath> toRoot = this::findTreePath;;

    /**
     * A custom function to determine the path to the root node (TreePath).
     * See findTreePath
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

    private Node node(T value, T parent) {
        return sTreeCore.getSTreeModel().node(value, parent);
    }

    /**
     * This method tries to determine the path from a node to the root (TreePath).
     * Per default it does so in three ways:
     * 1. (TODO) Use the displayed nodes so see if there is a path. The tree has stored information of everything it has rendered so far.
     * 2. (TODO) Use the toParent methods to determine if a path can be determined
     * 3. Walk the complete tree in search of the specified node.
     *
     * It is obvious that the last approach is the most expensive, but the first two are not guaranteed to lead to success.
     * The user may also provide a custom toRoot function, if the default approach does not suffice.
     */
    public TreePath findTreePath(T value) {
        if (Objects.equals(value, root)) {
            return new TreePath(new Object[]{node(root, null)});
        }
        TreePath treePath = null;

        // First try the efficient but not guaranteed approach
        treePath = findTreePathByNodeAndToParent(value);
        if (treePath != null) {
            return treePath;
        }

        // Walk the tree
        treePath = findTreePathByWalkingTheTree(value);
        if (treePath != null) {
            return treePath;
        }

        // not found
        return null;
    }

    private TreePath findTreePathByNodeAndToParent(T value) {
        List<Node> rootToNode = new ArrayList<>();
        while (true) {
            // Find the node
            Node node = sTreeCore.getSTreeModel().findNode(value);
            // TODO: toParent
            if (node == null) {
                return null; // path to root not found
            }

            // If found add it to the start of the path
            rootToNode.add(0, node);

            // If it is the root, we're done
            if (Objects.equals(value, root)) {
                return new TreePath(rootToNode.toArray());
            }

            // Try to solve the parent (and in doing so work our way up to the root)
            value = (T)node.parent();
        }
    }

    // Breadth first (not depth)
    private TreePath findTreePathByWalkingTheTree(T value) {

        // init
        List<TreePath> toExamineTreePaths = new ArrayList<>();
        toExamineTreePaths.add(new TreePath(node(root, null)));

        // process
        while (!toExamineTreePaths.isEmpty()) {
            TreePath treePath = toExamineTreePaths.remove(0);
            Node node = (Node)treePath.getLastPathComponent();
            T nodeValue = (T)node.value();

            // If value found
            if (Objects.equals(value, nodeValue)) {
                return treePath;
            }

            // Create more treepaths to examine
            for (T child : determineChildrenOf(nodeValue)) {
                toExamineTreePaths.add(new MyTreePath(treePath, node(child, nodeValue)));
            }
        }
        return null;
    }

    // Only needed to make the MyTreePath(TreePath parent, Object lastPathComponent) constructor accessible
    class MyTreePath extends TreePath {
        public MyTreePath(TreePath parent, Object lastPathComponent) {
            super(parent, lastPathComponent);
        }
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
            Node node = (Node)path.getLastPathComponent();
            selectedItems.add((T)node.value());
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

        // Find the place in the tree that changed and refresh
        // This does not work for changes in children collections: STree.this.treeNodeChanged(node);
        if (SwingUtilities.isEventDispatchThread()) {
            STree.this.treeStructureChanged(node);
        }
        else {
            SwingUtilities.invokeLater(() -> STree.this.treeStructureChanged(node));
        }
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
    private ExceptionHandler exceptionHandler = this::handleException;


    // ===========================================================================
    // FLUENT API

    @Override
    public void setName(String v) {
        super.setName(v);
        sTreeCore.setName(v + ".sTreeCore"); // For tests we need to address the actual list
    }

    public STree<T> rootVisible(boolean value) {
        sTreeCore.setRootVisible(value);
        return this;
    }

    static public <T> STree<T> of(T root) {
        return new STree<T>().root(root);
    }
}
