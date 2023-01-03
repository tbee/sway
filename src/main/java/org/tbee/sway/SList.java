package org.tbee.sway;

import org.tbee.sway.list.DefaultListCellRenderer;
import org.tbee.sway.list.SListCore;
import org.tbee.sway.support.SwayUtil;

import javax.swing.JScrollPane;
import java.awt.Color;
import java.util.List;

public class SList<T> extends SBorderPanel {

    final private SListCore<T> sListCore = new SListCore<>();
    final private JScrollPane jScrollPane = new JScrollPane(sListCore);

    public SList() {
        super();
        center(jScrollPane);
        sListCore.setCellRenderer(new DefaultListCellRenderer(this));
    }

    public SListCore<T> getSListCore() {
        return sListCore;
    }

    // =======================================================================
    // DATA

    /**
     *
     * @param v
     */
    public void setData(List<T> v) {
        sListCore.getListModel().setData(v);
    }
    public List<T> getData() {
        return sListCore.getListModel().getData();
    }
    public SList<T> data(List<T> v) {
        setData(v);
        return this;
    }

    // ===========================================================================
    // RENDERING e.g. alternate row colors

    /** Alternate the background color for rows */
    public void setAlternateRowColor(boolean v) {
        firePropertyChange(ALTERNATEROWCOLOR, this.alternateRowColor, this.alternateRowColor = v);
    }
    public boolean getAlternateRowColor() {
        return alternateRowColor;
    }
    private boolean alternateRowColor = true;
    final static public String ALTERNATEROWCOLOR = "alternateRowColor";
    public SList<T> alternateRowColor(boolean v) {
        setAlternateRowColor(v);
        return this;
    }

    /** The color to use for the alternating background color for rows */
    public void setFirstAlternateRowColor(Color v) {
        firePropertyChange(FIRSTALTERNATEROWCOLOR, this.firstAlternateRowColor, this.firstAlternateRowColor = v);
    }
    public Color getFirstAlternateRowColor() {
        return firstAlternateRowColor;
    }
    private Color firstAlternateRowColor = SwayUtil.getFirstAlternateRowColor();
    final static public String FIRSTALTERNATEROWCOLOR = "firstAlternateRowColor";
    public SList<T> firstAlternateRowColor(Color v) {
        firstAlternateRowColor(v);
        return this;
    }

    /** The second color to use for the alternating background color for rows */
    public void setSecondAlternateRowColor(Color v) {
        firePropertyChange(SECONDALTERNATEROWCOLOR, this.secondAlternateRowColor, this.secondAlternateRowColor = v);
    }
    public Color getSecondAlternateRowColor() {
        return secondAlternateRowColor;
    }
    private Color secondAlternateRowColor = SwayUtil.getSecondAlternateRowColor();
    final static public String SECONDALTERNATEROWCOLOR = "secondAlternateRowColor";
    public SList<T> secondAlternateRowColor(Color v) {
        setSecondAlternateRowColor(v);
        return this;
    }


    // ===========================================================================
    // FLUENT API

    @Override
    public void setName(String v) {
        super.setName(v);
        sListCore.setName(v + ".sListCore"); // For tests we need to address the actual list
    }
    public SList<T> name(String v) {
        setName(v);
        return this;
    }

    public SList<T> visible(boolean value) {
        setVisible(value);
        return this;
    }
}
