package org.tbee.sway;

import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.list.DefaultListCellRenderer;
import org.tbee.sway.support.SwayUtil;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class SComboBox<T> extends JComboBox<T> {

    public SComboBox() {
        setRenderer(new DefaultListCellRenderer(() -> format, () -> alternateRowColor, () -> firstAlternateRowColor, () -> secondAlternateRowColor));
        addActionListener (e -> fireSelectionChanged());
    }


    // =======================================================================
    // DATA

    private List<T> data = List.of();

    /**
     *
     * @param v
     */
    public void setData(List<T> v) {
// TBEERNOT        unregisterFromAllBeans();
        this.data = Collections.unmodifiableList(v); // We don't allow outside changes to the provided list
        setModel(new DefaultComboBoxModel<>(new Vector<>(this.data)));
// TBEERNOT       registerToAllBeans();
    }
    public List<T> getData() {
        return this.data;
    }
    public SComboBox<T> data(List<T> v) {
        setData(v);
        return this;
    }

    // ===========================================================================
    // RENDERING

    private Format<T> format = null;

    // TBEERNOT not sure how to do this, but I know this API needs to be there. TTD? :-D
    /**
     *
     * @param v
     * @return
     */
    public SComboBox<T> render(Format<T> v) {
        this.format = v;
        return this;
    }

    // TBEERNOT not sure how to do this, but I know this API needs to be there. TTD? :-D
    /**
     *
     * @param clazz
     * @return
     */
    public SComboBox<T> renderFor(Class<T> clazz) {
        return render((Format<T>) FormatRegistry.findFor(clazz));
    }

    // ===========================================================================
    // ALTERNATE ROW COLORS

    /** Alternate the background color for rows */
    public void setAlternateRowColor(boolean v) {
        firePropertyChange(ALTERNATEROWCOLOR, this.alternateRowColor, this.alternateRowColor = v);
    }
    public boolean getAlternateRowColor() {
        return alternateRowColor;
    }
    private boolean alternateRowColor = true;
    final static public String ALTERNATEROWCOLOR = "alternateRowColor";
    public SComboBox<T> alternateRowColor(boolean v) {
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
    public SComboBox<T> firstAlternateRowColor(Color v) {
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
    public SComboBox<T> secondAlternateRowColor(Color v) {
        setSecondAlternateRowColor(v);
        return this;
    }


    // ===========================================================================
    // SELECTION

    /**
     *
     * @return
     */
    public T getSelection() {
        return (T)getSelectedItem();
    }

    /**
     *
     */
    public void setSelection(T v) {
        setSelectedItem(v);
    }

    /**
     *
     */
    public void clearSelection() {
        setSelectedItem(null);
    }

    /**
     *
     * @param listener
     */
    synchronized public void addSelectionChangedListener(Consumer<T> listener) {
        if (selectionChangedListeners == null) {
            selectionChangedListeners = new ArrayList<>();
        }
        selectionChangedListeners.add(listener);
    }
    synchronized public boolean removeSelectionChangedListener(Consumer<T> listener) {
        if (selectionChangedListeners == null) {
            return false;
        }
        return selectionChangedListeners.remove(listener);
    }
    private List<Consumer<T>> selectionChangedListeners;
    private void fireSelectionChanged() {
        if (selectionChangedListeners != null) {
            selectionChangedListeners.forEach(scl -> scl.accept(getSelection()));
        }
    }

    /**
     * @param onSelectionChangedListener
     * @return
     */
    public SComboBox<T> onSelectionChanged(Consumer<T> onSelectionChangedListener) {
        addSelectionChangedListener(onSelectionChangedListener);
        return this;
    }


    // ========================================================
    // OF

    static public <T> SComboBox<T> of() {
        return new SComboBox<T>();
    }

    static public <T> SComboBox<T> of(Format<T> format) {
        return new SComboBox<T>().render(format);
    }

    static public <T> SComboBox<T> of(Class<T> clazz) {
        Format<T> format = (Format<T>) FormatRegistry.findFor(clazz);
        if (format == null) {
            throw new IllegalArgumentException("No format found for " + clazz);
        }
        return of(format);
    }


    // ===========================================================================
    // FLUENT API

    public SComboBox<T> name(String v) {
        setName(v);
        return this;
    }

    public SComboBox<T> visible(boolean value) {
        setVisible(value);
        return this;
    }

    static public <T> SComboBox<T> of(List<T> data) {
        return new SComboBox<T>().data(data);
    }
}
