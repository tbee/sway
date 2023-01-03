package org.tbee.sway.list;

import org.tbee.sway.SList;

import java.awt.Color;
import java.awt.Component;

public class DefaultListCellRenderer extends javax.swing.DefaultListCellRenderer {

    private final SList<?> sList;

    public DefaultListCellRenderer(SList<?> sList) {
        this.sList = sList;
    }

    @Override
    public Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Alternating row color
        if (sList.getAlternateRowColor()) {
            if (!sList.getSListCore().isSelectedIndex(index)) {
                Color color = ((index % 2 != 0) ? sList.getFirstAlternateRowColor() : sList.getSecondAlternateRowColor());
                if (component.getBackground() != color) {
                    component.setBackground(color);
                }
            }
        }

        // Done
        return component;
    }

}
