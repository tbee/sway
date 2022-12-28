package org.tbee.sway.table;

import org.tbee.sway.support.FocusInterpreter;
import org.tbee.sway.support.SwayUtil;

import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * This is an extended JTable, that is used by STable.
 * You probably want to use STable.
 *
 * @param <TableType>
 */
public class STableCore<TableType> extends javax.swing.JTable {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STableCore.class);

    final private TableRowSorter<TableModel<TableType>> tableRowSorter;

    public STableCore() {
        super(new TableModel<TableType>());

        // TODO: somehow the setComparator is forgotten, so we override the relevant methods. But we should figure out why this is.
        tableRowSorter = new TableRowSorter<>(getTableModel()){
            @Override
            public Comparator<?> getComparator(int column) {
                Comparator<?> comparator = getTableModel().getTableColumns().get(column).getSorting();
                if (comparator != null) {
                    return comparator;
                }
                return super.getComparator(column);
            }
            @Override
            protected boolean useToString(int column) {
                Comparator<?> comparator = getTableModel().getTableColumns().get(column).getSorting();
                if (comparator != null) {
                    return false;
                }
                return super.useToString(column);
            }
        };
        construct();
    }

    private void construct() {

        // the FocusInterpreterListener must be kept in an instance variable, otherwise it will be cleared by the WeakArrayList used in the FocusInterpreter
        focusInterpreterListener = evt -> {
            if (evt.getState() == FocusInterpreter.State.FOCUS_LOST) {
                stopEdit();
            }
        };
        focusInterpreter.addFocusListener(focusInterpreterListener);

        // Sorting
        setRowSorter(tableRowSorter);
        tableRowSorter.addRowSorterListener(e -> {
            // Clear the selection, because in the new sort it may not be feasible to maintain it
            // And the selected rows will change position.
            // (A more intelligent algorithme could maintain the selection if possible)
            STableCore.this.clearSelection();
        });

        // "Sets whether editors in this JTable get the keyboard focus when an editor is activated as a result of the JTable forwarding keyboard events for a cell."
        // "By default, this property is false, and the JTable retains the focus unless the cell is clicked."
        super.setSurrendersFocusOnKeystroke(true);

        // upon keypress the editor is started automatically
        // note: this behaviour deviates slightly from the normal behaviour
        //       for example: pressing F2 first fires a "focus gained"
        //                    typing directly first processes the character
        putClientProperty("JTable.autoStartsEdit", Boolean.TRUE);

        // default row height
        if (UIManager.get("Table.RowHeight") != null) {
            setRowHeight(UIManager.getInt("Table.RowHeight"));
        }

        // per default fill the viewport
        setFillsViewportHeight(true);

        // per default the table must have cellspacing, which means a visible grid (Nimbus does not)
        if (getIntercellSpacing().width < 1 || getIntercellSpacing().height < 1) {
            setIntercellSpacing(new Dimension(1, 1));
        }
    }
    private FocusInterpreter.FocusInterpreterListener focusInterpreterListener = null;
    final private FocusInterpreter focusInterpreter = new FocusInterpreter(this);

    // =======================================================================
    // TABLEMODEL

    /**
     * Get the actual table model
     * @return
     */
    public TableModel<TableType> getTableModel() {
        return (TableModel<TableType>)super.getModel();
    }

    // =======================================================================
    // DATA

    /**
     * Set new data to show
     * @param v
     */
    public void setData(List<TableType> v) {
        getTableModel().setData(v);
    }
    public List<TableType> getData() {
        return getTableModel().getData();
    }
    public STableCore<TableType> data(List<TableType> v) {
        setData(v);
        return this;
    }


    /**
     * Stop the edit by either accepting or cancelling
     */
    public void stopEdit() {
        if (!isEditing()) {
            return;
        }
        try {
            if (getCellEditor() != null) {
                getCellEditor().stopCellEditing();
            }
        }
        finally {
            cancelEdit();
        }
    }

    /**
     * Cancel the edit
     */
    public void cancelEdit() {
        if (!isEditing()) {
            return;
        }
        if (getCellEditor() != null) {
            getCellEditor().cancelCellEditing();
        }
    }


    // =======================================================================
    // COLUMNS

    /**
     * Get the columns
     * @return Unmodifiable list of colums
     */
    public List<TableColumn<TableType, ?>> getColumns() {
        return getTableModel().getTableColumns();
    }

    /**
     * Finds (the first!) column with the provided id.
     * @param id
     * @return
     */
    public <ColumnType> TableColumn<TableType, ColumnType> findColumnById(String id) {
        return getTableModel().findTableColumnById(id);
    }


    /**
     * Append a column
     * @param tableColumn
     * @param <ColumnType>
     */
    public <ColumnType extends Object> void addColumn(TableColumn<TableType, ColumnType> tableColumn) {
        getTableModel().addColumn(tableColumn);
    }

    /**
     * Remove a column
     * @param tableColumn
     * @return Indicate if a remove actually took place.
     * @param <ColumnType>
     */
    public <ColumnType extends Object> boolean removeColumn(TableColumn<TableType, ColumnType> tableColumn) {
        return getTableModel().removeColumn(tableColumn);
    }

    /**
     * Add a column. Requires the table() call at the end to continue the fluent API
     * ...column(String.class).title("Property").valueSupplier(d -> d.getProperty())).table()
     * @param type
     * @return
     * @param <ColumnType>
     */
    public <ColumnType extends Object> TableColumn<TableType, ColumnType> column(Class<ColumnType> type) {
        var tableColumn = new TableColumn<TableType, ColumnType>(type);
        addColumn(tableColumn);
        return tableColumn;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        // TableColumn based
        TableCellRenderer renderer = getTableModel().getTableColumns().get(column).getRenderer();
        if (renderer != null) {
            return renderer;
        }

        // Default behavior
        return super.getCellRenderer(row, column);
    }


    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        // TableColumn based
        TableCellEditor editor = getTableModel().getTableColumns().get(column).getEditor();
        if (editor != null) {
            return editor;
        }

        // Default behavior
        return super.getCellEditor(row, column);
    }

    // ===========================================================================
    // RENDERING e.g. alternate row colors

    /** Alternate the background color for rows */
    public void setAlternateRowColor(boolean v) {
        firePropertyChange(ALTERNATEROWCOLOR, this.alternateRowColor, this.alternateRowColor = v);
    }
    public boolean getAlternateRowColor() {
        return alternateRowColor;
    }
    private boolean alternateRowColor = true;
    final static public String ALTERNATEROWCOLOR = "alternateRowColor";
    public STableCore<TableType> alternateRowColor(boolean v) {
        setAlternateRowColor(v);
        return this;
    }

    /** The color to use for the alternating background color for rows */
    public void setFirstAlternateRowColor(Color v) {
        firePropertyChange(FIRSTALTERNATEROWCOLOR, this.firstAlternateRowColor, this.firstAlternateRowColor = v);
    }
    public Color getFirstAlternateRowColor() {
        return firstAlternateRowColor;
    }
    private Color firstAlternateRowColor = SwayUtil.getFirstAlternateRowColor();
    final static public String FIRSTALTERNATEROWCOLOR = "firstAlternateRowColor";
    public STableCore<TableType> firstAlternateRowColor(Color v) {
        firstAlternateRowColor(v);
        return this;
    }

    /** The second color to use for the alternating background color for rows */
    public void setSecondAlternateRowColor(Color v) {
        firePropertyChange(SECONDALTERNATEROWCOLOR, this.secondAlternateRowColor, this.secondAlternateRowColor = v);
    }
    public Color getSecondAlternateRowColor() {
        return secondAlternateRowColor;
    }
    private Color secondAlternateRowColor = SwayUtil.getSecondAlternateRowColor();
    final static public String SECONDALTERNATEROWCOLOR = "secondAlternateRowColor";
    public STableCore<TableType> secondAlternateRowColor(Color v) {
        setSecondAlternateRowColor(v);
        return this;
    }

    /** UneditableCellsShowAsDisabled */
    public void setUneditableCellsShowAsDisabled(boolean v) {
        firePropertyChange(UNEDITABLECELLSSHOWASDISABLED, this.uneditableCellsShowAsDisabled, this.uneditableCellsShowAsDisabled = v);
    }
    public boolean getUneditableCellsShowAsDisabled() {
        return uneditableCellsShowAsDisabled;
    }
    private boolean uneditableCellsShowAsDisabled = true;
    final static public String UNEDITABLECELLSSHOWASDISABLED = "uneditableCellsShowAsDisabled";
    public STableCore<TableType> uneditableCellsShowAsDisabled(boolean v) {
        setUneditableCellsShowAsDisabled(v);
        return this;
    }

    /** DisabledTableShowsCellsAsDisabled */
    public void setDisabledTableShowsCellsAsDisabled(boolean v) {
        firePropertyChange(DISABLEDTABLESHOWSCELLSASDISABLED, this.disabledTableShowsCellsAsDisabled, this.disabledTableShowsCellsAsDisabled = v);
    }
    public boolean getDisabledTableShowsCellsAsDisabled() {
        return disabledTableShowsCellsAsDisabled;
    }
    private boolean disabledTableShowsCellsAsDisabled = true;
    final static public String DISABLEDTABLESHOWSCELLSASDISABLED = "disabledTableShowsCellsAsDisabled";
    public STableCore<TableType> disabledTableShowsCellsAsDisabled(boolean v) {
        setDisabledTableShowsCellsAsDisabled(v);
        return this;
    }

    /** Editable */
    public void setEditable(boolean v) {
        editable = v;
        repaint();
    }
    public boolean isEditable() {
        return editable;
    }
    private boolean editable = true;
    public STableCore<TableType> editable(boolean v) {
        setEditable(v);
        return this;
    }

    /** UneditableTableShowsCellsAsDisabled */
    public void setUneditableTableShowsCellsAsDisabled(boolean v) {
        firePropertyChange(UNEDITABLETABLESHOWSCELLSASDISABLED, this.uneditableTableShowsCellsAsDisabled, this.uneditableTableShowsCellsAsDisabled = v);
    }
    public boolean getUneditableTableShowsCellsAsDisabled() {
        return uneditableTableShowsCellsAsDisabled;
    }
    private boolean uneditableTableShowsCellsAsDisabled = true;
    final static public String UNEDITABLETABLESHOWSCELLSASDISABLED = "uneditableTableShowsCellsAsDisabled";
    public STableCore<TableType> uneditableTableShowsCellsAsDisabled(boolean v) {
        setUneditableTableShowsCellsAsDisabled(v);
        return this;
    }

    /** must repaint because cells may be shown disabled */
    public void setEnabled(boolean v) {
        super.setEnabled(v);
        repaint();
    }

    /**
     * Updated rendering
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

        // get the component
        Component component = super.prepareRenderer(renderer, row, col);

        // alternate the row color
        if (getEditingRow() != row && alternateRowColor) {
            if (!isRowSelected(row) || isPrinting) {
                Color lColor = ((row % 2 != 0) ? getFirstAlternateRowColor() : getSecondAlternateRowColor());
                if (component.getBackground() != lColor) {
                    component.setBackground( lColor );
                }
            }
        }

        // render disabled cells
        component.setEnabled(true);
        if ( (disabledTableShowsCellsAsDisabled && component.isEnabled() != isEnabled())     // if table is disabled
          || (uneditableTableShowsCellsAsDisabled  && component.isEnabled() != isEditable()) // if table is marked uneditable
        ) {
            component.setEnabled(false);
        }
        // if the cell is not editable, show it as disabled
        if (uneditableCellsShowAsDisabled && !isCellEditable(row, col)) {
            component.setEnabled(false);
        }

        // done
        return component;
    }

    // ===========================================================================
    // Printing

    /**
     * Remember if we are being printed
     */
    public void print(Graphics g) {
        try {
            isPrinting = true;
            super.print(g);
        }
        finally {
            isPrinting = false;
        }
    }
    boolean isPrinting = false;

    /**
     * is the table currently being printed?
     * @return
     */
    public boolean isPrinting() {
        return isPrinting;
    }

    // ===========================================================================
    // FLUENT API

    public STableCore<TableType> name(String v) {
        setName(v);
        return this;
    }

    // ===========================================================================
    // BINDING

    /**
     * monitorBean
     */
    public void setMonitorBean(Class<TableType> v) {
        getTableModel().setMonitorBean(v);
    }
    public Class<TableType> getMonitorBean() {
        return getTableModel().getMonitorBean();
    }
    private Class<TableType> monitorBean = null;
    public STableCore<TableType> monitorBean(Class<TableType> v) {
        setMonitorBean(v);
        return this;
    }

    // ===========================================================================
    // cell renderer and editor helpers

    /** set a renderer for a whole column */
    public void setColumnRenderer(int column, TableCellRenderer renderer) {
        getColumnModel().getColumn(column).setCellRenderer(renderer);
    }
    public TableCellRenderer getColumnRenderer(int column) {
        return getColumnModel().getColumn(column).getCellRenderer();
    }
    public STableCore<TableType> columnRenderer(int column, TableCellRenderer renderer) {
        setColumnRenderer(column, renderer);
        return this;
    }

    /** set a editor for a whole column */
    public void setColumnEditor(int column, TableCellEditor cellEditor) {
        getColumnModel().getColumn(column).setCellEditor(cellEditor);
    }
    public TableCellEditor getColumnEditor(int column) {
        return getColumnModel().getColumn(column).getCellEditor();
    }
    public STableCore<TableType> columnEditor(int column, TableCellEditor cellEditor) {
        setColumnEditor(column, cellEditor);
        return this;
    }


    /**
     * Generate two editors, and wrap one for renderer
     *
     * @param value
     * @return
     */
    public STableCore<TableType> defaultEditorAndRenderer(Class<?> columnClass, Supplier<TableCellEditor> value) {
        setDefaultEditor(columnClass, value.get());
        setDefaultRenderer(columnClass, new UseTableCellEditorAsTableCellRenderer(value.get()));
        return this;
    }
}