package org.tbee.sway.table;

import java.util.List;

/**
 * This table implements an opinioned way how the table API should ook.
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
}