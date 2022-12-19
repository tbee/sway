package org.tbee.sway.table;

import org.tbee.sway.ColorUtil;

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
// - alternating colors
// - more editors and renderers (LocalDate, etc)
// - make primitive bean properties editable
// - visualizing uneditable
// - binding (listen to) bean properties
// - sorting (map the row in the table model)
// - column reordering (map the column in the table model)
// - column hiding (map the column in the table model)
// - fix known bugs (JXTable)

/**
 * This table implements an opinionated way how the table API should look.
 * Use: new JTable().column(<Type>.class).valueSupplier(d -> d.getValue())...
 *
 * @param <TableType>
 */
public class STable<TableType> extends javax.swing.JTable {

    public STable() {
        super(new TableModel<TableType>());
    }

    public TableModel<TableType> getTableModel() {
        return (TableModel<TableType>) getModel();
    }

    // =======================================================================
    // DATA

    public void setData(List<TableType> v) {
        getTableModel().setData(v);
    }
    public List<TableType> getData() {
        return getTableModel().getData();
    }


    // =======================================================================
    // COLUMNS

    private <ColumnType extends Object> void addColumn(TableColumn<TableType, ColumnType> tableColumn) {
        tableColumn.table = this;
        getTableModel().addColumn(tableColumn);
    }

    /**
     * Basic method of adding a column, but generics make this unreadable
     * @param tableColumn
     * @return
     * @param <ColumnType>
     */
    public <ColumnType extends Object> STable<TableType> column(TableColumn<TableType, ColumnType> tableColumn) {
        addColumn(tableColumn);
        return this;
    }

    /**
     * A a column.
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
     * Generate columns based on bean info
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
                if (propertyName == null) {
                    throw new IllegalArgumentException("Property '" + propertyName + "' not found in bean " + tableTypeClass);
                }
                column((Class<Object>)propertyDescriptor.getPropertyType()) // It's okay, JTable will still use the appropriate renderer and editor
                        .title(propertyName) //
                        .valueSupplier(bean -> {
                            try {
                                return propertyDescriptor.getReadMethod().invoke(bean);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }) //
                        .valueConsumer((bean,value) -> {
                            try {
                                propertyDescriptor.getWriteMethod().invoke(bean, value);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }) //
                        .editable(propertyDescriptor.getWriteMethod() != null) // if there is a write method then it is editable
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

    /** alternate the background color for rows */
    public void setAlternateRowColor(boolean value) {
        alternateRowColor = value;
    }
    public boolean getAlternateRowColor() {
        return alternateRowColor;
    }
    private boolean alternateRowColor = true;

    /** the color to use for the alternating background color for rows */
    public void setFirstAlternateRowColor(Color value) {
        firstAlternateRowColor = value;
    }
    public Color getFirstAlternateRowColor() {
        return firstAlternateRowColor;
    }
    private Color firstAlternateRowColor = new Color( UIManager.getColor("Table.background").getRGB() ); // creating a new color will remove the show-table background pattern thing in JTattoo LaF

    /** the second color to use for the alternating background color for rows */
    public void setSecondAlternateRowColor(Color value) {
        secondAlternateRowColor = value;
    }
    public Color getSecondAlternateRowColor() {
        return secondAlternateRowColor;
    }
    private Color secondAlternateRowColor = ColorUtil.brighterOrDarker(firstAlternateRowColor, 0.05);

    /** UneditableCellsShowAsDisabled */
    public boolean getUneditableCellsShowAsDisabled() {
        return uneditableCellsShowAsDisabled;
    }
    public void setUneditableCellsShowAsDisabled(boolean value) {
        uneditableCellsShowAsDisabled = value;
    }
    private boolean uneditableCellsShowAsDisabled = true;
    public STable<TableType> uneditableCellsShowAsDisabled(boolean value) {
        setUneditableCellsShowAsDisabled(value);
        return this;
    }

    /** DisabledTableShowsCellsAsDisabled */
    public boolean getDisabledTableShowsCellsAsDisabled() {
        return disabledTableShowsCellsAsDisabled;
    }
    public void setDisabledTableShowsCellsAsDisabled(boolean value) {
        disabledTableShowsCellsAsDisabled = value;
    }
    private boolean disabledTableShowsCellsAsDisabled = true;
    public STable<TableType> disabledTableShowsCellsAsDisabled(boolean value) {
        setDisabledTableShowsCellsAsDisabled(value);
        return this;
    }

    /** Editable */
    public boolean isEditable() {
        return editable;
    }
    public void setEditable(boolean value) {
        editable = value;
        repaint();
    }
    private boolean editable = true;
    public STable<TableType> editable(boolean value) {
        setEditable(value);
        return this;
    }

    //    /** UneditableTableShowsCellsAsDisabled */
    public boolean getUneditableTableShowsCellsAsDisabled() {
        return uneditableTableShowsCellsAsDisabled;
    }
    public void setUneditableTableShowsCellsAsDisabled(boolean value) {
        uneditableTableShowsCellsAsDisabled = value;
    }
    private boolean uneditableTableShowsCellsAsDisabled = true;
    public STable<TableType> uneditableTableShowsCellsAsDisabled(boolean value) {
        setUneditableTableShowsCellsAsDisabled(value);
        return this;
    }

    /** must repaint because cells may be shown disabled */
    public void setEnabled(boolean editable)
    {
        super.setEnabled(editable);
        repaint();
    }

    /**
     * Update rendering
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

        // optionally change the row height
        // TBEERNOT autosetRowHeight(row, component);

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
}