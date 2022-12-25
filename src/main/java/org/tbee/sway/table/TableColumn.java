package org.tbee.sway.table;

import org.tbee.sway.STable;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TableColumn<TableType, ColumnType extends Object> {

    public TableColumn(Class<ColumnType> type) {
        this.type = type;
    }


    // =======================================================================
    // TABLE(MODEL)

    /**
     * table
     * @param v
     */
    public void setTable(STable<TableType> v) {
        this.table = v;
    }
    public STable<TableType> getTable() {
        return this.table;
    }
    private STable<TableType> table;
    // Needed for a fluent API
    public STable<TableType> table() {
        return table;
    }

    /**
     *
     * @param v
     */
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

    /**
     * Type
     *
     * @return
     */
    public Class<ColumnType> getType() {
        return type;
    }
    final private Class<ColumnType> type;

    /**
     * Id: use to identify the column programmatically
     *
     * @param v
     */
    public void setId(String v) {
        id = v;
    }
    public String getId() {
        return id;
    }
    private String id = "";
    public TableColumn<TableType, ColumnType> id(String v) {
        setId(v);
        return this;
    }

    /**
     * title
     *
     * @param v
     */
    public void setTitle(String v) {
        title = v;
        fireTableStructureChanged();
    }
    public String getTitle() {
        return title;
    }
    private String title = "";
    public TableColumn<TableType, ColumnType> title(String v) {
        setTitle(v);
        return this;
    }

    /**
     * editable
     *
     * @param v
     */
    public void setEditable(boolean v) {
        editable = v;
        fireTableStructureChanged();
    }
    public boolean getEditable() {
        return editable == null ? false : editable;
    }
    private Boolean editable = null;
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

    /**
     * setValueFunction
     *
     * @param v
     */
    public void setValueConsumer(BiConsumer<TableType, ColumnType> v) {
        valueConsumer = v;
    }
    public BiConsumer<TableType, ColumnType> getValueConsumer() {
        return valueConsumer;
    }
    private BiConsumer<TableType, ColumnType> valueConsumer = null;
    public TableColumn<TableType, ColumnType> valueConsumer(BiConsumer<TableType, ColumnType> v) {
        setValueConsumer(v);
        return this;
    }

    /**
     * getValueFunction
     *
     * @param v
     */
    public void setValueSupplier(Function<TableType, ColumnType> v) {
        valueSupplier = v;
        fireTableDataChanged();
    }
    public Function<TableType, ColumnType> getValueSupplier() {
        return valueSupplier;
    }
    private Function<TableType, ColumnType> valueSupplier = null;
    public TableColumn<TableType, ColumnType> valueSupplier(Function<TableType, ColumnType> v) {
        setValueSupplier(v);
        return this;
    }

    /**
     * bindToProperty
     */
    public void setMonitorProperty(String v) {
        monitorProperty = v;
    }
    public String getMonitorProperty() {
        return monitorProperty;
    }
    private String monitorProperty = null;
    public TableColumn<TableType, ColumnType> monitorProperty(String v) {
        setMonitorProperty(v);
        return this;
    }

    /** Editor: */
    public void setEditor(TableCellEditor value) {
        editor = value;
    }
    public TableCellEditor getEditor() { return editor; }
    public TableColumn<TableType, ColumnType> editor(TableCellEditor value) {
        setEditor(value);
        return this;
    }
    volatile private TableCellEditor editor = null;
    final static public String EDITOR = "editor";

    /** Renderer: */
    public void setRenderer(TableCellRenderer value) {
        renderer = value;
    }
    public TableCellRenderer getRenderer() { return renderer; }
    public TableColumn<TableType, ColumnType> renderer(TableCellRenderer value) { setRenderer(value); return this; }
    volatile private TableCellRenderer renderer = null;
    final static public String RENDERER = "renderer";
    /**
     * Shortcut for withRenderer(new UseTableCellEditorAsTableCellRenderer(editor)) 
     */
    public TableColumn<TableType, ColumnType> renderer(TableCellEditor value) {
        // note: in order to use an editor as renderer it needs to be a separate instance, and they don't implement clonable
        setRenderer(new UseTableCellEditorAsTableCellRenderer(value));
        return this;
    }

    /**
     * Generate two editors, and wrap one for renderer
     * @param value
     * @return
     */
    public TableColumn<TableType, ColumnType> editorAndRenderer(Supplier<TableCellEditor> value) {
        editor(value.get());
        renderer(value.get());
        return this;
    }

}
