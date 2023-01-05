package org.tbee.sway.table;

import org.tbee.sway.STable;
import org.tbee.sway.support.FocusInterpreter;

import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Comparator;
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

    final private STable<TableType> sTable;

    public STableCore(STable<TableType> sTable) {
        super(new TableModel<TableType>(sTable));
        this.sTable = sTable;

        // TODO: somehow the setComparator is forgotten, so we override the relevant methods. But we should figure out why this is.
        tableRowSorter = new TableRowSorter<>(getTableModel()){
            @Override
            public Comparator<?> getComparator(int column) {
                Comparator<?> comparator = sTable.getTableColumns().get(column).getSorting();
                if (comparator != null) {
                    return comparator;
                }
                return super.getComparator(column);
            }
            @Override
            protected boolean useToString(int column) {
                Comparator<?> comparator = sTable.getTableColumns().get(column).getSorting();
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
                sTable.stopEdit();
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

    public STable<TableType> getSTable() {
        return sTable;
    }


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
    // RENDERER

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        // TableColumn based
        TableCellRenderer renderer = sTable.getTableColumns().get(column).getRenderer();
        if (renderer != null) {
            return renderer;
        }

        // Default behavior
        return super.getCellRenderer(row, column);
    }


    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        // TableColumn based
        TableCellEditor editor = sTable.getTableColumns().get(column).getEditor();
        if (editor != null) {
            return editor;
        }

        // Default behavior
        return super.getCellEditor(row, column);
    }

    /**
     * Updated rendering
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

        // get the component
        Component component = super.prepareRenderer(renderer, row, col);

        // alternate the row color
        if (getEditingRow() != row && sTable.getAlternateRowColor()) {
            if (!isRowSelected(row) || isPrinting) {
                Color color = ((row % 2 != 0) ? sTable.getFirstAlternateRowColor() : sTable.getSecondAlternateRowColor());
                if (component.getBackground() != color) {
                    component.setBackground( color );
                }
            }
        }

        // render disabled cells
        component.setEnabled(true);
        if ( (sTable.getDisabledTableShowsCellsAsDisabled() && component.isEnabled() != isEnabled())     // if table is disabled
          || (sTable.getUneditableCellsShowAsDisabled()  && component.isEnabled() != sTable.isEditable()) // if table is marked uneditable
        ) {
            component.setEnabled(false);
        }
        // if the cell is not editable, show it as disabled
        if (sTable.getUneditableCellsShowAsDisabled() && !isCellEditable(row, col)) {
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