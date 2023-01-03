package org.tbee.sway;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatAsJavaTextFormat;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.support.SwayUtil;
import org.tbee.sway.table.FormatCellRenderer;
import org.tbee.sway.table.STableCore;
import org.tbee.sway.table.STableNavigator;
import org.tbee.sway.table.TableColumn;
import org.tbee.util.ClassUtil;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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
 *     <li>Pagination (TODO)</li>
 *     <li>Tooltips per cell (TODO)</li>
 *     <li>Copy / paste functionality (TODO)</li>
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
 * @param <TableType>
 */
public class STable<TableType> extends SBorderPanel {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STable.class);

    private final STableCore<TableType> sTableCore;

    public STable() {

        // Create components
        sTableCore = new STableCore<TableType>(this);
        JScrollPane scrollPane = new JScrollPane(sTableCore);
        STableNavigator tableNavigator = new STableNavigator(sTableCore);

        // Layout
        center(scrollPane);
        south(tableNavigator);
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
        unregisterFromAllBeans();
        this.data = Collections.unmodifiableList(v); // We don't allow outside changes to the provided list
        registerToAllBeans();
       sTableCore.getTableModel().fireTableDataChanged();
    }
    public List<TableType> getData() {
        return this.data;
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
     * @param <ColumnType>
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

    /**
     *
     * @param listener
     */
    synchronized public void addSelectionChangedListener(Consumer<List<TableType>> listener) {
        if (selectionChangedListeners == null) {
            selectionChangedListeners = new ArrayList<>();

            // Start listening
            sTableCore.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    var selectedItems = getSelection();
                    selectionChangedListeners.forEach(l -> l.accept(selectedItems));
                }
            });
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

    private Method addPropertyChangeListenerMethod = null;
    private Method removePropertyChangeListenerMethod = null;
    private boolean boundToBean = false;

    /**
     * bindToBean
     */
    public void setMonitorBean(Class<TableType> v) {

        // unregister if already registered
        if (monitorBean != null) {
            unregisterFromAllBeans();
        }

        // Remember
        monitorBean = v;

        // Find the binding methods
        addPropertyChangeListenerMethod = null;
        removePropertyChangeListenerMethod = null;
        if (monitorBean != null) {
            try {
                addPropertyChangeListenerMethod = monitorBean.getMethod("addPropertyChangeListener", new Class<?>[]{PropertyChangeListener.class});
            }
            catch (NoSuchMethodException e) {
                // ignore silently throw new RuntimeException(e);
            }
            try {
                removePropertyChangeListenerMethod = monitorBean.getMethod("removePropertyChangeListener", new Class<?>[]{PropertyChangeListener.class});
            }
            catch (NoSuchMethodException e) {
                // ignore silently throw new RuntimeException(e);
            }
            boundToBean = (addPropertyChangeListenerMethod != null && removePropertyChangeListenerMethod != null);
        }

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
        if (logger.isDebugEnabled()) logger.debug("Found bean at row(s) " + rowIdxs);

        // Now loop all columns that are bound
        for (int colIdx = 0; colIdx < getTableColumns().size(); colIdx++) {
            String monitorProperty = getTableColumns().get(colIdx).getMonitorProperty();
            if (monitorProperty != null && monitorProperty.equals(evt.getPropertyName())) {
                for (Integer rowIdx : rowIdxs) {
                    if (logger.isDebugEnabled()) logger.debug("Invoke fireTableCellUpdated(" + rowIdx + "," + colIdx + ")");
                    getSTableCore().getTableModel().fireTableCellUpdated(rowIdx, colIdx);
                }
            }
        }
    };

    protected void registerToAllBeans() {
        if (!boundToBean) {
            return;
        }
        for (Object record : data) {
            try {
                if (logger.isDebugEnabled()) logger.debug("Register to " + record);
                addPropertyChangeListenerMethod.invoke(record, beanPropertyChangeListener);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
    protected void unregisterFromAllBeans() {
        if (!boundToBean) {
            return;
        }
        for (Object record : data) {
            try {
                if (logger.isDebugEnabled()) logger.debug("Unregister from " + record);
                removePropertyChangeListenerMethod.invoke(record, beanPropertyChangeListener);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
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
                if (logger.isDebugEnabled()) logger.debug("Found format for " + columnClass + ", add renderer for filter header");
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

    /** must repaint because cells may be shown disabled */
    public void setEnabled(boolean v) {
        super.setEnabled(v);
        repaint();
    }


    // ===========================================================================
    // FLUENT API

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
}