package org.tbee.sway.table;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TableColumn<TableType, ColumnType extends Object> {

    public TableColumn(Class<ColumnType> type) {
        this.type = type;
    }


    // =======================================================================
    // TABLE(MODEL)

    /**
     * Needed for a fluent API
     * @return
     */
    public JTable<TableType> table() {
        return table;
    }
    JTable<TableType> table;
    TableModel<TableType> tabelModel;

    private void fireTableStructureChanged() {
        if (tabelModel != null) {
            tabelModel.fireTableStructureChanged();
        }
    }
    private void fireTableDataChanged() {
        if (tabelModel != null) {
            tabelModel.fireTableDataChanged();
        }
    }

    // =======================================================================
    // VALUE

    ColumnType getValue(TableType record) {
        return valueSupplier.apply(record);
    }
    void setValue(TableType record, Object value) {
        if (getEditable() == false) {
            return; // not editable, then we're not setting
        }
        valueConsumer.accept(record, (ColumnType) value);
    }

    // =======================================================================
    // PROPERTIES

    // TYPE
    final private Class<ColumnType> type;
    public Class<ColumnType> getType() {
        return type;
    }

    // TITLE
    private String title = "";
    public String getTitle() {
        return title;
    }
    public void setTitle(String v) {
        title = v;
        fireTableStructureChanged();
    }
    public TableColumn<TableType, ColumnType> title(String v) {
        setTitle(v);
        return this;
    }

    // EDITABLE
    private boolean editable = false;
    public boolean getEditable() {
        return editable;
    }
    public void setEditable(boolean v) {
        editable = v;
        fireTableStructureChanged();
    }
    public TableColumn<TableType, ColumnType> editable(boolean v) {
        setEditable(v);
        return this;
    }

    // setValueFunction
    private BiConsumer<TableType, ColumnType> valueConsumer = null;
    public BiConsumer<TableType, ColumnType> getValueConsumer() {
        return valueConsumer;
    }
    public void setValueConsumer(BiConsumer<TableType, ColumnType> v) {
        valueConsumer = v;
    }
    public TableColumn<TableType, ColumnType> valueConsumer(BiConsumer<TableType, ColumnType> v) {
        setValueConsumer(v);
        return this;
    }

    // getValueFunction
    private Function<TableType, ColumnType> valueSupplier = null;
    public Function<TableType, ColumnType> getValueSupplier() {
        return valueSupplier;
    }
    public void setValueSupplier(Function<TableType, ColumnType> v) {
        valueSupplier = v;
        fireTableDataChanged();
    }
    public TableColumn<TableType, ColumnType> valueSupplier(Function<TableType, ColumnType> v) {
        setValueSupplier(v);
        return this;
    }
}
