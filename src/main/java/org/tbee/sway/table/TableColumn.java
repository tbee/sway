package org.tbee.sway.table;

import org.tbee.sway.STable;

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
    public STable<TableType> table() {
        return table;
    }

    /**
     *
     * @param v
     */
    public void setTable(STable<TableType> v) {
        this.table = v;
    }
    public STable<TableType> getTable() {
        return this.table;
    }
    private STable<TableType> table;

    public void setTabelModel(TableModel<TableType> v) {
        this.tabelModel = v;
    }
    public TableModel<TableType> getTabelModel() {
        return this.tabelModel;
    }
    private TableModel<TableType> tabelModel;

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
        if (determineEditable() == false) {
            return; // not editable, then we're not setting
        }
        valueConsumer.accept(record, (ColumnType) value);
    }

    // =======================================================================
    // PROPERTIES

    // Type
    final private Class<ColumnType> type;
    public Class<ColumnType> getType() {
        return type;
    }

    // Id: use to identify the column programatically
    private String id = "";
    public String getId() {
        return id;
    }
    public void setId(String v) {
        id = v;
    }
    public TableColumn<TableType, ColumnType> id(String v) {
        setId(v);
        return this;
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
    private Boolean editable = null;
    public boolean getEditable() {
        return editable == null ? false : editable;
    }
    public void setEditable(boolean v) {
        editable = v;
        fireTableStructureChanged();
    }
    public TableColumn<TableType, ColumnType> editable(boolean v) {
        setEditable(v);
        return this;
    }
    boolean determineEditable() {
        if (editable != null) {
            return editable;
        }
        return (valueConsumer != null);
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
