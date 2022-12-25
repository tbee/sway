package org.tbee.sway.table;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableModel<TableType> extends AbstractTableModel {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STableCore.class);

    // =======================================================================
    // COLUMNS

    final private List<TableColumn<TableType, ?>> tableColumns = new ArrayList<>();
    public List<TableColumn<TableType, ?>> getTableColumns() {
        return Collections.unmodifiableList(tableColumns);
    }

    public TableColumn<TableType, ?> findTableColumnById(String id) {
        return tableColumns.stream() //
                .filter(tc -> id.equals(tc.getId())) //
                .findFirst().orElse(null);
    }

    public void addColumn(TableColumn<TableType, ?> tableColumn) {
        tableColumn.setTabelModel(this);
        this.tableColumns.add(tableColumn);
        fireTableStructureChanged();
    }
    public boolean removeColumn(TableColumn<TableType, ?> tableColumn) {
        boolean removed = this.tableColumns.remove(tableColumn);
        fireTableStructureChanged();
        return removed;
    }

    // =======================================================================
    // DATA

    private List<TableType> data = List.of();
    public void setData(List<TableType> v) {
        unregisterFromAllBeans();
        this.data = Collections.unmodifiableList(v); // We don't allow outside changes to the provided list
        registerToAllBeans();
        fireTableDataChanged();
    }
    public List<TableType> getData() {
        return this.data;
    }


    // =======================================================================
    // TABLEMODEL

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return tableColumns.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return tableColumns.get(columnIndex).getTitle();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return tableColumns.get(columnIndex).getType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return tableColumns.get(columnIndex).determineEditable();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TableColumn<TableType, ?> column = tableColumns.get(columnIndex);
        TableType record = data.get(rowIndex);
        return column.getValue(record);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TableColumn<TableType, ?> column = tableColumns.get(columnIndex);
        TableType record = data.get(rowIndex);
        column.setValue(record, aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
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
        for (int colIdx = 0; colIdx < tableColumns.size(); colIdx++) {
            String monitorProperty = tableColumns.get(colIdx).getMonitorProperty();
            if (monitorProperty != null && monitorProperty.equals(evt.getPropertyName())) {
                for (Integer rowIdx : rowIdxs) {
                    if (logger.isDebugEnabled()) logger.debug("Invoke fireTableCellUpdated(" + rowIdx + "," + colIdx + ")");
                    fireTableCellUpdated(rowIdx, colIdx);
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

}
