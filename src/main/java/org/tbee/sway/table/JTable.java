package org.tbee.sway.table;

import java.util.List;

public class JTable<TableType> extends javax.swing.JTable {

    public JTable() {
        super(new TableModel<TableType>());
    }
    public JTable(List<TableType> data) {
        super(new TableModel<TableType>());
        setData(data);
    }

    public TableModel<TableType> getTableModel() {
        return (TableModel<TableType>) getModel();
    }


    public void setData(List<TableType> v) {
        getTableModel().setData(v);
    }
    public List<TableType> getData() {
        return getTableModel().getData();
    }

    public <ColumnType extends Object> JTable<TableType> column(TableColumn<TableType, ColumnType> tableColumn) {
        addColumn(tableColumn);
        return this;
    }
    public <ColumnType extends Object> void addColumn(TableColumn<TableType, ColumnType> tableColumn) {
        tableColumn.table = this;
        getTableModel().addColumn(tableColumn);
    }
    public <ColumnType extends Object> TableColumn<TableType, ColumnType> column(Class<ColumnType> type) {
        var tableColumn = new TableColumn<TableType, ColumnType>(type);
        addColumn(tableColumn);
        return tableColumn;
    }
}