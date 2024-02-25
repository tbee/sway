package org.tbee.sway;

import com.google.common.base.Splitter;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatAsJavaTextFormat;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.mixin.PropertyChangeListenerMixin;
import org.tbee.sway.support.BeanUtil;
import org.tbee.sway.support.SwayUtil;
import org.tbee.sway.table.FormatCellRenderer;
import org.tbee.sway.table.STableCore;
import org.tbee.sway.table.STableNavigator;
import org.tbee.sway.table.TableColumn;
import org.tbee.util.ClassUtil;
import org.tbee.util.ExceptionUtil;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// TODO
// - column reordering (map the column in the table model)
// - column hiding (map the column in the table model)
// - TAB/enter key behavior: skip edit to next editable cell
// - automatically resize row height to keep showing the value
// - tooltips
// - OnFocusStopEditHandler
// - AligningTableHeaderRenderer
// - Resizable rows and columns
// - more editors and renderers (a.o. based on Format) -> can we create automatic editors and sorting for everything in formatRegistry?                                                                                                                                                 d                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               2221
// - better javadoc
// - per cell renderer and editor
// - binding (listen to) list changes
// - pagination
// - copy and paste -> get/setValueAtAsString
// - remember column and row sizes, column order, hidden columns, etc until next opening of specific component
// - table header
// - fix known bugs (JXTable) like focus handling
// - support row management; insert and delete rows
//   - automatically add a new row at the end of the table when in the last cell and press enter (ForEdit)
//   - insert / delete keys
// - set sort programmatically

/**
 * <h2>Basic usage</h2>
 * This table implements an opinionated way how the table API should look.
 * The basic approach is that the end user does NOT provide a TableModel implementation anymore,
 * but interacts completely via the STable's API.
 * Data is shown using setData(), columns are added using the column() method(s).
 * <br/>
 * <br/>
 * The main focus of Sway is simplicity, the simplest example of this component is:
 * <pre>{@code
 * var sTable = new STable<SomeBean>() //
 *         .columns(SomeBean.class, "name", "distance", "roundtrip") //
 *         .data(aListOfSomeBeans);
 * }
 * </pre>
 * Note: it would be wise to introduce public constants in SomeBean to hold the property names.
 * <br/>
 * <br/>
 * If SomeBean has implemented the JavaBean specification correctly, this will give you a table with:
 * <ul>
 *     <li>A navigation footer</li>
 *     <li>Scrollbars</li>
 *     <li>Automatic refresh of cells</li>
 *     <li>Sorting</li>
 *     <li>Filtering</li>
 *     <li>Copy / paste functionality (Excel compatible)</li>
 *     <li>Pagination (TODO)</li>
 *     <li>Tooltips per cell (TODO)</li>
 *     <li>Column reordering and hiding (TODO)</li>
 *     <li>Resizable rows and columns (TODO)</li>
 *     <li>Automatically sizing row (TODO)</li>
 *     <li>Quick data entry using the enter key (TODO)</li>
 * </ul>
 * <br/>
 * A more elaborate example:
 * <pre>{@code
 * var sTable = new STable<SomeBean>() //
 *         .column(String.class).title("name RO").valueSupplier(b -> b.getName()).table() // read only
 *         .column(String.class).title("name RW").valueSupplier(SomeBean::getName).valueConsumer(SomeBean::setName).table() // read write
 *         .column(Integer.class).title("distance RW").valueSupplier(SomeBean::getDistance).valueConsumer(SomeBean::setDistance).table() // read write
 *         .column(Integer.class).title("roundtrip RO").valueSupplier(SomeBean::getRoundtrip).table() // derived property, so read only
 *         .data(aListOfSomeBeans);
 * }
 * </pre>
 * Note: the columns() method uses Java's BeanInfo class to generate columns similar to the example above.
 * <br/>
 *
 * <h2>Binding</h2>
 * STable allows binding to JavaBeans for automatic cell refresh (only suited for limited amount of data).
 * This means the objects provided as data must implement the JavaBean's interface,
 * most notably addPropertyChangeListener, removePropertyChangeListener, and it should fire appropriate PropertyChangeEvents.
 * STable registers itself to EACH bean in the provided data, and monitors its PropertyChangeEvents.
 * If an PCE comes in, and it involves a column that is displayed, then the corresponding cell is notified as changed
 * (the bean for a fact is present as a row, because STable is registered to it).
 * Because of that, STable will execute the getValue() for that row and column (cell), triggering a refresh.
 * <br/>
 * <br/>
 * This binding only involves refreshing cells, so it is not a real read/write binding.
 * Hence the methods used to configure this are called "monitor", not "bind".
 * This monitoring can be applied to any column, irrespective of the data shown; it only determines when an automatic refresh should be done, it does not influence what data is shown.
 * <br/>
 * Extending the previous example with binding:
 * <pre>{@code
 * var sTable = new STable<SomeBean>() //
 *         .column(String.class).title("name RO")...monitorProperty("name").table() //
 *         .column(String.class).title("name RW")...monitorProperty("name").table() //
 *         .column(Integer.class).title("distance RW")...monitorProperty("distance").table() //
 *         .column(Integer.class).title("roundtrip RO")...monitorProperty("roundtrip").table() //
 *         .monitorBean(SomeBean.class) //
 *         .data(aListOfSomeBeans);
 * }
 * </pre>
 * Note: the columns() method calls monitorBean, and monitorProperty on each column.
 * <br/>
 *
 * <h2>Selection</h2>
 * Selection still has the three modes, but set via an enum, and the listener gets a list of selected items.
 * <pre>{@code
 * var sTable = new STable<SomeBean>() //
 *         .selectionMode(STable.SelectionMode.MULTIPLE) //
 *         .onSelectionChanged(selection -> System.out.println("onSelectionChanged: " + selection)) //
 * }
 * </pre>
 *
 * Selection uses the table type:
 * <pre>{@code
 * var sTable = new STable<SomeBean>() //
 *         .selectionMode(STable.SelectionMode.MULTIPLE) //
 *         .data(aListOfSomeBeans); //
 *
 *  sTable.setSelection(List.of(bean1, bean3, bean12));
 *  List<SomeBean> selection = sTable.getSelection();
 * }
 * </pre>
 * Note: if the selection mode does not match the selection
 * (e.g. selection mode is single, but the to-be-set selection has multiple items),
 * the last possible selection is what is selected (so the last item in the selection will be selected).
 *
 * <h2>Filtering</h2>
 * <pre>{@code
 * var sTable = new STable<SomeBean>() //
 *         .columns(SomeBean.class, "name", "distance", "roundtrip") //
 *         .filterHeaderEnabled(true) // This will show the filter header
 *         .data(aListOfSomeBeans);
 * }
 * </pre>
 *
 * <h2>Bean factory</h2>
 * <pre>{@code
 * var sTable = new STable<SomeBean>() //
 *         .columns(SomeBean.class, "name", "distance", "roundtrip") //
 *         .beanFactory(() -> new SomeBean()) //
 *         .onRowAdded(b -> ...) // fired when a row was added
 *         .data(aListOfSomeBeans);
 * }
 * </pre>
 * If configured, the bean factory allows the table to automatically add new rows.
 * This can be relevant if for example a larger set of data is pasted into the table and rows need to be added at the end.
 *
 * @param <TableType>
 */
