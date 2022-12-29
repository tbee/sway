package org.tbee.sway;

import org.tbee.sway.table.STableCore;
import org.tbee.sway.table.STableNavigator;
import org.tbee.sway.table.TableColumn;
import org.tbee.util.ClassUtil;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// TODO
// - filter
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
 * STable stable = new STable<SomeBean>() //
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
 *     <li>Sorting (TODO)</li>
 *     <li>Filtering (TODO)</li>
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
 * STable stable = new STable<SomeBean>() //
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
 * STable stable = new STable<SomeBean>() //
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
 * STable stable = new STable<SomeBean>() //
 *         .selectionMode(STable.SelectionMode.MULTIPLE) //
 *         .onSelectionChanged(selection -> System.out.println("onSelectionChanged: " + selection)) //
 * }
 * </pre>
 *
 * Selection uses the table type:
 * <pre>{@code
 * STable stable = new STable<SomeBean>() //
 *         .selectionMode(STable.SelectionMode.MULTIPLE) //
 *         .data(aListOfSomeBeans); //
 *
 *  stable.setSelection(List.of(bean1, bean3, bean12));
 *  List<SomeBean> selection = stable.getSelection();
 * }
 * </pre>
 * Note: if the selection mode does not match the selection
 * (e.g. selection mode is single, but the to-be-set selection has multiple items),
 * the last possible selection is what is selected (so the last item in the selection will be selected).
 *
 * @param <TableType>
 */
public class STable<TableType> extends JPanel {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STable.class);

    private final STableCore<TableType> sTableCore;

    public STable() {

        // Create components
        sTableCore = new STableCore<TableType>();
        JScrollPane scrollPane = new JScrollPane(sTableCore);
        STableNavigator tableNavigator = new STableNavigator(sTableCore);

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(tableNavigator, BorderLayout.SOUTH);
    }

    public STableCore<TableType> sTable() {
        return sTableCore;
    }

    // ===========================================================================
    // DATA

    /**
     * Set new data to show
     *
     * @param v
     */
    public void setData(List<TableType> v) {
        sTableCore.setData(v);
    }

    public List<TableType> getData() {
        return sTableCore.getData();
    }

    public STable<TableType> data(List<TableType> v) {
        setData(v);
        return this;
    }

    /**
     * Stop the edit by either accepting or cancelling
     */
    public void stopEdit() {
        sTableCore.stopEdit();
    }

    /**
     * Cancel the edit
     */
    public void cancelEdit() {
        sTableCore.cancelEdit();
    }

    /**
     * Has this table currently a cell with an active editor
     */
    public boolean isEditing() {
        return sTableCore.isEditing();
    }

    // ===========================================================================
    // COLUMNS

    /**
     * Get the columns
     * @return Unmodifiable list of colums
     */
    public List<TableColumn<TableType, ?>> getColumns() {
        return sTableCore.getColumns();
    }

    /**
     * Finds (the first!) column with the provided id.
     * Usage: ...<ColumType>findColumnById(id)
     * @param id
     * @return
     */
    public <ColumnType> TableColumn<TableType, ColumnType> findColumnById(String id) {
        return sTable().findColumnById(id);
    }


    /**
     * Append a column
     * @param tableColumn
     * @param <ColumnType>
     */
    public <ColumnType extends Object> void addColumn(TableColumn<TableType, ColumnType> tableColumn) {
        sTableCore.addColumn(tableColumn);
    }

    /**
     * Remove a column
     * @param tableColumn
     * @return Indicate if a remove actually took place.
     * @param <ColumnType>
     */
    public <ColumnType extends Object> boolean removeColumn(TableColumn<TableType, ColumnType> tableColumn) {
        return sTableCore.removeColumn(tableColumn);
    }

    /**
     * Add a column. Requires the table() call at the end to continue the fluent API
     * ...column(String.class).title("Property").valueSupplier(d -> d.getProperty())).table()
     * @param type
     * @return
     * @param <ColumnType>
     */
    public <ColumnType extends Object> TableColumn<TableType, ColumnType> column(Class<ColumnType> type) {
        TableColumn<TableType, ColumnType> column = sTableCore.column(type);
        column.setTable(this);
        return column;
    }

    /**
     * Generate columns based on bean info.
     * ...columns(Bean.class, "property", "anotherProperty")
     * <p>
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
                propertyNames = propertyDescriptorsMap.keySet() //
                        .stream().filter(pn -> !excludedPropertyNames.contains(pn)) //
                        .toList().toArray(new String[]{});
            }

            // For each property create a column
            for (String propertyName : propertyNames) {
                PropertyDescriptor propertyDescriptor = propertyDescriptorsMap.get(propertyName);
                if (propertyDescriptor == null) {
                    throw new IllegalArgumentException("Property '" + propertyName + "' not found in bean " + tableTypeClass);
                }

                // Handle primitive types
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                if (propertyType.isPrimitive()) {
                    propertyType = ClassUtil.primitiveToClass(propertyType);
                }

                // Add column
                column((Class<Object>) propertyType) // It's okay, JTable will still use the appropriate renderer and editor
                        .id(propertyName) //
                        .title(propertyName) //
                        .valueSupplier(bean -> {
                            try {
                                return propertyDescriptor.getReadMethod().invoke(bean);
                            }
                            catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                            catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }) //
                        .valueConsumer((bean,value) -> {
                            try {
                                propertyDescriptor.getWriteMethod().invoke(bean, value);
                            }
                            catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                            catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }) //
                        .editable(propertyDescriptor.getWriteMethod() != null) // if there is a write method then it is editable
                        .monitorProperty(propertyName) //
                ;
            }
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        monitorBean(tableTypeClass);
        return this;
    }


    // ===========================================================================
    // SELECTION

    enum SelectionMode{ SINGLE(ListSelectionModel.SINGLE_SELECTION), INTERVAL(ListSelectionModel.SINGLE_INTERVAL_SELECTION), MULTIPLE(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
     * @return
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

    /**
     * monitorBean
     */
    public void setMonitorBean(Class<TableType> v) {
        sTableCore.setMonitorBean(v);
    }
    public Class<TableType> getMonitorBean() {
        return sTableCore.getMonitorBean();
    }
    public STable<TableType> monitorBean(Class<TableType> v) {
        setMonitorBean(v);
        return this;
    }

    // ===========================================================================
    // FLUENT API

    public STable<TableType> name(String v) {
        setName(v);
        sTableCore.name(v + ".sTableCore"); // For tests we need to address the actual table
        return this;
    }

    public STable<TableType> visible(boolean value) {
        setVisible(value);
        return this;
    }
}