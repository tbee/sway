package org.tbee.sway;

import javax.swing.JComponent;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Collection;

public class SVerticalPanel extends SPanelExtendable<SVerticalPanel> {

    public SVerticalPanel() {
       setLayout(new GridLayout(1, 1));
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        ((GridLayout)getLayout()).setRows(getComponentCount());
    }

    static public SVerticalPanel of() {
        return new SVerticalPanel();
    }


    public static SVerticalPanel of(JComponent... components) {
        SVerticalPanel sVerticalPanel = of();
        sVerticalPanel.add(components);
        return sVerticalPanel;
    }

    public static SVerticalPanel of(Collection<? extends JComponent> components) {
        SVerticalPanel sVerticalPanel = of();
        sVerticalPanel.add(components);
        return sVerticalPanel;
    }
}
