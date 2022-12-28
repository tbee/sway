package org.tbee.sway.table;

import org.tbee.sway.STextField;
import org.tbee.sway.format.Format;
import org.tbee.sway.support.SwayUtil;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import java.awt.Component;

public class FormatCellEditor<T> extends javax.swing.DefaultCellEditor {

    final private Format<T> format;

    public FormatCellEditor(Format<T> format) {
        super(new STextField(format));
        this.format = format;
        ((JComponent)getComponent()).setBorder(new LineBorder(SwayUtil.getHighlightColor()));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        STextField<T> sTextField = (STextField<T>) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        sTextField.setValue((T)value);
        return sTextField;
    }

    @Override
    public Object getCellEditorValue() {
        STextField<T> sTextField = (STextField<T>) getComponent();
        sTextField.setValueFromText(); // force the textfield to parse the value, since it has not had a lost focus event and done so itself
        T value = sTextField.getValue();
        return value;
    }
}
