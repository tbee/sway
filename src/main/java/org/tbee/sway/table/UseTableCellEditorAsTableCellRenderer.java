package org.tbee.sway.table;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * Use an TableCellEditor as a TableCellRenderer.
 */
public class UseTableCellEditorAsTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {

    final private TableCellEditor tableCellEditor;

    public UseTableCellEditorAsTableCellRenderer(TableCellEditor tableCellEditor) {
        super();
        this.tableCellEditor = tableCellEditor;
    }

    /**
     * TableCellEditor
     */
    public TableCellEditor getTableCellEditor() {
        return tableCellEditor;
    }

    /**
     *
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component lEditor = tableCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        return lEditor;
    }
}
