package org.tbee.sway.action;

import org.tbee.sway.support.IconRegistry;
import org.tbee.sway.table.STableCore;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

public class STableCopySelection implements Action {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STableCopySelection.class);

    // These are the same separators as used by Excel
    static final String FIELD_SEPARATOR = "\t";
    static final String RECORD_SEPARATOR = "\n";

    @Override
    public String label() {
        return "Copy selection";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find("copy", IconRegistry.Usage.MENU);
    }

    @Override
    public boolean isApplicableFor(Component component, Map<String, Object> context) {
        return component instanceof STableCore;
    }

    @Override
    public boolean isEnabled(Component component, Map<String, Object> context) {
        STableCore sTable = (STableCore)component;
        return sTable.isEnabled();
    }

    @Override
    public void apply(Component component, Map<String, Object> context) {
        STableCore sTableCore = (STableCore)component;

        // get the range to copy into
        int selectedRowCnt = sTableCore.getSelectedRowCount();
        int selectedColCnt = sTableCore.getSelectedColumnCount();
        int[] selectedRows = sTableCore.getSelectedRows();
        int[] selectedCols = sTableCore.getSelectedColumns();

        // if row selection is not allowed, emulate
        if (!sTableCore.getRowSelectionAllowed()) {
            selectedRowCnt = sTableCore.getRowCount();
            selectedRows = new int[selectedRowCnt];
            for (int i = 0; i < selectedRowCnt; i++) selectedRows[i] = i;
        }

        // if column selection is not allowed, emulate
        if (!sTableCore.getColumnSelectionAllowed()) {
            selectedColCnt = sTableCore.getColumnCount();
            selectedCols = new int[selectedColCnt];
            for (int i = 0; i < selectedColCnt; i++) selectedCols[i] = i;
        }

        // create the copy
        String s = (selectedColCnt <= 0 || selectedRowCnt <= 0 ? "" : copy(sTableCore, selectedRows, selectedCols));

        // dump in system clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(s);
        clipboard.setContents(stringSelection, stringSelection);
    }


    /**
     * The method actually generating the copy string that is placed on the clipboard
     */
    static public String copy(STableCore<?> table, int[] selectedRows, int[] selectedCols) {

        // copy from all selected rows
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < selectedRows.length; i++) {

            // copy from all selected columns
            for (int j = 0; j < selectedCols.length; j++) {

                // convert value
                String value = table.getTableModel().getValueAtAsString(selectedRows[i], selectedCols[j]);

                // field
                if (logger.isDebugEnabled()) logger.debug("copy from table cell " + selectedRows[i] + "," + selectedCols[j] + ": " + value);
                stringBuffer.append( value );

                // field separator
                if (j < selectedCols.length - 1) stringBuffer.append(FIELD_SEPARATOR);
            }
            // line separator
            if (i < selectedRows.length - 1) stringBuffer.append(RECORD_SEPARATOR);
        }
        return stringBuffer.toString();
    }
}
