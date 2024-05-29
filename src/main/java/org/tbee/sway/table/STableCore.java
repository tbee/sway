package org.tbee.sway.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.tbee.sway.STable;
import org.tbee.sway.preference.PreferenceHelper;
import org.tbee.sway.support.FocusInterpreter;

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

        // key shortcuts
        addKeyShortcut("copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false), e -> sTable.copy());
        addKeyShortcut("paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false), e -> sTable.paste());
        addKeyShortcut("cut", KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false), e -> sTable.cut());
        addKeyShortcut("deleteSelectedRows", KeyStroke.getKeyStroke("DELETE"), e -> STableCore.this.sTable.deleteSelectedRows());
    }
    private FocusInterpreter.FocusInterpreterListener focusInterpreterListener = null;
    final private FocusInterpreter focusInterpreter = new FocusInterpreter(this);

    public STable<TableType> getSTable() {
        return sTable;
    }

    /**
     * To simply creating keyboard shortcuts
     * Uses the inputMap and actionMap.
     */
    public void addKeyShortcut(String id, KeyStroke keyStroke, Consumer<ActionEvent> consumer) {
		getInputMap().put(keyStroke, id);
		getActionMap().put(id, action(consumer));    	
    }
    private AbstractAction action(Consumer<ActionEvent> consumer) {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				consumer.accept(e);
			}
		};
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
    	column = convertColumnIndexToModel(column);
        TableCellRenderer renderer = sTable.getTableColumns().get(column).getRenderer();
        if (renderer != null) {
            return renderer;
        }

        // Default behavior
        return super.getCellRenderer(row, column);
    }


    @Override
    public TableCellEditor getCellEditor(int row, int column) {
    	column = convertColumnIndexToModel(column);
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
    
    
	// ===========================================================================
	// Preferences

	// TODO: use PreferenceHelper
    private String getNameForPreferences() {
    	return this.sTable.getPreferencesId();
    }
    private boolean getAutoSavePreferences() {
    	return this.sTable != null && this.sTable.getPreferencesId() != null;
    }

    /**
     * save all preferences
     */
    public void savePreferences() {
    	saveColumnVisiblePreferences();
    	saveColumnWidthPreferences();
    	saveColumnOrderPreferences();
    }

    /**
     * restore all preferences
     */
    public void restorePreferences() {
		// if we're not valid than all kind of column model initialization is not done yet
    	restoreColumnVisiblePreferences();
    	restoreColumnWidthPreferences();
    	restoreColumnOrderPreferences();
    }
    private int restoringPreferences = 0; // changes to swing components should all be done in the EDT, so all changes to this variable is single threaded

    /**
     * For monitoring preference related changes with auto save
     */
	public void columnMarginChanged(ChangeEvent e) {
		super.columnMarginChanged(e);
		saveColumnWidthPreferences();
	}

    /**
     * For monitoring preference related changes with auto save
     */
	public void setAutoResizeMode(int v) {
		super.setAutoResizeMode(v);
		saveColumnWidthPreferences();
	}

	/**
	 * a column move must abort the edit
	 */
	public void columnMoved(TableColumnModelEvent e)
	{
		sTable.stopEdit();
		super.columnMoved(e);
		saveColumnOrderPreferences();
	}

    /** Tells listeners that a column was added to the model. */
    public void columnAdded(TableColumnModelEvent e)
	{
    	sTable.stopEdit();
		super.columnAdded(e);
		savePreferences();
	}


    /** Tells listeners that a column was removed from the model. */
    public void columnRemoved(TableColumnModelEvent e)
	{
    	sTable.stopEdit();
		super.columnRemoved(e);
		savePreferences();
	}


	// --------------------------
	// column width

    final static private String COLUMN_WIDTH_ID = ".CW.";

    /**
     *
     */
    public void saveColumnWidthPreferences() {
    	if (!getAutoSavePreferences() || restoringPreferences > 0) {
    		return;
    	}

    	// get the preferences
    	Preferences preferences = Preferences.userNodeForPackage(this.getClass());

    	// clear old values
    	try {
    		for (String key : preferences.keys() ) {
    			if (key.startsWith(getNameForPreferences() + COLUMN_WIDTH_ID)) {
    				preferences.remove(key);
    			}
    		}
    	}
    	catch (BackingStoreException e) { 
    		logger.warn(e.getMessage(), e); 
    	}

    	// save autoresize mode
		preferences.put(getNameForPreferences() + ".ARM", "" + getAutoResizeMode() );

    	// save column widths
		for (int i = 0; i < getColumnCount(); i++) {
			int modelIdx = getColumnModel().getColumn(i).getModelIndex();
			String key = getNameForPreferences() + COLUMN_WIDTH_ID + modelIdx;
			if (logger.isDebugEnabled()) logger.debug(key + "  <-  " + getColumnModel().getColumn(i).getPreferredWidth());
			preferences.put(key, "" + getColumnModel().getColumn(i).getPreferredWidth() );
		}
    }

    /**
     *
     */
    public void restoreColumnWidthPreferences() {
    	if (!getAutoSavePreferences()) {
    		return;
    	}

    	// get the preferences
    	Preferences preferences = Preferences.userNodeForPackage(this.getClass());
    	restoringPreferences++;
    	try {
    		
	    	// restore auto resize mode
	    	String value = preferences.get(getNameForPreferences() + ".ARM", null);
	    	if (value != null) {
	    		setAutoResizeMode(Integer.parseInt(value));
	    	}

	    	// restore column widths
			for (int i = 0; i < getColumnCount(); i++) {
				int lModelIdx = getColumnModel().getColumn(i).getModelIndex();
				String key = getNameForPreferences() + COLUMN_WIDTH_ID + lModelIdx;
				value = preferences.get(key, null);
				if ( value != null) {
					if (logger.isDebugEnabled()) logger.debug(key + "  ->  " + value);
					getColumnModel().getColumn(i).setPreferredWidth( Integer.parseInt( value ) );
				}
			}
    	}
    	finally {
			restoringPreferences--;
    	}
    }

	// --------------------------

    final static private String COLUMN_ORDER_ID = ".CO.";

	/**
     *
     */
    public void saveColumnOrderPreferences() {
    	if (!getAutoSavePreferences() || restoringPreferences > 0) {
    		return;
    	}

    	// get the preferences
    	Preferences preferences = Preferences.userNodeForPackage(this.getClass());

    	// clear old values
    	try {
    		for (String key : preferences.keys() ) {
    			if (key.startsWith(getNameForPreferences() + COLUMN_ORDER_ID)) {
    				preferences.remove(key);
    			}
    		}
    	}
    	catch (BackingStoreException e) { 
    		logger.warn(e.getMessage(), e); 
		}

    	// scan through all columns
    	// - CO1 = model index of the column present as the nr 1 in the view
    	// - CO2 = model index of the column present as the nr 2 in the view
		for (int i = 0; i < getColumnCount(); i++) {
			// store value
			String key = getNameForPreferences() + COLUMN_ORDER_ID + i;
			if (logger.isDebugEnabled()) logger.debug(key + "  <-  " + getColumnModel().getColumn(i).getModelIndex());
			preferences.put(key, "" + getColumnModel().getColumn(i).getModelIndex() );
		}
    }

    /**
     *
     */
    public void restoreColumnOrderPreferences() {
    	if (!getAutoSavePreferences()) {
    		return;
    	}

    	// get the preferences
    	Preferences preferences = Preferences.userNodeForPackage(this.getClass());
    	restoringPreferences++;
    	try {
	    	// restore column location
			for (int i = 0; i < getColumnCount(); i++) {
				
				String key = getNameForPreferences() + COLUMN_ORDER_ID + i;
				String value = preferences.get(key, null);
				if (value != null) {
					
					// convert to int
					if (logger.isDebugEnabled()) logger.debug(key + "  ->  " + value);
					int lModelIdx = Integer.parseInt(value);

					// find the view index of the model idx
					for (int j = i; j < getColumnCount(); j++) {
						if ( getColumnModel().getColumn(j).getModelIndex() == lModelIdx && j != i) {
							if (logger.isDebugEnabled()) logger.debug("moving view column " + j + "  to  " + i);
							moveColumn(j, i);
						}
					}
				}
			}
    	}
    	finally {
			restoringPreferences--;
    	}
    }

	// --------------------------

    final static private String COLUMN_HIDDEN_ID = ".CV.";

	/**
     *
     */
    public void saveColumnVisiblePreferences() {
    	if (!getAutoSavePreferences() || restoringPreferences > 0) {
    		return;
    	}

    	// get the preferences
    	Preferences preferences = Preferences.userNodeForPackage(this.getClass());

    	// clear old values
    	try {
    		for (String key : preferences.keys() ) {
    			if (key.startsWith(getNameForPreferences() + COLUMN_HIDDEN_ID)) {
    				preferences.remove(key);
    			}
    		}
    	}
    	catch (BackingStoreException e) { 
    		logger.warn(e.getMessage(), e); 
    	}

    	// we check all table model columns to see if they exist in the column model
    	// first construct a list of all columns
    	List<Integer> hiddenColumns = new ArrayList<Integer>();
		for (int i = 0; i < getModel().getColumnCount(); i++) {
			hiddenColumns.add(i);
		}

    	// now remove those that are visible from the list (so the hidden ones remain)
		for (int i = 0; i < getColumnCount(); i++) {
			hiddenColumns.remove(getColumnModel().getColumn(i).getModelIndex());
		}
		if (logger.isDebugEnabled()) logger.debug("Hidden columns=" + hiddenColumns);

		// store which model indexes are hidden
		for (int i = 0; i < hiddenColumns.size(); i++) {
			String key = getNameForPreferences() + COLUMN_HIDDEN_ID + hiddenColumns.get(i);
			if (logger.isDebugEnabled()) logger.debug(key + " = hidden");
			preferences.put(key, "hidden" );
		}
    }

    /**
     *
     */
    public void restoreColumnVisiblePreferences() {
    	if (!getAutoSavePreferences()) {
    		return;
    	}

    	// get the preferences
    	Preferences preferences = Preferences.userNodeForPackage(this.getClass());
    	restoringPreferences++;
    	try {
	    	// scan all columns in the table model
			for (int i = 0; i < getModel().getColumnCount(); i++) {
				
				// if the key exists
				String key = getNameForPreferences() + COLUMN_HIDDEN_ID + i;
				String value = preferences.get(key, null);
				if (logger.isDebugEnabled()) logger.debug(key + "  ->  " + value);
				if ( value != null) {
					
					// find the index in the view
					for (int j = 0; j < getColumnCount(); j++) {
						// if this view index has the same model index
						if ( getColumnModel().getColumn(j).getModelIndex() == i) {
							// hide column
							// NOTE: we're using JXTable's column.setVisible instead of removeColumn, because removing columns results in all kinds of unexpected behavior.
							if (logger.isDebugEnabled()) logger.debug("hiding column modelidx=" + i + ", viewidx="  + j);
// TBEERNOT							((TableColumnModelExt)getColumnModel()).getColumnExt(j).setVisible(false);
// import org.jdesktop.swingx.table.TableColumnModelExt;
						}
					}
				}
			}
    	}
    	finally {
			restoringPreferences--;
    	}
    }
}