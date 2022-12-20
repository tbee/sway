package org.tbee.sway.table;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableModel<TableType> extends AbstractTableModel {


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
        this.data = Collections.unmodifiableList(v); // We don't allow outside changes to the provided list
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
}
