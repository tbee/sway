package org.tbee.sway.list;

import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.util.function.Supplier;

public class DefaultListCellRenderer<T> extends javax.swing.DefaultListCellRenderer {


    final private Supplier<Boolean> alternateRowColorSupplier;
    final private Supplier<Color> firstAlternateRowColorSupplier;
    final private Supplier<Color> secondAlternateRowColorSupplier;
    final private Supplier<Format<T>> formatSupplier;
    public DefaultListCellRenderer(Supplier<Format<T>> formatSupplier, Supplier<Boolean> alternateRowColorSupplier, Supplier<Color> firstAlternateRowColorSupplier, Supplier<Color> secondAlternateRowColorSupplier) {
        this.formatSupplier = formatSupplier;
        this.alternateRowColorSupplier = alternateRowColorSupplier;
        this.firstAlternateRowColorSupplier = firstAlternateRowColorSupplier;
        this.secondAlternateRowColorSupplier = secondAlternateRowColorSupplier;
    }

    @Override
    public Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Get values
        Format format = formatSupplier == null ? null : formatSupplier.get();
        if (format == null && value != null) {
            format = FormatRegistry.findFor(value.getClass());
        }
        Color firstAlternateRowColor = firstAlternateRowColorSupplier == null ? null : firstAlternateRowColorSupplier.get();
        Color secondAlternateRowColor = secondAlternateRowColorSupplier == null ? null : secondAlternateRowColorSupplier.get();
        boolean alternateRowColor = alternateRowColorSupplier == null ? false : alternateRowColorSupplier.get();

        // Apply format
        if (format != null && component instanceof JLabel jLabel) {
            jLabel.setText(format.toString((T)value));
            jLabel.setIcon(format.toIcon((T)value));
            jLabel.setHorizontalAlignment(format.horizontalAlignment().getSwingConstant());
        }

        // Alternate row color
        if (!isSelected) {
            if (alternateRowColor) {
                Color color = ((index % 2 != 0) ? firstAlternateRowColor : secondAlternateRowColor);
                if (component.getBackground() != color) {
                    component.setBackground(color);
                }
            }
        }

        // Done
        return component;
    }

}
