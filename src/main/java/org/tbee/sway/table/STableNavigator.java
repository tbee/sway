package org.tbee.sway.table;


import org.tbee.sway.STableCore;
import org.tbee.sway.SwingUtil;
import org.tbee.sway.comedia.text.CNumericDocument;
import org.tbee.sway.comedia.ui.CEncodedIcon;
import org.tbee.sway.support.MigLayoutUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Copy and adapted from CoMedia cbeans
 */
public class STableNavigator extends JPanel implements ListSelectionListener, TableModelListener, ActionListener, FocusListener, PropertyChangeListener {

    JButton firstButton = new JButton();
    JButton priorButton = new JButton();
    JTextField recordText = new JTextField();
    JButton nextButton = new JButton();
    JButton lastButton = new JButton();
    JButton lastNewButton = new JButton();
    JLabel recordLabel = new JLabel();
    Border border1;

    /**
     * The related table control.
     */
    STableCore table = null;

    /**
     * The last selection row.
     */
    private int lastSelection = 0;

    /**
     * The integer model for text editor.
     */
    final private CNumericDocument document = new CNumericDocument();

    /**
     * Constructs this panel and assigns the main parameters.
     *
     * @param table a related table control.
     */
    public STableNavigator(STableCore table) {
        construct();
        setTable(table);
    }

    /**
     * Initializes visual controls of this console.
     */
    private void construct() {
        // defaults
        priorButton.setActionCommand("Prior");
        firstButton.setActionCommand("First");
        recordText.setName(this.getClass().getSimpleName() + "-recordText");
        nextButton.setActionCommand("Next");
        lastButton.setActionCommand("Last");
        lastNewButton.setActionCommand("LastNew");
        recordLabel.setText(" / 0");
        recordLabel.setOpaque(true); // we get paint errors if we do not do this, because underlying panels may be transparent

        // setup the buttons
        initButton(firstButton, CEncodedIcon.FIRST_SIGN);
        initButton(priorButton, CEncodedIcon.PRIOR_SIGN);
        initButton(nextButton, CEncodedIcon.NEXT_SIGN);
        initButton(lastButton, CEncodedIcon.LAST_SIGN);
        initButton(lastNewButton, CEncodedIcon.LAST_NEW_SIGN);

        // setup record text
        recordText.setEnabled(false);
        recordText.setHorizontalAlignment(JLabel.RIGHT);
        recordText.addActionListener(this);
        recordText.setActionCommand("Goto");
        recordText.addFocusListener(this);
        recordText.setDocument(document);
        recordText.setText("0");

        // sizes based on the icon image
        priorButton.setPreferredSize(new Dimension(21, 17));
        firstButton.setPreferredSize(new Dimension(21, 17));
        recordText.setPreferredSize(new Dimension(80, 19));
        nextButton.setPreferredSize(new Dimension(21, 17));
        lastButton.setPreferredSize(new Dimension(21, 17));
        lastNewButton.setPreferredSize(new Dimension(21, 17));

        // layout
        this.setLayout(MigLayoutUtil.newMigLayoutFillNoGaps());
        this.add(firstButton, MigLayoutUtil.newCC().growY());
        this.add(priorButton, MigLayoutUtil.newCC().growY());
        this.add(recordText, MigLayoutUtil.newCC().growY());
        this.add(nextButton, MigLayoutUtil.newCC().growY());
        this.add(lastButton, MigLayoutUtil.newCC().growY());
        this.add(lastNewButton, MigLayoutUtil.newCC().growY());
        this.add(recordLabel, MigLayoutUtil.newCC().growY().growX().pushX());
    }

    /**
     * Initializes a button with icon and listeners.
     *
     * @param button a button to initialize.
     * @param icon   a icon for the button.
     */
    private void initButton(JButton button, CEncodedIcon icon) {
        // setup icons
        button.setIcon(icon);
        // create disabled icon
        icon = new CEncodedIcon(icon.getImageArray(), icon.getIconWidth(), icon.getIconHeight());
        icon.setEnabled(false);
        button.setDisabledIcon(icon);

        // setup button
        button.setEnabled(false);
        button.addActionListener(this);
        button.setFocusPainted(false);
        button.setRequestFocusEnabled(false);
        button.setMargin(SwingUtil.EMPTY_INSETS);
    }

    /**
     * Performs an event when selection of the related table is changed.
     *
     * @param e an object which describes occured event.
     */
    public void valueChanged(ListSelectionEvent e) {
        updateContent();
    }

