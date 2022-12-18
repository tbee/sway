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
}