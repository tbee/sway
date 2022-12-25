package org.tbee.sway;

import org.tbee.sway.table.STableNavigator;
import org.tbee.sway.table.TableColumn;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.util.List;

public class STableAIO<TableType> extends JPanel {

    private final STable<TableType> sTable;

    public STableAIO() {

        // Create components
        sTable = new STable<TableType>();
        JScrollPane scrollPane = new JScrollPane(sTable);
        STableNavigator tableNavigator = new STableNavigator(sTable);

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(tableNavigator, BorderLayout.SOUTH);
    }

    public STable<TableType> sTable() {
        return sTable;
    }

    // ===========================================================================
    // DATA

    public STableAIO<TableType> data(List<TableType> v) {
        sTable.data(v);
        return this;
    }

    // ===========================================================================
    // COLUMNS

    public <ColumnType extends Object> TableColumn<TableType, ColumnType> column(Class<ColumnType> type) {
        TableColumn<TableType, ColumnType> column = sTable.column(type);
        column.setTableAOI(this);
        return column;
    }

    public STableAIO<TableType> columns(Class<TableType> tableTypeClass, String... propertyNames) {
        sTable.columns(tableTypeClass, propertyNames);
        return this;
    }

    public TableColumn<TableType, ?> findColumnById(String id) {
        return sTable.findColumnById(id);
    }

    // ===========================================================================
    // BINDING

    public STableAIO<TableType> monitorBean(Class<TableType> v) {
        sTable.monitorBean(v);
        return this;
    }


    // ===========================================================================
    // FLUENT API

    public STableAIO<TableType> name(String v) {
        setName(v);
        sTable.name(v + ".sTable"); // For tests we need to address the actual table
        return this;
    }


}