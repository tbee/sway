package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.list.DefaultListCellRenderer;
import org.tbee.sway.list.SListCore;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.ExceptionHandlerDefaultMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.SelectionMixin;
import org.tbee.sway.support.SwayUtil;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SList<T> extends JPanel implements
        JComponentMixin<SList<T>>,
        ExceptionHandlerDefaultMixin<SList<T>>,
        SelectionMixin<SList<T>, T>,
        BindToMixin<SList<T>, List<T>> {
    static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SList.class);

    final private SListCore<T> sListCore;
    final private JScrollPane jScrollPane;

    public SList() {
        setLayout(new BorderLayout());
        sListCore = new SListCore<>(this);
        jScrollPane = new JScrollPane(sListCore);
        add(jScrollPane, BorderLayout.CENTER);
        sListCore.setCellRenderer(new DefaultListCellRenderer(() -> format, () -> alternateRowColor, () -> firstAlternateRowColor, () -> secondAlternateRowColor));

        // Start listening for selection changes
        sListCore.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                var selectedItems = getSelection();
                if (selectionChangedListeners != null) {
                    selectionChangedListeners.forEach(l -> l.accept(selectedItems));
                }
                firePropertyChange(SELECTION, null, selectedItems);
            }
        });
    }

    public SListCore<T> getSListCore() {
        return sListCore;
    }

    // ===========================================================================================================================
    // For Mixins

    @Override
    public BindingEndpoint<List<T>> defaultBindingEndpoint() {
        return selection$();
    }


    // =======================================================================
    // DATA

    private List<T> data = List.of();

    /**
     *
     * @param v
     */
    public void setData(List<T> v) {
// TBEERNOT       unregisterFromAllBeans();
        this.data = Collections.unmodifiableList(v); // We don't allow outside changes to the provided list
// TBEERNOT       registerToAllBeans();
    }
    public List<T> getData() {
        return this.data;
    }
    public SList<T> data(List<T> v) {
        setData(v);
        refresh();
        return this;
    }

    public void refresh() {
        sListCore.getListModel().contentsChanged();
    }

    // ===========================================================================
    // RENDERING

    private Format<T> format = null;

    /**
     * @param v
     * @return
     */
    public SList<T> render(Format<T> v) {
        this.format = v;
        return this;
    }

    /**
     * @param clazz
     * @return
     */
    public SList<T> renderFor(Class<T> clazz) {
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
    // SELECTION

    public enum SelectionMode{ //
        SINGLE(ListSelectionModel.SINGLE_SELECTION), //
        INTERVAL(ListSelectionModel.SINGLE_INTERVAL_SELECTION), //
        MULTIPLE(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        private int code;
        private SelectionMode(int code) {
            this.code = code;
        }

        static SList.SelectionMode of(int code) {
            for (SList.SelectionMode selectionMode : values()) {
                if (selectionMode.code == code) {
                    return selectionMode;
                }
            }
            throw new IllegalArgumentException("Code does not exist " + code);
        }
    }

    /**
     *
     * @param v
     */
    public void setSelectionMode(SList.SelectionMode v) {
        sListCore.setSelectionMode(v.code);
    }
    public SList.SelectionMode getSelectionMode() {
        return SList.SelectionMode.of(sListCore.getSelectionModel().getSelectionMode());
    }
    public SList<T> selectionMode(SList.SelectionMode v) {
        setSelectionMode(v);
        return this;
    }

    /**
     *
     * @return
     */
    public List<T> getSelection() {
        var selectedItems = new ArrayList<T>(sListCore.getSelectionModel().getSelectedItemsCount());
        for (int rowIdx : sListCore.getSelectionModel().getSelectedIndices()) {
            selectedItems.add(getData().get(rowIdx));
        }
        return Collections.unmodifiableList(selectedItems);
    }

    /**
     *
     */
    public void setSelection(List<T> values) {
        clearSelection();
        List<T> data = getData();
        for (T value : values) {
            int index = data.indexOf(value);
            sListCore.getSelectionModel().addSelectionInterval(index, index);
        }
    }

    /**
     *
     */
    public void clearSelection() {
        sListCore.clearSelection();
    }

    /**
     *
     * @param listener
     */
    synchronized public void addSelectionChangedListener(Consumer<List<T>> listener) {
        if (selectionChangedListeners == null) {
            selectionChangedListeners = new ArrayList<>();
        }
        selectionChangedListeners.add(listener);
    }
    synchronized public boolean removeSelectionChangedListener(Consumer<List<T>> listener) {
        if (selectionChangedListeners == null) {
            return false;
        }
        return selectionChangedListeners.remove(listener);
    }
    private List<Consumer<List<T>>> selectionChangedListeners;

    /**
     * @param onSelectionChangedListener
     * @return
     */
    public SList<T> onSelectionChanged(Consumer<List<T>> onSelectionChangedListener) {
        addSelectionChangedListener(onSelectionChangedListener);
        return this;
    }

    // ========================================================
    // EXCEPTION HANDLER

    /**
     * Set the ExceptionHandler used a.o. in binding
     * @param v
     */
    public void setExceptionHandler(ExceptionHandler v) {
        firePropertyChange(EXCEPTIONHANDLER, exceptionHandler, exceptionHandler = v);
    }
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
    private ExceptionHandler exceptionHandler = this::handleException;


    // ===========================================================================
    // FLUENT API

    @Override
    public void setName(String v) {
        super.setName(v);
        sListCore.setName(v + ".sListCore"); // For tests we need to address the actual list
    }

    static public <T> SList<T> of() {
        return new SList<T>();
    }

    static public <T> SList<T> of(List<T> data) {
    	return new SList<T>().data(data);
    }
}
