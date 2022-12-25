package org.tbee.sway;

import org.tbee.sway.support.SwayUtil;
import org.tbee.sway.table.TableColumn;
import org.tbee.sway.table.TableModel;
import org.tbee.sway.table.UseTableCellEditorAsTableCellRenderer;

import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// TODO
// - sorting (map the row in the table model) GlazedLists?
// - more editors and renderers (LocalDate, etc)
// - better javadoc
// - per cell renderer and editor
// - binding (listen to) list changes
// - pagination
// - filter
// - default footer showing active row/col etc -> STableAIO
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
// - make separate component and per default wrap in JScrollPane -> STableAIO with footer, scrollpane, etc

/**
 * <h2>Basic usage</h2>
 * This table implements an opinionated way how the table API should look.
 * The basic approach is that the end user does NOT provide a TableModel implementation anymore,
 * but interacts completely via the STable's API.
 * Data is shown using setData(), columns are added using the column() method(s).
 * <br/>
 * <br/>
 * Example:
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
 * (the bean is present as a row, because STable is registered to it).
 * Because of that, STable will execute the getValue() for that row and column (cell), triggering a refresh.
 * <br/>
 * <br/>
 * This binding only involves refreshing cells, so it is not a real read/write binding.
 * Hence the methods used to configure this are called "monitor", not "bind".
 * These monitoring can be applied to any column, irrespective of the data shown; it only determines when an automatic refresh should be done, it does not influence what data shuold be shown.
 * <br/>
 * Extending the previous example with binding:
 * <pre>{@code
 * STable stable = new STable<SomeBean>() //
 *         .column(String.class).title("name RO")...monitorProperty("name").table() //
 *         .column(String.class).title("name RW")...monitorProperty("name").table() //
 *         .column(Integer.class).title("distance RW")...monitorProperty("distance").table() //
 *         .column(Integer.class).title("roundtrip RO")...monitorProperty("roundtrip").table() //
 *         .columns(SomeBean.class, "name", "distance", "roundtrip") // automatically do a monitorProperty
 *         .monitorBean(SomeBean.class) // this activates the monitoring of all the beans in the data
 *         .data(aListOfSomeBeans);
 * }
 * </pre>
 * Hint: it would be wise to introduce public constants in SomeBean to hold the property names.
 * <br/>
 * <br/>
 * Note: STable should still be wrapped in a JScrollPane.
 *
 * @param <TableType>
 */
