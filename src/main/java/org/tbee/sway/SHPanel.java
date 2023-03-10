package org.tbee.sway;

import javax.swing.JComponent;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.Collection;

public class SHPanel extends SPanelExtendable<SHPanel> {

    public SHPanel() {
        setLayout(new GridLayout(1, 1) {
            @Override
            public void layoutContainer(Container parent) {
                setColumns(getComponentCount());
                super.layoutContainer(parent);
            }
        });
    }

    static public SHPanel of() {
        return new SHPanel();
    }

    public static SHPanel of(JComponent... components) {
        SHPanel panel = of();
        panel.add(components);
        return panel;
    }

    public static SHPanel of(Collection<? extends JComponent> components) {
        SHPanel panel = of();
        panel.add(components);
        return panel;
    }


    public int getGap() {
        return ((GridLayout)getLayout()).getHgap();
    }
    public void setGap(int v) {
        ((GridLayout)getLayout()).setHgap(v);
    }
    public SHPanel gap(int v) {
        setGap(v);
        return this;
    }
}