    /**
     * Performs an event when model of the related table is changed.
     *
     * @param e an object which describes occured event.
     */
    public void tableChanged(TableModelEvent e) {
        // since Java 1.6 table.getRowCount() cannot be done from inside a table model listener, and has to be postponed until after all listeners have executed
        // http://java.net/jira/browse/SWINGX-1520
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateContent();
            }
        });
    }

    /**
     * Gets the related table control.
     *
     * @result the related table control.
     */
    public STableCore getTable() {
        return table;
    }

    /**
     * Sets a new related table control.
     *
     * @param table a related table control.
     */
    public void setTable(STableCore table) {
        if (this.table != null) {
            // TBEERNOT this.table.removePropertyChangeListener(STable.SORTERCHANGED_PROPERTYID, this);
            this.table.getSelectionModel().removeListSelectionListener(this);
            this.table.getModel().removeTableModelListener(this);
        }
        this.table = table;
        this.lastSelection = (table.getSelectedRow() >= 0) ? table.getSelectedRow() : 0;
        if (this.table != null) {
            // TBEERNOT this.table.addPropertyChangeListener(STable.SORTERCHANGED_PROPERTYID, this);
            this.table.getSelectionModel().addListSelectionListener(this);
            this.table.getModel().addTableModelListener(this);
            updateContent();
        }
    }

    /**
     * Updates the contents of this scroller.
     */
    private void updateContent() {
        if (table == null) {
            firstButton.setEnabled(false);
            priorButton.setEnabled(false);
            nextButton.setEnabled(false);
            lastButton.setEnabled(false);
            lastNewButton.setEnabled(false);
            recordText.setEnabled(false);
            recordLabel.setText(" / 0");
            validate();
            return;
        }

        // Count the numbers
        int currentRow = (table.getSelectedRow() >= 0) ? table.getSelectedRow() : 0;
        if (table.getSelectedRowCount() > 1) currentRow = lastSelection;
        else lastSelection = currentRow;
        int insertRow = -1; // TBEERNOT ((table instanceof JTableForEdit) && ((JTableForEdit) table).getAllowInsertRows()) ? table.getRowCount() : -1;
        int rowCount = table.getRowCount();
        int selectionCount = 0;
        table.getSelectedRowCount();

        firstButton.setEnabled(currentRow > 0);
        priorButton.setEnabled(firstButton.isEnabled());
        lastButton.setEnabled(currentRow < rowCount);
        lastNewButton.setEnabled(insertRow >= 0 && currentRow != insertRow);
        nextButton.setEnabled(lastButton.isEnabled() || lastNewButton.isEnabled());
        recordText.setEnabled(rowCount > 0);
        recordText.setText("" + (currentRow + 1));
        recordLabel.setText(" / " + rowCount + (selectionCount == 0 ? "" : " [" + selectionCount + "]"));

        validate();
    }

    /**
     * Performs actions from user interface.
     *
     * @param e description of the action.
     */
    public void actionPerformed(ActionEvent e) {
        // I have no idea what so ever why an enter in an editing cell triggers this action event
        // but as long as the table is editing, navigation is not done
        if (table.isEditing()) return;

        String cmd = e.getActionCommand();

        // Count the numbers
        int currentRow = (table.getSelectedRow() >= 0) ? table.getSelectedRow() : 0;
        if (table.getSelectedRowCount() > 1) currentRow = lastSelection;
        else lastSelection = currentRow;
        int insertRow = -1; // TBEERNOT ((table instanceof JTableForEdit) && ((JTableForEdit) table).getAllowInsertRows()) ? table.getRowCount() : -1;
        int rowCount = table.getRowCount();

        if (cmd.equals("First")) gotoRow(0);
        else if (cmd.equals("Prior")) gotoRow(currentRow - 1);
        else if (cmd.equals("Next")) gotoRow(currentRow + 1);
        else if (cmd.equals("Last")) gotoRow(rowCount - 1);
        else if (cmd.equals("LastNew")) gotoRow(insertRow);
        else if (cmd.equals("Goto")) {
            int row = Integer.parseInt(recordText.getText());
            gotoRow(row - 1);
        }
    }

    /**
     * Performs event when this table box gets a focus.
     *
     * @param e an object which described occured event.
     */
    public void focusGained(FocusEvent e) {
    }

    /**
     * Performs event when this table box lost a focus.
     *
     * @param e an object which described occured event.
     */
    public void focusLost(FocusEvent e) {
        if (document.isModified()) recordText.postActionEvent();
    }

    /**
     * Moves the pointer in the table.
     *
     * @param row the selected row.
     */
    private void gotoRow(int row) {
        // TBEERNOT
//        if (row == table.getRowCount()) {
//            TableModel lTableModel = table.getModel();
//            if (lTableModel instanceof TableSorter) lTableModel = ((TableSorter) lTableModel).getTableModel();
//            ((TableModelForEdit) table.getModel()).addRowAt(table.getRowCount());
//        }

        if (table != null && row >= 0 && row < table.getRowCount()) {
            table.removeEditor();
            table.setRowSelectionInterval(row, row);

            if (table.getAutoscrolls()) {
                Rectangle cellRect = table.getCellRect(row, table.getSelectedColumn(), false);
                if (cellRect != null) {
                    table.scrollRectToVisible(cellRect);
                }
            }
        }
    }

    /**
     * the colow of the indexfield background
     */
    public Color getIndexBackground() {
        return recordText.getBackground();
    }

    public void setIndexBackground(Color c) {
        recordText.setBackground(c);
    }

    /**
     *
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateContent();
    }
}