public class STable<TableType> extends JPanel implements PropertyChangeListenerMixin<STable<TableType>> {
    static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(STable.class);

    private final STableCore<TableType> sTableCore;

    public STable() {

        // Create components
        sTableCore = new STableCore<TableType>(this);
        JScrollPane scrollPane = new JScrollPane(sTableCore);
        STableNavigator tableNavigator = new STableNavigator(sTableCore);

        // Start listening for the selection
        sTableCore.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                var selectedItems = getSelection();
                if (selectionChangedListeners != null) {
                    selectionChangedListeners.forEach(l -> l.accept(selectedItems));
                }
                firePropertyChange(SELECTION, null, selectedItems);
            }
        });

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(tableNavigator, BorderLayout.SOUTH);
    }

    public STableCore<TableType> getSTableCore() {
        return sTableCore;
    }

    // ===========================================================================
    // DATA

    private List<TableType> data = List.of();


    /**
     *
     * @param v
     */
    public void setData(List<TableType> v) {
    	if (v == null) {
    		throw new IllegalArgumentException("Null not allowed, provide an empty list");
    	}
        unregisterFromAllBeans();
        this.data = new ArrayList<>(v); // We don't allow outside changes to the provided list
        registerToAllBeans();
       sTableCore.getTableModel().fireTableDataChanged();
    }
    public List<TableType> getData() {
        return Collections.unmodifiableList(this.data);
    }
    public STable<TableType> data(List<TableType> v) {
        setData(v);
        return this;
    }

    /**
     * Stop the edit by either accepting or cancelling
     */
    public void stopEdit() {
        if (!isEditing()) {
            return;
        }
        try {
            if (sTableCore.getCellEditor() != null) {
                sTableCore.getCellEditor().stopCellEditing();
            }
        }
        finally {
            cancelEdit();
        }
    }

    /**
     * Cancel the edit
     */
    public void cancelEdit() {
        if (!isEditing()) {
            return;
        }
        if (sTableCore.getCellEditor() != null) {
            sTableCore.getCellEditor().cancelCellEditing();
        }
    }

    /**
     * Has this table currently a cell with an active editor
     */
    public boolean isEditing() {
        return sTableCore.isEditing();
    }

    // =======================================================================
    // COLUMNS

    final private List<TableColumn<TableType, ?>> tableColumns = new ArrayList<>();

    /**
     * Get the columns
     * @return Unmodifiable list of columns
     */
    public List<TableColumn<TableType, ?>> getTableColumns() {
        return Collections.unmodifiableList(tableColumns);
    }

    /**
     * Append a column
     * @param tableColumn
     */
    public void addColumn(TableColumn<TableType, ?> tableColumn) {
        tableColumn.setsTable(this);
        this.tableColumns.add(tableColumn);
        sTableCore.getTableModel().fireTableStructureChanged();
    }

    /**
     * Remove a column
     * @param tableColumn
     * @return Indicate if a remove actually took place.
     */
    public boolean removeColumn(TableColumn<TableType, ?> tableColumn) {
        boolean removed = this.tableColumns.remove(tableColumn);
        sTableCore.getTableModel().fireTableStructureChanged();
        return removed;
    }

    /**
     * Finds (the first!) column with the provided id.
     * Usage:
     * <pre>{@code
     *     ...<ColumType>findColumnById(id)
     * }</pre>
     *
     * @param id
     * @return
     */
    public <ColumnType> TableColumn<TableType, ColumnType> findColumnById(String id) {
        return (TableColumn<TableType, ColumnType>) tableColumns.stream() //
                .filter(tc -> id.equals(tc.getId())) //
                .findFirst().orElse(null);
    }
    /**
     * Add a column. Requires the table() call at the end to continue the fluent API
     * <pre>{@code
     *     ...column(String.class).title("Property").valueSupplier(d -> d.getProperty())).table()
     * }</pre>
     * @param type
     * @return
     * @param <ColumnType>
     */
    public <ColumnType extends Object> TableColumn<TableType, ColumnType> column(Class<ColumnType> type) {
        var tableColumn = new TableColumn<TableType, ColumnType>(type);
        addColumn(tableColumn);
        return tableColumn;
    }


    /**
     * Generate columns based on bean info.
     * <pre>{@code
     *     ...columns(Bean.class, "property", "anotherProperty")
     * }</pre>
     * This method will set monitorProperty() on each column, and monitorBean() on this class.
     *
     * @param tableTypeClass
     * @param propertyNames  the properties to show. If none are specified all discovered properties are show, in an undefined order.
     * @return
     */
    public STable<TableType> columns(Class<TableType> tableTypeClass, String... propertyNames) {
        try {
            // Use Java's bean inspection classes to analyse the bean
            BeanInfo beanInfo = Introspector.getBeanInfo(tableTypeClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            Map<String, PropertyDescriptor> propertyDescriptorsMap = Arrays.stream(propertyDescriptors).collect(Collectors.toMap(pd -> pd.getName(), pd -> pd));

            // If no properties are specified, assume all. Order is undefined.
            if (propertyNames.length == 0) {
                var excludedPropertyNames = List.of("class", "propertyChangeListeners", "vetoableChangeListeners");
                propertyNames = propertyDescriptorsMap.keySet().stream() //
                        .filter(pn -> !excludedPropertyNames.contains(pn)) //
                        .toList().toArray(new String[]{});
            }

            // For each property create a column
            for (String propertyName : propertyNames) {
                PropertyDescriptor propertyDescriptor = propertyDescriptorsMap.get(propertyName);
                if (propertyDescriptor == null) {
                    throw new IllegalArgumentException("Property '" + propertyName + "' not found in bean " + tableTypeClass);
                }

                // Add column
                Class<?> propertyType = ClassUtil.primitiveToClass(propertyDescriptor.getPropertyType());
                column((Class<Object>) propertyType) // It's okay, JTable will still use the appropriate renderer and editor
                        .id(propertyName) //
                        .title(propertyName) // TODO: i18n / translation
                        .valueSupplier(bean -> readFromPropertyDescriptor(propertyDescriptor, bean)) //
                        .valueConsumer((bean,value) -> writeToPropertyDescriptor(propertyDescriptor, bean, value)) //
                        .editable(propertyDescriptor.getWriteMethod() != null) // if there is a write method then it is editable
                        .monitorProperty(propertyName) //
                ;
            }
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }

        // Also start monitor this bean
        monitorBean(tableTypeClass);

        // Done
        return this;
    }

    private void writeToPropertyDescriptor(PropertyDescriptor propertyDescriptor, TableType bean, Object value) {
        try {
            propertyDescriptor.getWriteMethod().invoke(bean, value);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object readFromPropertyDescriptor(PropertyDescriptor propertyDescriptor, TableType bean) {
        try {
            return propertyDescriptor.getReadMethod().invoke(bean);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    // ===========================================================================
    // SELECTION

    enum SelectionMode{ //
        SINGLE(ListSelectionModel.SINGLE_SELECTION), //
        INTERVAL(ListSelectionModel.SINGLE_INTERVAL_SELECTION), //
        MULTIPLE(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

         private int code;
         private SelectionMode(int code) {
             this.code = code;
         }

         static SelectionMode of(int code) {
             for (SelectionMode selectionMode : values()) {
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
    public void setSelectionMode(SelectionMode v) {
        sTableCore.setSelectionMode(v.code);
    }
    public SelectionMode getSelectionMode() {
        return SelectionMode.of(sTableCore.getSelectionModel().getSelectionMode());
    }
    public STable<TableType> selectionMode(SelectionMode v) {
        setSelectionMode(v);
        return this;
    }

    /**
     *
     * @return
     */
    public List<TableType> getSelection() {
        var selectedItems = new ArrayList<TableType>(sTableCore.getSelectionModel().getSelectionMode());
        for (int rowIdx : sTableCore.getSelectionModel().getSelectedIndices()) {
            rowIdx = sTableCore.convertRowIndexToModel(rowIdx);
            selectedItems.add(getData().get(rowIdx));
        }
        return Collections.unmodifiableList(selectedItems);
    }

    /**
     *
     */
    public void setSelection(List<TableType> values) {
        clearSelection();
        List<TableType> data = getData();
        for (TableType value : values) {
            int index = data.indexOf(value);
            int rowIdx = sTableCore.convertRowIndexToView(index);
            sTableCore.getSelectionModel().addSelectionInterval(rowIdx, rowIdx);
        }
    }

    /**
     *
     */
    public void clearSelection() {
         sTableCore.clearSelection();
    }
    final static public String SELECTION = "selection";
    public BindingEndpoint<List<TableType>> selection$() {
        return BindingEndpoint.of(this, SELECTION, exceptionHandler);
    }

    /**
     *
     * @param listener
     */
    synchronized public void addSelectionChangedListener(Consumer<List<TableType>> listener) {
        if (selectionChangedListeners == null) {
            selectionChangedListeners = new ArrayList<>();
        }
        selectionChangedListeners.add(listener);
    }
    synchronized public boolean removeSelectionChangedListener(Consumer<List<TableType>> listener) {
        if (selectionChangedListeners == null) {
            return false;
        }
        return selectionChangedListeners.remove(listener);
    }
    private List<Consumer<List<TableType>>> selectionChangedListeners;

    /**
     * @param onSelectionChangedListener
     * @return
     */
    public STable<TableType> onSelectionChanged(Consumer<List<TableType>> onSelectionChangedListener) {
        addSelectionChangedListener(onSelectionChangedListener);
        return this;
    }



    // ===========================================================================
    // BINDING

    private BeanUtil.PropertyChangeConnector propertyChangeConnector = null;
    private boolean registerToAllBeans = false;

    /**
     *
     */
    public void setMonitorBean(Class<TableType> v) {

        // unregister if already registered
        if (registerToAllBeans) {
            unregisterFromAllBeans();
        }

        // Remember
        monitorBean = v;

        // Find the binding methods
        propertyChangeConnector = BeanUtil.getPropertyChangeConnector(v);
        registerToAllBeans = propertyChangeConnector.isComplete();

        // Register
        registerToAllBeans();
    }
    public Class<TableType> getMonitorBean() {
        return monitorBean;
    }
    private Class<TableType> monitorBean = null;
    public STable<TableType> monitorBean(Class<TableType> v) {
        setMonitorBean(v);
        return this;
    }

    final private PropertyChangeListener beanPropertyChangeListener = evt -> {
        Object evtSource = evt.getSource();

        // Find the bean in the data
        var rowIdxs = new ArrayList<Integer>();
        for (int rowIdx = 0; rowIdx < data.size(); rowIdx++) {
            if (evtSource.equals(data.get(rowIdx))) {
                rowIdxs.add(rowIdx);
            }
        }
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Found bean at row(s) " + rowIdxs);

        // Now loop all columns that are bound
        for (int colIdx = 0; colIdx < getTableColumns().size(); colIdx++) {
            String monitorProperty = getTableColumns().get(colIdx).getMonitorProperty();
            if (monitorProperty != null && monitorProperty.equals(evt.getPropertyName())) {
                for (Integer rowIdx : rowIdxs) {
                    if (LOGGER.isDebugEnabled()) LOGGER.debug("Invoke fireTableCellUpdated(" + rowIdx + "," + colIdx + ")");
                    if (SwingUtilities.isEventDispatchThread()) {
                        getSTableCore().getTableModel().fireTableCellUpdated(rowIdx, colIdx);
                    }
                    else {
                        int colIdxFinal = colIdx;
                        SwingUtilities.invokeLater(() -> getSTableCore().getTableModel().fireTableCellUpdated(rowIdx, colIdxFinal));
                    }
                }
            }
        }
    };

    protected void registerToAllBeans() {
        if (!registerToAllBeans) {
            return;
        }
        for (Object record : data) {
            if (LOGGER.isDebugEnabled()) LOGGER.debug("Register to " + record);
            propertyChangeConnector.register(record, beanPropertyChangeListener);
        }
    }
    protected void unregisterFromAllBeans() {
        if (!registerToAllBeans) {
            return;
        }
        for (Object record : data) {
            if (LOGGER.isDebugEnabled()) LOGGER.debug("Unregister from " + record);
            propertyChangeConnector.unregister(record, beanPropertyChangeListener);
        }
    }


    // ===========================================================================
    // FilterHeader

    /** Filter header used in the table */
    private TableFilterHeader tableFilterHeader = null;

    /**
     *
     * @return Coderazzi's filter header component used in the table or null if never used
     */
    public TableFilterHeader getTableFilterHeader() {
        return tableFilterHeader;
    }

    /**
     * True if filter header is enabled.
     *
     * @return
     */
    public boolean isFilterHeaderEnabled() {
        return tableFilterHeader != null && tableFilterHeader.isEnabled();
    }

    /**
     * Enables or disables (and hides) filter header.
     * @param v True to enable the filter header component
     */
    public void setFilterHeaderEnabled(boolean v) {
        // if filter was off and enabled, create and attach it
        if (v == true && tableFilterHeader == null) {
            tableFilterHeader = new TableFilterHeader(this.sTableCore, AutoChoices.ENABLED);
            setupFilterHeaderRenderers();
        }
        if (tableFilterHeader != null) {
            tableFilterHeader.setVisible(v);
            tableFilterHeader.setEnabled(v);
        }
    }

    private void setupFilterHeaderRenderers() {

        for (TableColumn tableColumn : getTableColumns()) {
            Class columnClass = tableColumn.getType();

            // Try to determine the used format
            Format format = null;
            TableCellRenderer renderer = tableColumn.getRenderer();
            if (format == null && renderer instanceof FormatCellRenderer formatCellRenderer) {
                format = formatCellRenderer.getFormat();
            }
            if (format == null) {
                format = FormatRegistry.findFor(columnClass);
            }
            // If format found, configure it
            if (format != null && tableFilterHeader.getParserModel().getFormat(columnClass) == null) {
                if (LOGGER.isDebugEnabled()) LOGGER.debug("Found format for " + columnClass + ", add renderer for filter header");
                tableFilterHeader.getParserModel().setFormat(columnClass, new FormatAsJavaTextFormat(format));
            }
        }
    }

    /**
     *
     * @param v
     * @return
     */
    public STable<TableType> filterHeaderEnabled(boolean v) {
        setFilterHeaderEnabled(v);
        return this;
    }

    // ===========================================================================
    // RENDERING e.g. alternate row colors

    /** Alternate the background color for rows */
    public void setAlternateRowColor(boolean v) {
        firePropertyChange(ALTERNATEROWCOLOR, this.alternateRowColor, this.alternateRowColor = v);
    }
    public boolean getAlternateRowColor() {
        return alternateRowColor;
    }
    private boolean alternateRowColor = true;
    final static public String ALTERNATEROWCOLOR = "alternateRowColor";
    public STable<TableType> alternateRowColor(boolean v) {
        setAlternateRowColor(v);
        return this;
    }
    public BindingEndpoint<Boolean> alternateRowColor$() {
        return BindingEndpoint.of(this, ALTERNATEROWCOLOR, exceptionHandler);
    }

    /** The color to use for the alternating background color for rows */
    public void setFirstAlternateRowColor(Color v) {
        firePropertyChange(FIRSTALTERNATEROWCOLOR, this.firstAlternateRowColor, this.firstAlternateRowColor = v);
    }
    public Color getFirstAlternateRowColor() {
        return firstAlternateRowColor;
    }
    private Color firstAlternateRowColor = SwayUtil.getFirstAlternateRowColor();
    final static public String FIRSTALTERNATEROWCOLOR = "firstAlternateRowColor";
    public STable<TableType> firstAlternateRowColor(Color v) {
        firstAlternateRowColor(v);
        return this;
    }
    public BindingEndpoint<Color> firstAlternateRowColor$() {
        return BindingEndpoint.of(this, FIRSTALTERNATEROWCOLOR, exceptionHandler);
    }

    /** The second color to use for the alternating background color for rows */
    public void setSecondAlternateRowColor(Color v) {
        firePropertyChange(SECONDALTERNATEROWCOLOR, this.secondAlternateRowColor, this.secondAlternateRowColor = v);
    }
    public Color getSecondAlternateRowColor() {
        return secondAlternateRowColor;
    }
    private Color secondAlternateRowColor = SwayUtil.getSecondAlternateRowColor();
    final static public String SECONDALTERNATEROWCOLOR = "secondAlternateRowColor";
    public STable<TableType> secondAlternateRowColor(Color v) {
        setSecondAlternateRowColor(v);
        return this;
    }
    public BindingEndpoint<Color> secondAlternateRowColor$() {
        return BindingEndpoint.of(this, SECONDALTERNATEROWCOLOR, exceptionHandler);
    }

    /** UneditableCellsShowAsDisabled */
    public void setUneditableCellsShowAsDisabled(boolean v) {
        firePropertyChange(UNEDITABLECELLSSHOWASDISABLED, this.uneditableCellsShowAsDisabled, this.uneditableCellsShowAsDisabled = v);
    }
    public boolean getUneditableCellsShowAsDisabled() {
        return uneditableCellsShowAsDisabled;
    }
    private boolean uneditableCellsShowAsDisabled = true;
    final static public String UNEDITABLECELLSSHOWASDISABLED = "uneditableCellsShowAsDisabled";
    public STable<TableType> uneditableCellsShowAsDisabled(boolean v) {
        setUneditableCellsShowAsDisabled(v);
        return this;
    }
    public BindingEndpoint<Boolean> uneditableCellsShowAsDisabled$() {
        return BindingEndpoint.of(this, UNEDITABLECELLSSHOWASDISABLED, exceptionHandler);
    }

    /** DisabledTableShowsCellsAsDisabled */
    public void setDisabledTableShowsCellsAsDisabled(boolean v) {
        firePropertyChange(DISABLEDTABLESHOWSCELLSASDISABLED, this.disabledTableShowsCellsAsDisabled, this.disabledTableShowsCellsAsDisabled = v);
    }
    public boolean getDisabledTableShowsCellsAsDisabled() {
        return disabledTableShowsCellsAsDisabled;
    }
    private boolean disabledTableShowsCellsAsDisabled = true;
    final static public String DISABLEDTABLESHOWSCELLSASDISABLED = "disabledTableShowsCellsAsDisabled";
    public STable<TableType> disabledTableShowsCellsAsDisabled(boolean v) {
        setDisabledTableShowsCellsAsDisabled(v);
        return this;
    }
    public BindingEndpoint<Boolean> disabledTableShowsCellsAsDisabled$() {
        return BindingEndpoint.of(this, DISABLEDTABLESHOWSCELLSASDISABLED, exceptionHandler);
    }

    /** Editable */
    public void setEditable(boolean v) {
        editable = v;
        repaint();
    }
    public boolean isEditable() {
        return editable;
    }
    private boolean editable = true;
    public STable<TableType> editable(boolean v) {
        setEditable(v);
        return this;
    }

    /** UneditableTableShowsCellsAsDisabled */
    public void setUneditableTableShowsCellsAsDisabled(boolean v) {
        firePropertyChange(UNEDITABLETABLESHOWSCELLSASDISABLED, this.uneditableTableShowsCellsAsDisabled, this.uneditableTableShowsCellsAsDisabled = v);
    }
    public boolean getUneditableTableShowsCellsAsDisabled() {
        return uneditableTableShowsCellsAsDisabled;
    }
    private boolean uneditableTableShowsCellsAsDisabled = true;
    final static public String UNEDITABLETABLESHOWSCELLSASDISABLED = "uneditableTableShowsCellsAsDisabled";
    public STable<TableType> uneditableTableShowsCellsAsDisabled(boolean v) {
        setUneditableTableShowsCellsAsDisabled(v);
        return this;
    }
    public BindingEndpoint<Boolean> uneditableTableShowsCellsAsDisabled$() {
        return BindingEndpoint.of(this, UNEDITABLETABLESHOWSCELLSASDISABLED, exceptionHandler);
    }

    /** must repaint because cells may be shown disabled */
    public void setEnabled(boolean v) {
        super.setEnabled(v);
        repaint();
    }

    // ===========================================================================
    // SORTING

    public void cancelSorting() {
        sTableCore.getRowSorter().setSortKeys(null);
    }

    // ===========================================================================
    // LIST MANIPULATION

	/** the table supports inserting of rows */
	public void setAllowInsertRows(boolean v) { 
        firePropertyChange(ALLOWINSERTROWS, this.allowInsertRows, this.allowInsertRows = v);
	}
	public boolean getAllowInsertRows() { 
		return allowInsertRows; 
	}
    final static public String ALLOWINSERTROWS = "allowInsertRows";
	private boolean allowInsertRows = true; // we allow per default this behavior
    public STable<TableType> allowInsertRows(boolean v) {
    	setAllowInsertRows(v);
        return this;
    }
    public BindingEndpoint<Boolean> allowInsertRows$() {
        return BindingEndpoint.of(this, ALLOWINSERTROWS, exceptionHandler);
    }
	public boolean checkAllowInsertRows() {
		return getAllowInsertRows() && isEnabled() && isEditable();
	}

	/** the table supports deleting of rows */
	public void setAllowDeleteRows(boolean v) { 
        firePropertyChange(ALLOWDELETEROWS, this.allowDeleteRows, this.allowDeleteRows = v);
	}
	public boolean getAllowDeleteRows() { 
		return allowDeleteRows; 
	}
	private boolean allowDeleteRows = true; // we allow per default this behavior
    final static public String ALLOWDELETEROWS = "allowDeleteRows";
    public STable<TableType> allowDeleteRows(boolean v) {
    	setAllowDeleteRows(v);
        return this;
    }
    public BindingEndpoint<Boolean> allowDeleteRows$() {
        return BindingEndpoint.of(this, ALLOWDELETEROWS, exceptionHandler);
    }
	public boolean checkAllowDeleteRows() {
		return getAllowDeleteRows() && isEnabled() && isEditable();
	}
	
	/** the table supports inserting of rows */
	public void setConfirmDeleteRows(Function<List<TableType>, Boolean> v) { 
        firePropertyChange(CONFIRMDELETEROWS, this.confirmDeleteRows, this.confirmDeleteRows = v);
	}
	public Function<List<TableType>, Boolean> getConfirmDeleteRows() { 
		return confirmDeleteRows; 
	}
	private Function<List<TableType>, Boolean> confirmDeleteRows = (beans) -> {
		// TBEERNOT internationalization
		return JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(this), "Delete rows?", "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;

	}; // delete without confirmation
    final static public String CONFIRMDELETEROWS = "confirmDeleteRows";
    public STable<TableType> confirmDeleteRows(Function<List<TableType>, Boolean> v) {
    	setConfirmDeleteRows(v);
        return this;
    }
    public BindingEndpoint<Boolean> confirmDeleteRows$() {
        return BindingEndpoint.of(this, CONFIRMDELETEROWS, exceptionHandler);
    }


	/** BeanFactory */
    public void setBeanFactory(Supplier<TableType> v) {
        firePropertyChange(BEANFACTORY, this.beanFactory, this.beanFactory = v);
    }
    public Supplier<TableType> getBeanFactory() {
        return beanFactory;
    }
    private Supplier<TableType> beanFactory = null;
    final static public String BEANFACTORY = "beanFactory";
    public STable<TableType> beanFactory(Supplier<TableType> v) {
        setBeanFactory(v);
        return this;
    }
    public BindingEndpoint<Supplier<TableType>> beanFactory$() {
        return BindingEndpoint.of(this, CONFIRMDELETEROWS, exceptionHandler);
    }

    /**
     * Add a new row to the list.
     * This will use the beanFactory, call rowAdded listeners, and finally calls the provided callback (if not null).
     * The row in the callback is the index in the view (table)
     *
     * @return row index where the row was added or -1 if insert if not allowed or possible
     */
    public int appendRow(BiConsumer<TableType, Integer> callback) {
    	if (!checkAllowInsertRows()) {
    		return -1;
    	}
        if (beanFactory == null) {
            throw new IllegalStateException("BeanFactory must be provided");
        }
        
        // To make sure the row is at the end
        // TBEERNOT: replace this with a appendedRow logic in the datamode: appendedRow stays the last row until a new row is appended. GhostRow becomes appended row upon first PCE.
        cancelSorting();

        // Create and add bean
        TableType bean = beanFactory.get();
        data.add(bean);
        sTableCore.getTableModel().fireTableDataChanged();
        int rowIdx = data.size() - 1;
		rowIdx = getSTableCore().convertRowIndexToView(rowIdx);
        fireRowAdded(bean, rowIdx);
        if (callback != null) {
        	callback.accept(bean, rowIdx);
        }
        return rowIdx;
    }
    /**
     * Add a new row to the list.
     * This will use the beanFactory and call rowAdded listeners.
     * The row is the index in the view (table)
     *
     * @return row index where the row was added or -1 if insert if not allowed or possible
     */
    public int appendRow() {
    	return appendRow(null);
    }
    

	/**
	 * Delete all selected rows.
     * The row in the callback is the index in the view (table)
	 * Removes row from list one by one, and calls rowDeleted listeners and the callback (if not null) for each.
	 */
	public void deleteSelectedRows(BiConsumer<TableType, Integer> callback) {
		// allowed?
		if (!checkAllowDeleteRows()) {
			return;
		}

		// confirm
		List<TableType> selection = getSelection();
		if (selection.isEmpty()) {
			return;
		}
		if (!confirmDeleteRows.apply(selection)) {
			return;
		}
		
		// delete
		selection.forEach(bean -> {
			int rowIdx = data.indexOf(bean);
			rowIdx = getSTableCore().convertRowIndexToView(rowIdx);
			data.remove(bean);
			if (callback != null) {
				callback.accept(bean, rowIdx);
			}
			fireRowDeleted(bean, rowIdx);
		});
        sTableCore.getTableModel().fireTableDataChanged();
	}
	/**
	 * Delete all selected rows.
	 * Removes row from list one by one, and calls rowDeleted listeners for each.
	 */
	public void deleteSelectedRows() {
		deleteSelectedRows(null);
	}
	
    /**
     * The row in the callback is the index in the view (table)
     * @param listener
     */
    synchronized public void addRowAddedListener(BiConsumer<TableType, Integer> listener) {
        if (rowAddedListeners == null) {
            rowAddedListeners = new ArrayList<>();
        }
        rowAddedListeners.add(listener);
    }
    synchronized public boolean removeRowAddedListener(BiConsumer<TableType, Integer> listener) {
        if (rowAddedListeners == null) {
            return false;
        }
        return rowAddedListeners.remove(listener);
    }
    private List<BiConsumer<TableType, Integer>> rowAddedListeners;
    public STable<TableType> onRowAdded(BiConsumer<TableType, Integer> onRowAddedListener) {
        addRowAddedListener(onRowAddedListener);
        return this;
    }
    private void fireRowAdded(TableType bean, int rowIdx) {
        if (rowAddedListeners == null) {
            return;
        }
        rowAddedListeners.forEach(l -> l.accept(bean, rowIdx));
    }


    /**
     * The row is the index in data, not in the table
     * @param listener
     */
    synchronized public void addRowDeletedListener(BiConsumer<TableType, Integer> listener) {
        if (rowDeletedListeners == null) {
            rowDeletedListeners = new ArrayList<>();
        }
        rowDeletedListeners.add(listener);
    }
    synchronized public boolean removeRowDeletedListener(BiConsumer<TableType, Integer> listener) {
        if (rowDeletedListeners == null) {
            return false;
        }
        return rowDeletedListeners.remove(listener);
    }
    private List<BiConsumer<TableType, Integer>> rowDeletedListeners;
    public STable<TableType> onRowDeleted(BiConsumer<TableType, Integer> onRowDeletedListener) {
        addRowDeletedListener(onRowDeletedListener);
        return this;
    }
    private void fireRowDeleted(TableType bean, int rowIdx) {
        if (rowDeletedListeners == null) {
            return;
        }
        rowDeletedListeners.forEach(l -> l.accept(bean, rowIdx));
    }

    // ===========================================================================
    // CLIPBOARD
    
    static final String FIELD_SEPARATOR = "\t";
    static final String RECORD_SEPARATOR = "\n";

    public void copy() {

        // get the range to copy into
        int selectedRowCnt = sTableCore.getSelectedRowCount();
        int selectedColCnt = sTableCore.getSelectedColumnCount();
        int[] selectedRows = sTableCore.getSelectedRows();
        int[] selectedCols = sTableCore.getSelectedColumns();

        // if row selection is not allowed, emulate
        if (!sTableCore.getRowSelectionAllowed()) {
            selectedRowCnt = sTableCore.getRowCount();
            selectedRows = new int[selectedRowCnt];
            for (int i = 0; i < selectedRowCnt; i++) selectedRows[i] = i;
        }

        // if column selection is not allowed, emulate
        if (!sTableCore.getColumnSelectionAllowed()) {
            selectedColCnt = sTableCore.getColumnCount();
            selectedCols = new int[selectedColCnt];
            for (int i = 0; i < selectedColCnt; i++) selectedCols[i] = i;
        }

        // create the copy
        String s = (selectedColCnt <= 0 || selectedRowCnt <= 0 ? "" : copy(sTableCore, selectedRows, selectedCols));

        // dump in system clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(s);
        clipboard.setContents(stringSelection, stringSelection);
    }


    /**
     * The method actually generating the copy string that is placed on the clipboard
     */
    private String copy(STableCore<?> table, int[] selectedRows, int[] selectedCols) {

        // copy from all selected rows
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < selectedRows.length; i++) {

            // copy from all selected columns
            for (int j = 0; j < selectedCols.length; j++) {

                // convert value
                String value = table.getTableModel().getValueAtAsString(selectedRows[i], selectedCols[j]);

                // field
                if (LOGGER.isDebugEnabled()) LOGGER.debug("copy from table cell " + selectedRows[i] + "," + selectedCols[j] + ": " + value);
                stringBuffer.append( value );

                // field separator
                if (j < selectedCols.length - 1) stringBuffer.append(FIELD_SEPARATOR);
            }
            // line separator
            if (i < selectedRows.length - 1) stringBuffer.append(RECORD_SEPARATOR);
        }
        return stringBuffer.toString();
    }

    public void paste() {

        try {
            // get data and start position
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            int[] selectedRows = sTableCore.getSelectedRows();
            int[] selectedCols = sTableCore.getSelectedColumns();

            // get data
            String clipboardContents = (String)(clipboard.getContents(sTableCore).getTransferData(DataFlavor.stringFlavor));

            // if row selection is not allowed, emulate
            if (!sTableCore.getRowSelectionAllowed()) {
                int selectedRowCnt = sTableCore.getRowCount();
                selectedRows = new int[selectedRowCnt];
                for (int i = 0; i < selectedRowCnt; i++) selectedRows[i] = i;
            }
            // if column selection is not allowed, emulate
            if (!sTableCore.getColumnSelectionAllowed()) {
                int lSelectedColCnt = sTableCore.getColumnCount();
                selectedCols = new int[lSelectedColCnt];
                for (int i = 0; i < lSelectedColCnt; i++) selectedCols[i] = i;
            }

            // do the actual paste logic
            paste(sTableCore, selectedRows, selectedCols, clipboardContents);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * The method actually processing the copy string
     */
    private void paste(STableCore sTableCore, int[] selectedRows, int[] selectedCols, String clipboardContents) {
        boolean startedInLastRow = (selectedRows.length == 0 || selectedRows[selectedRows.length - 1] == sTableCore.getRowCount() - 1);

        // TBEERNOT?
        // disable sorting
        // doing this on sorted tables works... almost
        // the addRowAt will reapply sorting, but the setValues do not.
        // And we don't want that, because since the sorting is done by a model but we talk in sTableCore row indexes, the setValueAt may make the row move to another sTableCore index
        sTableCore.getSTable().cancelSorting();

        // split into rows
        String[] clipboardRows = Splitter.on(RECORD_SEPARATOR).splitToList(clipboardContents).toArray(new String[]{});
        for (int i = 0; i < clipboardRows.length; i++)
        {
            // get single row
            String clipboardRow = clipboardRows[i];
            if (LOGGER.isDebugEnabled()) LOGGER.debug("pasting row " + i + ": " + clipboardRow);

            // determine the row to paste in
            int rowIdx = (i < selectedRows.length ? selectedRows[i] : -1);

            // not enough rows but add rows allowed
            if (rowIdx < 0 && startedInLastRow && sTableCore.getSTable().getAllowInsertRows()) {
                rowIdx = sTableCore.getSTable().appendRow();
            }
            if (rowIdx < 0) {
                if (LOGGER.isDebugEnabled()) LOGGER.debug("skipping cell");
                continue;
            }
            if (LOGGER.isDebugEnabled()) LOGGER.debug("pasting to sTableCore row " + rowIdx);

            // split into columns (and thus individual cells)
            String[] lClipboardCols = Splitter.on(FIELD_SEPARATOR).splitToList(clipboardRow).toArray(new String[]{});
            for (int j = 0; j < lClipboardCols.length; j++) {

                // get cell value
                String value = lClipboardCols[j];
                if (LOGGER.isDebugEnabled()) LOGGER.debug("pasting from " + i + "," + j + ": " + value);

                // determine the column to paste in
                int colIdx = ( j < selectedCols.length ? selectedCols[j] : -1);
                if (colIdx < 0) {
                    if (LOGGER.isDebugEnabled()) LOGGER.debug("skipping cell");
                    continue;
                }
                if (LOGGER.isDebugEnabled()) LOGGER.debug("paste to sTableCore cell " + rowIdx + "," + colIdx + ": " + value);

                // if value location
                if ( rowIdx < sTableCore.getModel().getRowCount() // if we use the sTableCore.getRowCount() we get the filtered amount
                  && colIdx < sTableCore.getColumnCount()
                  && sTableCore.isCellEditable(rowIdx, colIdx)) {

                    // write value TBEERNOT: view to model mapping
                    sTableCore.getTableModel().setValueAtAsString(value, rowIdx, colIdx);
                }
            }
        }
    }
    
    public void cut() {
    	copy();
    	deleteSelectedRows();
    }

	// ===========================================================================
	// Preferences

	/** 
	 * preferencesId; used to store the setup (like column widths) of this table.
	 * It should be unique within the whole application, so some combination of a screen id, and table-within-screen.
	 * Should be the last call to be made, so the implicit "restorePreferences()" has stuff to restore. 
	 */
	public void setPreferencesId(String value) {
		firePropertyChange(PREFERENCESID, preferencesId, preferencesId = value);
		restorePreferences(); // TBEERNOT can we move this to some first paint moment? 
	}
	public String getPreferencesId() { 
		return preferencesId; 
	}
	private String preferencesId = null;
    final static public String PREFERENCESID = "preferencesId";
	public STable<TableType> preferencesId(String value) { 
		setPreferencesId(value); 
		return this; 
	}
    public BindingEndpoint<String> preferencesId$() {
        return BindingEndpoint.of(this, PREFERENCESID, exceptionHandler);
    }

    /**
     * save all preferences
     */
    public void savePreferences() {
    	sTableCore.savePreferences();
    }

    /**
     * restore all preferences
     */
    public void restorePreferences() {
    	sTableCore.restorePreferences();
    }


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
    public STable<TableType> exceptionHandler(ExceptionHandler v) {
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

    static public <TableType> STable<TableType> of() {
        return new STable<TableType>();
    }

    static public <TableType> STable<TableType> of(List<TableType> data) {
        return new STable<TableType>().data(data);
    }

    @Override
    public void setName(String v) {
        super.setName(v);
        sTableCore.setName(v + ".sTableCore"); // For tests we need to address the actual table
    }
    public STable<TableType> name(String v) {
        setName(v);
        return this;
    }

    public STable<TableType> visible(boolean value) {
        setVisible(value);
        return this;
    }

    public STable<TableType> withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        return this;
    }
    public STable<TableType> withPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        return this;
    }

    public STable<TableType> overlayWith(Component overlayComponent) {
        SOverlayPane.overlayWith(this, overlayComponent);
        return this;
    }
    public STable<TableType> removeOverlay(Component overlayComponent) {
        SOverlayPane.removeOverlay(this, overlayComponent);
        return this;
    }

    /**
     * Binds to the default property 'selection'
     */
    public STable<TableType> bindTo(BindingEndpoint<List<TableType>> bindingEndpoint) {
        selection$().bindTo(bindingEndpoint);
        return this;
    }

    /**
     * Binds to the default property 'selection'.
     * Binding in this way is not type safe!
     */
    public STable<TableType> bindTo(Object bean, String propertyName) {
        return bindTo(BindingEndpoint.of(bean, propertyName));
    }

    /**
     * Binds to the default property 'selection'.
     * Binding in this way is not type safe!
     */
    public STable<TableType> bindTo(BeanBinder<?> beanBinder, String propertyName) {
        return bindTo(BindingEndpoint.of(beanBinder, propertyName));
    }
}