public class STable<TableType> extends javax.swing.JTable {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STable.class);

    public final static Map<Class<?>, Class<?>> primitiveToClassMap = new HashMap<>();
    static {
        primitiveToClassMap.put(boolean.class, Boolean.class);
        primitiveToClassMap.put(byte.class, Byte.class);
        primitiveToClassMap.put(short.class, Short.class);
        primitiveToClassMap.put(char.class, Character.class);
        primitiveToClassMap.put(int.class, Integer.class);
        primitiveToClassMap.put(long.class, Long.class);
        primitiveToClassMap.put(float.class, Float.class);
        primitiveToClassMap.put(double.class, Double.class);
    }

    public STable() {
        super(new TableModel<TableType>());
        construct();
    }

    private void construct()
    {
        // "Sets whether editors in this JTable get the keyboard focus when an editor is activated as a result of the JTable forwarding keyboard events for a cell."
        // "By default, this property is false, and the JTable retains the focus unless the cell is clicked."
        super.setSurrendersFocusOnKeystroke(true);

        // upon keypress the editor is started automatically
        // note: this behaviour deviates slightly from the normal behaviour
        //       for example: pressing F2 first fires a "focus gained"
        //                    typing directly first processes the character
        putClientProperty("JTable.autoStartsEdit", Boolean.TRUE);

        // default row height
        if (UIManager.get("Table.RowHeight") != null) {
            setRowHeight(UIManager.getInt("Table.RowHeight"));
        }

        // per default fill the viewport
        setFillsViewportHeight(true);

        // per default the table must have cellspacing, which means a visible grid (Nimbus does not)
        if (getIntercellSpacing().width < 1 || getIntercellSpacing().height < 1) {
            setIntercellSpacing(new Dimension(1,1));
        }

        // incorporate TableColumn settings
        var model = getTableModel();
        var tableColumns = model.getTableColumns();
        for (int colIdx = 0; colIdx < tableColumns.size(); colIdx++) {
            TableColumn tableColumn = tableColumns.get(colIdx);
            TableCellEditor tableCellEditor = tableColumn.getEditor();
            TableCellRenderer tableCellRenderer = tableColumn.getRenderer();
            if (tableCellEditor != null) {
                setColumnEditor(convertColumnIndexToView(colIdx), tableCellEditor);
            }
            if (tableCellRenderer != null) {
                setColumnRenderer(convertColumnIndexToView(colIdx), tableCellRenderer);
            }
        }
    }

    // =======================================================================
    // TABLEMODEL

    /**
     * Get the actual table model
     * @return
     */
    public TableModel<TableType> getTableModel() {
        return (TableModel<TableType>)super.getModel();
    }

    // =======================================================================
    // DATA

    /**
     * Set new data to show
     * @param v
     */
    public void setData(List<TableType> v) {
        getTableModel().setData(v);
    }
    public List<TableType> getData() {
        return getTableModel().getData();
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
            if (getCellEditor() != null) {
                getCellEditor().stopCellEditing();
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
        if (getCellEditor() != null) {
            getCellEditor().cancelCellEditing();
        }
    }


    // =======================================================================
    // COLUMNS

    /**
     * Get the columns
     * @return Unmodifiable list of colums
     */
    public List<TableColumn<TableType, ?>> getColumns() {
        return getTableModel().getTableColumns();
    }

    /**
     * Finds (the first!) column with the provided id.
     * @param id
     * @return
     */
    public TableColumn<TableType, ?> findColumnById(String id) {
        return getTableModel().findTableColumnById(id);
    }


    /**
     * Append a column
     * @param tableColumn
     * @param <ColumnType>
     */
    public <ColumnType extends Object> void addColumn(TableColumn<TableType, ColumnType> tableColumn) {
        tableColumn.setTable(this);
        getTableModel().addColumn(tableColumn);
    }

    /**
     * Remove a column
     * @param tableColumn
     * @return Indicate if a remove actually took place.
     * @param <ColumnType>
     */
    public <ColumnType extends Object> boolean removeColumn(TableColumn<TableType, ColumnType> tableColumn) {
        return getTableModel().removeColumn(tableColumn);
    }

    /**
     * Basic method of adding a column, but generics makes using this somewhat unreadable
     * ...column(new TableColumn<Bean, String>(String.class).title("Property").valueSupplier(d -> d.getProperty()))
     *
     * @param tableColumn
     * @return
     * @param <ColumnType>
     */
    private <ColumnType extends Object> STable<TableType> column(TableColumn<TableType, ColumnType> tableColumn) {
        addColumn(tableColumn);
        return this;
    }

    /**
     * Add a column. Requires the table() call at the end to continue the fluent API
     * ...column(String.class).title("Property").valueSupplier(d -> d.getProperty())).table()
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
     * ...columns(Bean.class, "property", "anotherProperty")
     * @param tableTypeClass
     * @param propertyNames
     * @return
     */
    public STable<TableType> columns(Class<TableType> tableTypeClass, String... propertyNames) {
        try {
            // Use Java's bean inspection classes to analyse the bean
            BeanInfo beanInfo = Introspector.getBeanInfo(tableTypeClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            Map<String, PropertyDescriptor> propertyDescriptorsMap = Arrays.stream(propertyDescriptors).collect(Collectors.toMap(pd -> pd.getName(), pd -> pd));

            // For each property create a column
            for (String propertyName : propertyNames) {
                PropertyDescriptor propertyDescriptor = propertyDescriptorsMap.get(propertyName);
                if (propertyDescriptor == null) {
                    throw new IllegalArgumentException("Property '" + propertyName + "' not found in bean " + tableTypeClass);
                }

                // Handle primitive types
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                if (propertyType.isPrimitive()) {
                    propertyType = primitiveToClassMap.get(propertyType);
                }

                // Add column
                column((Class<Object>) propertyType) // It's okay, JTable will still use the appropriate renderer and editor
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

    /**
     * Updated rendering
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

        // get the component
        Component component = super.prepareRenderer(renderer, row, col);

        // alternate the row color
        if (getEditingRow() != row && alternateRowColor) {
            if (!isRowSelected(row) || isPrinting) {
                Color lColor = ((row % 2 != 0) ? getFirstAlternateRowColor() : getSecondAlternateRowColor());
                if (component.getBackground() != lColor) {
                    component.setBackground( lColor );
                }
            }
        }

        // render disabled cells
        component.setEnabled(true);
        if ( (disabledTableShowsCellsAsDisabled && component.isEnabled() != isEnabled())     // if table is disabled
          || (uneditableTableShowsCellsAsDisabled  && component.isEnabled() != isEditable()) // if table is marked uneditable
        ) {
            component.setEnabled(false);
        }
        // if the cell is not editable, show it as disabled
        if (uneditableCellsShowAsDisabled && !isCellEditable(row, col)) {
            component.setEnabled(false);
        }

        // done
        return component;
    }

    // ===========================================================================
    // Printing

    /**
     * Remember if we are being printed
     */
    public void print(Graphics g) {
        try {
            isPrinting = true;
            super.print(g);
        }
        finally {
            isPrinting = false;
        }
    }
    boolean isPrinting = false;

    /**
     * is the table currently being printed?
     * @return
     */
    public boolean isPrinting() {
        return isPrinting;
    }

    // ===========================================================================
    // FLUENT API

    public STable<TableType> name(String v) {
        setName(v);
        return this;
    }

    // ===========================================================================
    // BINDING

    /**
     * monitorBean
     */
    public void setMonitorBean(Class<TableType> v) {
        getTableModel().setMonitorBean(v);
    }
    public Class<TableType> getMonitorBean() {
        return getTableModel().getMonitorBean();
    }
    private Class<TableType> monitorBean = null;
    public STable<TableType> monitorBean(Class<TableType> v) {
        setMonitorBean(v);
        return this;
    }

    // ===========================================================================
    // cell renderer and editor helpers

    /** set a renderer for a whole column */
    public void setColumnRenderer(int column, TableCellRenderer renderer) {
        getColumnModel().getColumn(column).setCellRenderer(renderer);
    }
    public TableCellRenderer getColumnRenderer(int column) {
        return getColumnModel().getColumn(column).getCellRenderer();
    }
    public STable<TableType> columnRenderer(int column, TableCellRenderer renderer) {
        setColumnRenderer(column, renderer);
        return this;
    }

    /** set a editor for a whole column */
    public void setColumnEditor(int column, TableCellEditor cellEditor) {
        getColumnModel().getColumn(column).setCellEditor(cellEditor);
    }
    public TableCellEditor getColumnEditor(int column)
    {
        return getColumnModel().getColumn(column).getCellEditor();
    }
    public STable<TableType> columnEditor(int column, TableCellEditor cellEditor) {
        setColumnEditor(column, cellEditor);
        return this;
    }


    /**
     * Generate two editors, and wrap one for renderer
     *
     * @param value
     * @return
     */
    public STable<TableType> defaultEditorAndRenderer(Class<?> columnClass, Supplier<TableCellEditor> value) {
        setDefaultEditor(columnClass, value.get());
        setDefaultRenderer(columnClass, new UseTableCellEditorAsTableCellRenderer(value.get()));
        return this;
    }
}