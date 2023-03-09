package org.tbee.sway;

import javax.swing.JComponent;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Collection;

public class SHorizontalPanel extends SPanelExtendable<SHorizontalPanel> {

    public SHorizontalPanel() {
        setLayout(new GridLayout(1, 1));
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        ((GridLayout)getLayout()).setColumns(getComponentCount());
    }

    static public SHorizontalPanel of() {
        return new SHorizontalPanel();
    }

    public static SHorizontalPanel of(JComponent... components) {
        SHorizontalPanel sVerticalPanel = of();
        sVerticalPanel.add(components);
        return sVerticalPanel;
    }

    public static SHorizontalPanel of(Collection<? extends JComponent> components) {
        SHorizontalPanel sVerticalPanel = of();
        sVerticalPanel.add(components);
        return sVerticalPanel;
    }


    public int getGap() {
        return ((GridLayout)getLayout()).getHgap();
    }
    public void setGap(int v) {
        ((GridLayout)getLayout()).setHgap(v);
    }
    public SHorizontalPanel gap(int v) {
        setGap(v);
        return this;
    }
}
