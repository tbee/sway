package org.tbee.sway.action;

import org.tbee.sway.support.IconRegistry;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.Map;

public class JTableCopyCellAsText implements Action, ClipboardOwner {

    @Override
    public String label() {
        return "Copy cell contents";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find("copy", IconRegistry.Usage.MENU);
    }

    @Override
    public boolean isApplicableFor(Component component, Map<String, Object> context) {
        return component instanceof JTable;
    }

    @Override
    public boolean isEnabled(Component component, Map<String, Object> context) {
        JTable jTable = (JTable)component;
        MouseEvent mouseEvent = (MouseEvent)context.get("MouseEvent"); // MouseEvent information is required
        return jTable.isEnabled() && mouseEvent != null;
    }

    @Override
    public void apply(Component component, Map<String, Object> context) {
        JTable jTable = (JTable)component;
        MouseEvent mouseEvent = (MouseEvent)context.get("MouseEvent");

        // We know where we clicked in the toplevel component,
        // but in order to get to the row and column, we need to calculate the click point inside the table component
        // by substracting the offset of the table within the top component from the click location
        Point locationOfTheTopComponent = mouseEvent.getComponent().getLocationOnScreen();
        Point locationOfTheTable = SwingUtilities.getDeepestComponentAt(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY()).getLocationOnScreen();
        Point clickLocationInTopComponent = mouseEvent.getPoint();
        Point clickLocationInTable = new Point(clickLocationInTopComponent.x + (locationOfTheTopComponent.x - locationOfTheTable.x), clickLocationInTopComponent.y + (locationOfTheTopComponent.y - locationOfTheTable.y));

        // find which cell was hit
        int colIdx = jTable.columnAtPoint(clickLocationInTable);
        int rowIdx = jTable.rowAtPoint(clickLocationInTable);

        // do it
        String s = "" + jTable.getValueAt(rowIdx, colIdx);
        Clipboard lClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        lClipboard.setContents(new StringSelection(s), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do we need to do something here?
    }
}
