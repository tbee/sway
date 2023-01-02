package org.tbee.sway.table;

import org.tbee.sway.format.Format;

import javax.swing.JLabel;
import javax.swing.JTable;
import java.awt.Component;

public class FormatCellRenderer<T> extends javax.swing.table.DefaultTableCellRenderer  {

    final private Format<T> format;

    public FormatCellRenderer(Format<T> format) {
        super();
        this.format = format;
    }

    public Format<T> getFormat() {
        return format;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel jLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        jLabel.setText(format.toString((T) value));
        jLabel.setHorizontalAlignment(format.horizontalAlignment().getSwingConstant());
        return jLabel;
    }
}
