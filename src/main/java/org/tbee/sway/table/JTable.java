package org.tbee.sway.table;

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
// - more editors and renderers
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
public class JTable<TableType> extends javax.swing.JTable {

    public JTable() {
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
    public <ColumnType extends Object> JTable<TableType> column(TableColumn<TableType, ColumnType> tableColumn) {
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
    public JTable<TableType> columns(Class<TableType> tableTypeClass, String... propertyNames) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(tableTypeClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            Map<String, PropertyDescriptor> propertyDescriptorsMap = Arrays.stream(propertyDescriptors).collect(Collectors.toMap(pd -> pd.getName(), pd -> pd));

            for (String propertyName : propertyNames) {
                PropertyDescriptor propertyDescriptor = propertyDescriptorsMap.get(propertyName);
                if (propertyName == null) {
                    throw new IllegalArgumentException("Property '" + propertyName + "' not found in bean " + tableTypeClass);
                }
                column((Class<Object>)propertyDescriptor.getPropertyType())
                        .title(propertyName) //
                        .valueSupplier(d -> {
                            try {
                                return propertyDescriptor.getReadMethod().invoke(d);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }) //
                        .valueConsumer((d,v) -> {
                            try {
                                propertyDescriptor.getWriteMethod().invoke(d, v);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }) //
                        .editable(propertyDescriptor.getWriteMethod() != null) // if there is a write method
                ;
            }
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}