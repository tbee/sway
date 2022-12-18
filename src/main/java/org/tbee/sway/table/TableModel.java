package org.tbee.sway.table;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class TableModel<TableType> extends AbstractTableModel {


    // =======================================================================
    // COLUMS

    final private List<TableColumn<TableType, ? extends Object>> tableColumns = new ArrayList<>();

    void addColumn(TableColumn<TableType, ? extends Object> tableColumn) {
        tableColumn.tabelModel = this;
        this.tableColumns.add(tableColumn);
        fireTableStructureChanged();
    }

    // =======================================================================
    // DATA

    private List<TableType> data = List.of();
    void setData(List<TableType> v) {
        this.data = v;
        fireTableDataChanged();
    }
    List<TableType> getData() {
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
        TableColumn<TableType, ? extends Object> column = tableColumns.get(columnIndex);
        TableType record = data.get(rowIndex);
        return column.getValue(record);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TableColumn<TableType, ? extends Object> column = tableColumns.get(columnIndex);
        TableType record = data.get(rowIndex);
        column.setValue(record, aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
