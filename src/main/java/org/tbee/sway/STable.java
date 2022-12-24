package org.tbee.sway;

import org.tbee.sway.support.SwayUtil;
import org.tbee.sway.table.TableColumn;
import org.tbee.sway.table.TableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO
// - better javadoc
// - more editors and renderers (LocalDate, etc)
// - per column editor and renderer
// - per cell renderer and editor
// - make primitive bean properties editable
// - visualizing uneditable
// - error handling: display errors/exceptions coming from setValueAt
// - binding (listen to) bean properties to update cells automatically: bindToBeanProperties(true)
// - binding (listen to) list changes:
// - sorting (map the row in the table model)
// - pagination
// - filter
// - default footer showing active row/col etc
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
// - make separate component and per default wrap in JScrollPane?

/**
 * This table implements an opinionated way how the table API should look.
 * Usage: new STable<TableType>().column(<ColType>.class).valueSupplier(d -> d.getValue())...table()
 * Or: new STable<TableType>().columns(<TableType>.class, "prop1", "prop2").table()
 *
 * It should still be wrapped in a JScrollPane.
 *
 * @param <TableType>
 */
public class STable<TableType> extends javax.swing.JTable {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STable.class);

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

    /**
     * Get the currently shown data
     * @return
     */
    public List<TableType> getData() {
        return getTableModel().getData();
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

                // Add column
                column((Class<Object>)propertyDescriptor.getPropertyType()) // It's okay, JTable will still use the appropriate renderer and editor
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
                        .bindToProperty(propertyName) //
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
     * bindToBean
     */
    public void setBindToBean(Class<TableType> v) {
        getTableModel().setBindToBean(v);
    }
    public Class<TableType> getBindToBean() {
        return getTableModel().getBindToBean();
    }
    private Class<TableType> beanClass = null;
    public STable<TableType> bindToBean(Class<TableType> v) {
        setBindToBean(v);
        return this;
    }
}