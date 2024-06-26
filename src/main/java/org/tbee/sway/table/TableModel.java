package org.tbee.sway.table;

import org.tbee.sway.STable;

import javax.swing.table.AbstractTableModel;

public class TableModel<TableType> extends AbstractTableModel {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STableCore.class);

    final private STable<TableType> sTable;

    public TableModel(STable<TableType> sTable) {
        this.sTable = sTable;
    }

    // =======================================================================
    // TABLEMODEL

    @Override
    public int getRowCount() {
        return sTable.getItems().size();
    }

    @Override
    public int getColumnCount() {
        return sTable.getTableColumns().size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return sTable.getTableColumns().get(columnIndex).getTitle();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return sTable.getTableColumns().get(columnIndex).getType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return sTable.getTableColumns().get(columnIndex).determineEditable();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TableColumn<TableType, ?> column = sTable.getTableColumns().get(columnIndex);
        TableType record = sTable.getItems().get(rowIndex);
        return column.getValue(record);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TableColumn<TableType, ?> column = sTable.getTableColumns().get(columnIndex);
        TableType record = sTable.getItems().get(rowIndex);
        column.setValue(record, aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public String getValueAtAsString(int rowIndex, int columnIndex) {
        TableColumn<TableType, ?> column = sTable.getTableColumns().get(columnIndex);
        TableType record = sTable.getItems().get(rowIndex);
        return column.getValueAsString(record);
    }

    public void setValueAtAsString(String aValue, int rowIndex, int columnIndex) {
        TableColumn<TableType, ?> column = sTable.getTableColumns().get(columnIndex);
        TableType record = sTable.getItems().get(rowIndex);
        column.setValueAsString(record, aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
