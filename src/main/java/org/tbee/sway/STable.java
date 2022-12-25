package org.tbee.sway;

import org.tbee.sway.table.STableCore;
import org.tbee.sway.table.STableNavigator;
import org.tbee.sway.table.TableColumn;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.util.List;

// TODO
// - selection and selection mode API
// - sorting (map the row in the table model) GlazedLists?
// - more editors and renderers (LocalDate, etc)
// - better javadoc
// - per cell renderer and editor
// - binding (listen to) list changes
// - pagination
// - filter
// - column reordering (map the column in the table model)
// - column hiding (map the column in the table model)
// - TAB/enter key behavior: skip edit to next editable cell
// - automatically resize row height to keep showing the value
// - tooltips
// - OnFocusStopEditHandler
// - AligningTableHeaderRenderer
// - Resizable rows and columns
// - copy and paste -> get/setValueAtAsString
// - remember column and row sizes, column order, hidden columns, etc until next opening of specific component
// - table header
// - fix known bugs (JXTable) like focus handling
// - support row management; insert and delete rows
//   - automatically add a new row at the end of the table when in the last cell and press enter (ForEdit)
//   - insert / delete keys

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
 * Hint: it would be wise to introduce public constants in SomeBean to hold the property names.
 * <br/>
 * <br/>
 * A more elaborate example:
 * <pre>{@code
 * STable stable = new STable<SomeBean>() //
 *         .column(String.class).title("name RO").valueSupplier(b -> b.getName()).table() // read only
 *         .column(String.class).title("name RW").valueSupplier(SomeBean::getName).valueConsumer(SomeBean::setName).table() // read write
 *         .column(Integer.class).title("distance RW").valueSupplier(SomeBean::getDistance).valueConsumer(SomeBean::setDistance).table() // read write
 *         .column(Integer.class).title("roundtrip RO").valueSupplier(SomeBean::getRoundtrip).table() // derived property, so read only
 *         .columns(SomeBean.class, "name", "distance", "roundtrip") // adds multiple columns
 *         .data(aListOfSomeBeans);
 * }
 * </pre>
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
 *         .columns(SomeBean.class, "name", "distance", "roundtrip") // calls monitorProperty and monitorBean
 *         .data(aListOfSomeBeans);
 * }
 * </pre>
 *
 * @param <TableType>
 */
public class STable<TableType> extends JPanel {

    private final STableCore<TableType> sTable;

    public STable() {

        // Create components
        sTable = new STableCore<TableType>();
        JScrollPane scrollPane = new JScrollPane(sTable);
        STableNavigator tableNavigator = new STableNavigator(sTable);

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(tableNavigator, BorderLayout.SOUTH);
    }

    public STableCore<TableType> sTable() {
        return sTable;
    }

    // ===========================================================================
    // DATA

    /**
     * Set new data to show
     *
     * @param v
     */
    public void setData(List<TableType> v) {
        sTable.setData(v);
    }

    public List<TableType> getData() {
        return sTable.getData();
    }

    public STable<TableType> data(List<TableType> v) {
        setData(v);
        return this;
    }

    /**
     * Stop the edit by either accepting or cancelling
     */
    public void stopEdit() {
        sTable.stopEdit();
    }

    /**
     * Cancel the edit
     */
    public void cancelEdit() {
        sTable.cancelEdit();
    }

    /**
     * Has this table currently a cell with an active editor
     */
    public boolean isEditing() {
        return sTable.isEditing();
    }

    // ===========================================================================
    // COLUMNS

    /**
     * Get the columns
     * @return Unmodifiable list of colums
     */
    public List<TableColumn<TableType, ?>> getColumns() {
        return sTable.getColumns();
    }

    /**
     * Finds (the first!) column with the provided id.
     * @param id
     * @return
     */
    public TableColumn<TableType, ?> findColumnById(String id) {
        return sTable().findColumnById(id);
    }


    /**
     * Append a column
     * @param tableColumn
     * @param <ColumnType>
     */
    public <ColumnType extends Object> void addColumn(TableColumn<TableType, ColumnType> tableColumn) {
        sTable.addColumn(tableColumn);
    }

    /**
     * Remove a column
     * @param tableColumn
     * @return Indicate if a remove actually took place.
     * @param <ColumnType>
     */
    public <ColumnType extends Object> boolean removeColumn(TableColumn<TableType, ColumnType> tableColumn) {
        return sTable.removeColumn(tableColumn);
    }

    /**
     * Add a column. Requires the table() call at the end to continue the fluent API
     * ...column(String.class).title("Property").valueSupplier(d -> d.getProperty())).table()
     * @param type
     * @return
     * @param <ColumnType>
     */
    public <ColumnType extends Object> TableColumn<TableType, ColumnType> column(Class<ColumnType> type) {
        TableColumn<TableType, ColumnType> column = sTable.column(type);
        column.setTable(this);
        return column;
    }

    /**
     * Generate columns based on bean info.
     * ...columns(Bean.class, "property", "anotherProperty")
     *
     * This method will set monitorProperty() on each column, and monitorBean() on this class.
     *
     * @param tableTypeClass
     * @param propertyNames
     * @return
     */    public STable<TableType> columns(Class<TableType> tableTypeClass, String... propertyNames) {
        sTable.columns(tableTypeClass, propertyNames);
        return this;
    }


    // ===========================================================================
    // BINDING

    /**
     * monitorBean
     */
    public void setMonitorBean(Class<TableType> v) {
        sTable.setMonitorBean(v);
    }
    public Class<TableType> getMonitorBean() {
        return sTable.getMonitorBean();
    }
    public STable<TableType> monitorBean(Class<TableType> v) {
        setMonitorBean(v);
        return this;
    }

    // ===========================================================================
    // FLUENT API

    public STable<TableType> name(String v) {
        setName(v);
        sTable.name(v + ".sTable"); // For tests we need to address the actual table
        return this;
    }
}