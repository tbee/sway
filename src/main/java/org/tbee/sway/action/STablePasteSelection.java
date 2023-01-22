package org.tbee.sway.action;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.Map;

import javax.swing.Icon;

import org.tbee.sway.support.IconRegistry;
import org.tbee.sway.table.STableCore;

import com.google.common.base.Splitter;

//TBEERNOT: move logic to STable
public class STablePasteSelection implements Action {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STablePasteSelection.class);

    // These are the same separators as used by Excel
    static final String FIELD_SEPARATOR = "\t";
    static final String RECORD_SEPARATOR = "\n";

    @Override
    public String label() {
        return "Paste";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.MENU_PASTE);
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
        STableCore table = (STableCore)component;

        try {
            // get data and start position
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            int[] selectedRows = table.getSelectedRows();
            int[] selectedCols = table.getSelectedColumns();

            // get data
            String clipboardContents = (String)(clipboard.getContents(table).getTransferData(DataFlavor.stringFlavor));

            // if row selection is not allowed, emulate
            if (!table.getRowSelectionAllowed()) {
                int selectedRowCnt = table.getRowCount();
                selectedRows = new int[selectedRowCnt];
                for (int i = 0; i < selectedRowCnt; i++) selectedRows[i] = i;
            }
            // if column selection is not allowed, emulate
            if (!table.getColumnSelectionAllowed()) {
                int lSelectedColCnt = table.getColumnCount();
                selectedCols = new int[lSelectedColCnt];
                for (int i = 0; i < lSelectedColCnt; i++) selectedCols[i] = i;
            }

            // do the actual paste logic
            paste(table, selectedRows, selectedCols, clipboardContents);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * The method actually processing the copy string
     */
    static public void paste(STableCore sTableCore, int[] selectedRows, int[] selectedCols, String clipboardContents) {
        boolean startedInLastRow = (selectedRows.length == 0 || selectedRows[selectedRows.length - 1] == sTableCore.getRowCount() - 1);

        // TBEERNOT?
        // disable sorting
        // doing this on sorted tables works... almost
        // the addRowAt will reapply sorting, but the setValues do not.
        // And we don't want that, because since the sorting is done by a model but we talk in sTableCore row indexes, the setValueAt may make the row move to another sTableCore index
        sTableCore.getSTable().cancelSorting();

        // split into rows
        String[] clipboardRows = Splitter.on(RECORD_SEPARATOR).splitToList(clipboardContents).toArray(new String[]{});
        for (int i = 0; i < clipboardRows.length; i++)
        {
            // get single row
            String clipboardRow = clipboardRows[i];
            if (logger.isDebugEnabled()) logger.debug("pasting row " + i + ": " + clipboardRow);

            // determine the row to paste in
            int rowIdx = (i < selectedRows.length ? selectedRows[i] : -1);

            // not enough rows but add rows allowed
            if (rowIdx < 0 && startedInLastRow && sTableCore.getSTable().getAllowInsertRows()) {
                rowIdx = sTableCore.getSTable().appendRow();
            }
            if (rowIdx < 0) {
                if (logger.isDebugEnabled()) logger.debug("skipping cell");
                continue;
            }
            if (logger.isDebugEnabled()) logger.debug("pasting to sTableCore row " + rowIdx);

            // split into columns (and thus individual cells)
            String[] lClipboardCols = Splitter.on(FIELD_SEPARATOR).splitToList(clipboardRow).toArray(new String[]{});
            for (int j = 0; j < lClipboardCols.length; j++) {

                // get cell value
                String value = lClipboardCols[j];
                if (logger.isDebugEnabled()) logger.debug("pasting from " + i + "," + j + ": " + value);

                // determine the column to paste in
                int colIdx = ( j < selectedCols.length ? selectedCols[j] : -1);
                if (colIdx < 0) {
                    if (logger.isDebugEnabled()) logger.debug("skipping cell");
                    continue;
                }
                if (logger.isDebugEnabled()) logger.debug("paste to sTableCore cell " + rowIdx + "," + colIdx + ": " + value);

                // if value location
                if ( rowIdx < sTableCore.getModel().getRowCount() // if we use the sTableCore.getRowCount() we get the filtered amount
                  && colIdx < sTableCore.getColumnCount()
                  && sTableCore.isCellEditable(rowIdx, colIdx)) {

                    // write value TBEERNOT: view to model mapping
                    sTableCore.getTableModel().setValueAtAsString(value, rowIdx, colIdx);
                }
            }
        }
    }
}
