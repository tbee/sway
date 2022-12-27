package org.tbee.sway.table;

import org.tbee.sway.STextField;
import org.tbee.sway.format.Format;

public class FormatCellEditor<T> extends javax.swing.DefaultCellEditor {

    final private Format<T> format;

    public FormatCellEditor(Format<T> format) {
        super(new STextField(format));
        this.format = format;
    }

    @Override
    public Object getCellEditorValue() {
        return ((STextField<T>)getComponent()).getValue();
    }
}
