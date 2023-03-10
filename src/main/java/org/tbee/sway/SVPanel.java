package org.tbee.sway;

import javax.swing.JComponent;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.Collection;

public class SVPanel extends SPanelExtendable<SVPanel> {

    public SVPanel() {
       setLayout(new GridLayout(1, 1) {
           @Override
           public void layoutContainer(Container parent) {
               setRows(getComponentCount());
               super.layoutContainer(parent);
           }
       });
    }

    static public SVPanel of() {
        return new SVPanel();
    }


    public static SVPanel of(JComponent... components) {
        SVPanel panel = of();
        panel.add(components);
        return panel;
    }

    public static SVPanel of(Collection<? extends JComponent> components) {
        SVPanel panel = of();
        panel.add(components);
        return panel;
    }

    public int getGap() {
        return ((GridLayout)getLayout()).getVgap();
    }
    public void setGap(int v) {
        ((GridLayout)getLayout()).setVgap(v);
    }
    public SVPanel gap(int v) {
        setGap(v);
        return this;
    }
}
