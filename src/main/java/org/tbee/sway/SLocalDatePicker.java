/*
 * Copyright: (c) KnowledgePlaza.nl
 * Version:   $Revision: 1.10 $
 * Modified:  $Date: 2010/10/08 08:39:34 $
 * By:        $Author: toeukpap $
 */
package org.tbee.sway;

import org.tbee.sway.support.ColorUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author user
 */
public class SLocalDatePicker extends JComponent {
    public static final DateTimeFormatter MMMM = DateTimeFormatter.ofPattern("MMMM");
    public static final DateTimeFormatter E = DateTimeFormatter.ofPattern("E");

    // ===========================================================================================================
    // CONSTRUCTOR

    public SLocalDatePicker() {
        this(LocalDate.now());
    }

    public SLocalDatePicker(LocalDate c) {
        value(c);

        // setup properties
        mMode(Mode.SINGLE);
        setLocale(Locale.getDefault());
        referenceLocalDate(LocalDate.now());
        value(LocalDate.now());

        // GUI
        constructGUI();
    }

    // ===========================================================================================================
    // PROPERTIES

    /**
     * LocalDate: the last selected
     */
    public LocalDate value() {
        return value;
    }

    public SLocalDatePicker value(LocalDate v) {
        try {
            // update calendar
            fireVetoableChange(VALUE, this.value, v);
            firePropertyChange(VALUE, this.value, this.value = v);

            // update calendars
            if (v == null && values.contains(this.value)) {
				removeLocalDate(this.value);
			}
            if (v != null && !values.contains(v)) {
                values(List.of(v));
            }

            // refresh
            if (v != null) {
                displayedLocalDate(v);
            }
            refreshLocalDate();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }
    volatile protected LocalDate value = null;
    final static public String VALUE = "value";

    /**
     * values: all selected calendars (depends on the selection mode)
     */
    public List<LocalDate> values() {
        return values;
    }

    public SLocalDatePicker values(List<LocalDate> v) {
        try {
            fireVetoableChange(VALUES, this.values, v);
            firePropertyChange(VALUES, this.values, this.values = v);

            // update calendar
            if (!values.contains(this.value) && values.size() > 0) value(values.get(0));

            // refresh
            refreshSelection();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }
    volatile protected List<LocalDate> values = new ArrayList<LocalDate>();
    final static public String VALUES = "values";

    public SLocalDatePicker addLocalDate(LocalDate v) {
        List<LocalDate> newValues = new ArrayList<LocalDate>(values);
        newValues.add(v);
        values(newValues);
        return this;
    }

    public SLocalDatePicker removeLocalDate(LocalDate v) {
        List<LocalDate> newValues = new ArrayList<LocalDate>(values);
        newValues.remove(v);
        values(newValues);
        return this;
    }

    /**
     * ReferenceLocalDate: determines how the component looks like e.g. first day of week
     */
    public LocalDate referenceLocalDate() {
        return referenceLocalDate;
    }

    public SLocalDatePicker referenceLocalDate(LocalDate v) {
        try {
            fireVetoableChange(REFERENCELOCALDATE, this.referenceLocalDate, v);
            firePropertyChange(REFERENCELOCALDATE, this.referenceLocalDate, this.referenceLocalDate = v);

            // refresh
            refreshLabels();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }
    volatile protected LocalDate referenceLocalDate = null;
    final static public String REFERENCELOCALDATE = "referenceLocalDate";

    /**
     * Locale: determines the language of the labels
     */
    public Locale locale() {
        return locale;
    }

    public SLocalDatePicker locale(Locale v) {
        try {
            fireVetoableChange(LOCALE, this.locale, v);
            firePropertyChange(LOCALE, this.locale, this.locale = v);

            // refresh
            refreshLabels();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }
    volatile protected Locale locale = Locale.getDefault();
    final static public String LOCALE = "locale";

    /**
     * Mode: single, range or multiple
     */
    public Mode mode() {
        return mode;
    }

    public void mMode(Mode v) {
        try {
            fireVetoableChange(MODE, this.mode, v);
            firePropertyChange(MODE, this.mode, this.mode = v);

            // update calendars
            List<LocalDate> values = new ArrayList<>();
            if (value() != null) values.add(value());
            values(values);
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public SLocalDatePicker withMode(Mode value) {
        mMode(value);
        return this;
    }

    volatile protected Mode mode = null;
    final static public String MODE = "mode";

    public enum Mode {SINGLE, RANGE, MULTIPLE}

    /**
     * determine if a date is selected
     */
    public boolean isSelected(LocalDate v) {
        if (v == null) {
            return false;
        }

        return values.contains(v);
    }

    /**
     * WeekendLabelColor: single, range or multiple
     */
    public Color weekendLabelColor() {
        return weekendLabelColor;
    }

    public SLocalDatePicker weekendLabelColor(Color v) {
        try {
            fireVetoableChange(WEEKENDLABELCOLOR, this.weekendLabelColor, v);
            firePropertyChange(WEEKENDLABELCOLOR, this.weekendLabelColor, this.weekendLabelColor = v);

            // update calendars
            List<LocalDate> lLocalDates = new ArrayList<LocalDate>();
            if (value() != null) lLocalDates.add(value());
            values(lLocalDates);
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }
    volatile protected Color weekendLabelColor = (new JTable()).getSelectionBackground();
    final static public String WEEKENDLABELCOLOR = "weekendLabelColor";


    // ===========================================================================================================
    // LAYOUT

    /**
     * ReferenceLocalDate: determines how the component looks like e.g. first day of week
     */
    public LocalDate displayedLocalDate() {
        return (LocalDate) displayedLocalDate;
    }

    public void displayedLocalDate(LocalDate v) {
        // the displayed localdate always points to the 1st of the month
        if (v == null) {
            throw new IllegalArgumentException("NULL not allowed");
        }
        v = v.withDayOfMonth(1);

        try {
			// set it
			fireVetoableChange(DISPLAYEDLOCALDATE, displayedLocalDate, v);
			firePropertyChange(DISPLAYEDLOCALDATE, displayedLocalDate, displayedLocalDate = v);
			refreshLocalDate();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }

    volatile protected LocalDate displayedLocalDate;
    final static public String DISPLAYEDLOCALDATE = "displayedLocalDate";

    /**
     *
     */
    protected void constructGUI() {
        setLayout(new CardLayout());
        add(constructGUIPicker(), "PICKER");
    }

    /**
     * @return
     */
    protected JPanel constructGUIYear() {
        // current year
        int year = displayedLocalDate.getYear();

        // grid
        int lGridWidth = 3;
        int lGridHeight = 6;
        year -= (lGridWidth * lGridHeight) / 2;
        yearPanel = new JPanel();
        yearPanel.setLayout(new GridLayout(lGridHeight, lGridWidth));
        for (int lCol = 0; lCol < lGridWidth; lCol++) {
            for (int lRow = 0; lRow < lGridHeight; lRow++) {
                // create button
                JButton lJButton = new JButton("" + year);
                lJButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // set year
                        displayedLocalDate = displayedLocalDate.withYear(Integer.parseInt(((JButton) e.getSource()).getText()));
                        SLocalDatePicker.this.remove(yearPanel);
                        yearPanel = null;
                    }
                });

                // layout
                yearPanel.add(lJButton);
                year++;
            }
        }

        // done
        return yearPanel;
    }
    private JPanel yearPanel = null;

    /**
     * @return
     */
    protected JPanel constructGUIMonth() {
        // current year
        LocalDate localDate = displayedLocalDate;

        // grid
        int lGridWidth = 2;
        int lGridHeight = 6;
        monthPanel = new JPanel();
        monthPanel.setLayout(new GridLayout(lGridHeight, lGridWidth));
        int idx = 0;
        for (int col = 0; col < lGridWidth; col++) {
            for (int row = 0; row < lGridHeight; row++) {
                // set month
                localDate = localDate.withMonth(idx);

                // create button
                JButton jButton = new JButton("" + MMMM.format(localDate));
                jButton.setActionCommand("" + idx);
                jButton.addActionListener(e -> {
                    displayedLocalDate = displayedLocalDate.withMonth(Integer.parseInt(((JButton) e.getSource()).getActionCommand()));
                    SLocalDatePicker.this.remove(monthPanel);
                    monthPanel = null;
                });

                // layout
                monthPanel.add(jButton);
                idx++;
            }
        }

        // done
        return monthPanel;
    }

    private JPanel monthPanel = null;

    /**
     * @return
     */
    protected JPanel constructGUIPicker() {
        // year spinner
        yearSpinnerModel = new YearSpinnerModel();
        yearJSpinner = new JSpinner(yearSpinnerModel);
        yearJSpinner.setEditor(new JSpinner.NumberEditor(yearJSpinner, "####"));
        ((JSpinner.DefaultEditor) yearJSpinner.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.LEFT);
        yearJSpinner.addChangeListener(e -> {
            // modify displayed calendar
            displayedLocalDate = displayedLocalDate.withYear(((Number) yearSpinnerModel.getValue()).intValue());
        });
        ((JSpinner.DefaultEditor) yearJSpinner.getEditor()).getTextField().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // double click
                if (e.getClickCount() < 2) return;

                // add and show panel
                add(constructGUIYear(), "YEAR");
                ((CardLayout) getLayout()).show(SLocalDatePicker.this, "YEAR");
            }
        });

        // month spinner
        monthSpinnerModel = new MonthSpinnerModel();
        monthJSpinner = new JSpinner(monthSpinnerModel);
        ((JSpinner.DefaultEditor) monthJSpinner.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.LEFT);
        monthJSpinner.addChangeListener(e -> displayedLocalDate = displayedLocalDate.withMonth(monthSpinnerModel.getList().indexOf(monthSpinnerModel.getValue()) + 1));
        yearJSpinner.setPreferredSize(monthJSpinner.getPreferredSize());
        ((JSpinner.DefaultEditor) monthJSpinner.getEditor()).getTextField().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // double click
                if (e.getClickCount() < 2) return;

                // add and show panel
                add(constructGUIMonth(), "MONTH");
                ((CardLayout) getLayout()).show(SLocalDatePicker.this, "MONTH");
            }
        });

        // daynames
        for (int i = 0; i < 7; i++) {
            daynameLabels[i] = new JLabel("d" + i);
            daynameLabels[i].setHorizontalAlignment(JLabel.CENTER);
        }

        // weeknumbers
        for (int i = 0; i < 6; i++) {
            weeknumberLabels[i] = new JLabel("w" + i);
            weeknumberLabels[i].setHorizontalAlignment(JLabel.CENTER);
        }

        // dates
        ActionListener iDayActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // extract the date that was clicked
                int dayIdx = Integer.parseInt(((JToggleButton) e.getSource()).getText());
                LocalDate clickedLocalDate = displayedLocalDate.withDayOfMonth(dayIdx);

                // current calendar
                LocalDate currentLocalDate = value();
                if (currentLocalDate == null) currentLocalDate = clickedLocalDate;

                // the new collection
                List<LocalDate> localDates = new ArrayList<LocalDate>(values);

                // what modifiers were pressed?
                boolean shiftPressed = (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0;
                boolean ctrlPressed = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0;
                LocalDate fromLocalDate = currentLocalDate.isBefore(clickedLocalDate) ? currentLocalDate : clickedLocalDate;
                LocalDate toLocalDate = currentLocalDate.isBefore(clickedLocalDate) ? clickedLocalDate : currentLocalDate;

                // Single
                if (mode() == Mode.SINGLE) {
                    // if already present
                    if (localDates.contains(clickedLocalDate)) {
                        // remove
                        localDates.remove(clickedLocalDate);
                    }
                    else {
                        // set one value
                        localDates.clear();
                        localDates.add(clickedLocalDate);
                    }
                }

                // range or multiple without extend active
                if ((mode() == Mode.RANGE) || (mode() == Mode.MULTIPLE && !ctrlPressed)) {
                    if (!shiftPressed) {
                        // if already present and only a range of one
                        if (localDates.size() == 1 && localDates.contains(clickedLocalDate)) { // found
                            // remove
                            localDates.clear();
                        }
                        else {
                            // set one value
                            localDates.clear();
                            localDates.add(clickedLocalDate);
                        }
                    }
                    else {
                        // add all dates to a new range
                        LocalDate localDate = fromLocalDate;
                        while (!localDate.isAfter(toLocalDate)) {
                            localDates.add(localDate);
                            localDate = localDate.plusDays(1);
                        }
                    }
                }

                // multiple with extend active
                if (mode() == Mode.MULTIPLE && ctrlPressed) {
                    if (!shiftPressed) {
                        // if already present and only a range of one
                        if (localDates.size() == 1 && localDates.contains(clickedLocalDate)) { // found
                            // remove
                            localDates.clear();
                        }
                        // if already present
                        else if (localDates.contains(clickedLocalDate)) { // found
                            // remove
                            localDates.remove(clickedLocalDate);
                        }
                        else {
                            // add one value
                            localDates.add(clickedLocalDate);
                        }
                    }
                    else if (shiftPressed) {
                        // add all dates to the range
                        LocalDate localDate = fromLocalDate;
                        while (!localDate.isAfter(toLocalDate)) {
                            if (!localDates.contains(localDate)) {
                                localDates.add(localDate);
                            }
                            localDate = localDate.plusDays(1);
                        }
                    }
                }

                // set
                values(localDates);
                value(clickedLocalDate);

                // refresh
                refreshSelection();
            }
        };
        Insets lEmptyInsets = new Insets(0, 0, 0, 0);
        for (int i = 0; i < 6 * 7; i++) {
            dateToggleButton[i] = new JToggleButton("?");
            dateToggleButton[i].setMargin(lEmptyInsets);
            dateToggleButton[i].addActionListener(iDayActionListener);
        }

        // goto today button
        final JLabel todayJButton = new JLabel(" ");
        todayJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                value(LocalDate.now());
                todayJButton.setBorder(null);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // tooltip
                LocalDate now = LocalDate.now();
                if (!(displayedLocalDate.getMonth() == now.getMonth())) {
                    todayJButton.setToolTipText(MMMM.format(now) + " " + now.getYear() + "...");

                    // border
                    todayJButton.setBorder(BorderFactory.createLineBorder(todayJButton.getForeground()));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                todayJButton.setBorder(null);
            }
        });

        // construct GUI; we use always available layout managers. Miglayout would have been better.
        pickerPanel.setLayout(new BorderLayout());

        // layout header
        JPanel lHeaderJPanel = new JPanel();
        lHeaderJPanel.setLayout(new GridLayout(1, 2, 2, 2));
        lHeaderJPanel.add(yearJSpinner);
        lHeaderJPanel.add(monthJSpinner);
        pickerPanel.add(lHeaderJPanel, BorderLayout.NORTH);

        // layout center
        JPanel contentJPanel = new JPanel();
        contentJPanel.setLayout(new GridLayout(7, 8, 0, 0));
        contentJPanel.add(todayJButton);
        for (int c = 0; c < 7; c++) {
            contentJPanel.add(daynameLabels[c]);
        }
        for (int r = 0; r < 6; r++) {
            contentJPanel.add(weeknumberLabels[r]);
            for (int c = 0; c < 7; c++) {
                contentJPanel.add(dateToggleButton[(r * 7) + c]);
            }
        }
        pickerPanel.add(contentJPanel, BorderLayout.CENTER);

        // refresh
        refreshLabels();

        // done
        return pickerPanel;
    }

    protected JSpinner yearJSpinner = null;
    protected YearSpinnerModel yearSpinnerModel = null;
    protected JSpinner monthJSpinner = null;
    protected SpinnerListModel monthSpinnerModel = null;
    protected JLabel[] daynameLabels = new JLabel[7]; // seven days in a week
    protected JLabel[] weeknumberLabels = new JLabel[6]; // we required a maximum of 6 weeks if the 1st of the month of a 31 days month falls on the last weekday
    protected JToggleButton[] dateToggleButton = new JToggleButton[6 * 7]; // we required a maximum of 6 weeks if the 1st of the month of a 31 days month falls on the last weekday
    private JLabel labelForColor = new JLabel(); // use to get the default colors
    private JToggleButton toggleButtonForColor = new JToggleButton(); // use to get the default colors
    private JPanel pickerPanel = new JPanel();

    /*
     *
     * @author user
     *
     */
    class YearSpinnerModel extends SpinnerNumberModel {
        public YearSpinnerModel() {
            super(displayedLocalDate.getYear(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        }
        public void increment() {
            setValue(getNextValue());
        }
        public void decrement() {
            setValue(getPreviousValue());
        }
    }

    ;

    /*
     *
     * @author user
     *
     */
    class MonthSpinnerModel extends SpinnerListModel {
        public MonthSpinnerModel() {
            super(getMonthNames());
        }

        @Override
        public Object getNextValue() {
            if (getList().indexOf(getValue()) == 11) {
                yearSpinnerModel.increment();
                return getList().get(0);
            }
            return super.getNextValue();
        }

        @Override
        public Object getPreviousValue() {
            if (getList().indexOf(getValue()) == 0) {
                yearSpinnerModel.decrement();
                return getList().get(11);
            }
            return super.getPreviousValue();
        }
    }

    ;

    /**
     *
     */
    protected void refreshLabels() {
        // we not setup yet
        if (yearJSpinner == null) return;
        Color normalDayColor = labelForColor.getForeground();
        Color weekendDayColor = weekendLabelColor();

        // mark as modifying
        try {
            // setup the dayLabels
            // DayOfWeek.SUNDAY = 1 and DayOfWeek.SATURDAY = 7
            WeekFields weekFields = WeekFields.of(locale);
            int firstDayOfWeek = weekFields.getFirstDayOfWeek().getValue();
            LocalDate localDate = LocalDate.of(2009, 7, 4 + firstDayOfWeek); // july 5th 2009 is a Sunday
            for (int i = 0; i < 7; i++) {
                // next
                localDate = localDate.withDayOfMonth(4 + i + firstDayOfWeek);

                // assign day
                daynameLabels[i].setText(E.format(localDate));//.substring(0,1) );

                // highlight weekend
                DayOfWeek dayOfWeek = localDate.getDayOfWeek();
                daynameLabels[i].setForeground(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY ? weekendDayColor : normalDayColor);
            }

            // set month labels
            monthSpinnerModel.setList(getMonthNames());

            // refresh the rest
            refreshLocalDate();
        }
        finally {
            //TBEERNOT iModifying.decrementAndGet();
        }
    }

    /**
     *
     */
    protected void refreshLocalDate() {
        // we not setup yet
        if (yearJSpinner == null) {
            return;
        }

        // mark as modifying
        try {
            // year
            int year = displayedLocalDate.getYear();
            yearSpinnerModel.setValue(year);

            // month
            int monthIdx = displayedLocalDate.getMonthValue();
            monthSpinnerModel.setValue(monthSpinnerModel.getList().get(monthIdx - 1));

            // setup the weekLabels
            List<Integer> weekLabels = getWeekLabels();
            for (int i = 0; i <= 5; i++) {
                // set label
                weeknumberLabels[i].setText(weekLabels.get(i).toString());

                // first hide
                // in GridBagLayout this becomes uneasy behavior: iWeeknumberJLabels[i].setVisible(false);
            }

            // setup the buttons [0..(6*7)-1]
            // determine with which button to start
            int firstOfMonthIdx = determineFirstOfMonthDayOfWeek();

            // hide the preceeding buttons
            for (int i = 0; i < firstOfMonthIdx; i++) {
                dateToggleButton[i].setVisible(false);
            }

            // set the month buttons
            int daysInMonth = determineDaysInMonth();
            LocalDate localDate = displayedLocalDate;
            for (int i = 1; i <= daysInMonth; i++) {
                // set the date
                localDate = localDate.withDayOfMonth(i);

                // determine the index in the buttons
                int idx = firstOfMonthIdx + i - 1;

                // update the button
                dateToggleButton[idx].setVisible(true);
                dateToggleButton[idx].setText("" + i);

                // make the corresponding weeklabel visible
                weeknumberLabels[idx / 7].setVisible(true);

                // highlight today
                dateToggleButton[idx].setForeground(isToday(localDate) ? ColorUtil.brighterOrDarker(toggleButtonForColor.getForeground(), 0.3) : toggleButtonForColor.getForeground());
            }

            // hide the trailing buttons
            for (int i = firstOfMonthIdx + daysInMonth; i < 6 * 7; i++) {
                dateToggleButton[i].setVisible(false);
            }

            // also update the selection
            refreshSelection();
        }
        finally {
            // iModifying.decrementAndGet();
        }
    }

    public void refreshSelection() {
        // we not setup yet
        if (yearJSpinner == null) return;

        // mark as modifying
        try {
            // setup the buttons [0..(6*7)-1]
            // determine with which button to start
            int firstOfMonthIdx = determineFirstOfMonthDayOfWeek();

            // set the month buttons
            int daysInMonth = determineDaysInMonth();
            LocalDate localDate = displayedLocalDate;
            for (int i = 1; i <= daysInMonth; i++) {
                // set the date
                localDate = localDate.withDayOfMonth(i);

                // determine the index in the buttons
                int lIdx = firstOfMonthIdx + i - 1;

                // is this date selected
                dateToggleButton[lIdx].setSelected(isSelected(localDate));
            }
        }
        finally {
            //iModifying.decrementAndGet();
        }
    }


    // =============================================================================
    // SUPPORT

    /**
     * @return
     */
    protected List<String> getMonthNames() {
        List<String> monthNames = new ArrayList<String>();
        for (int i = 0; i < 12; i++) {
            LocalDate localDate = LocalDate.of(2009, i + 1, 1);
            monthNames.add(MMMM.format(localDate));
        }
        return monthNames;
    }

    /**
     * Get a list with the weeklabels
     */
    protected List<Integer> getWeekLabels() {
        // result
        List<Integer> weekLabels = new ArrayList<Integer>();

        // setup the weekLabels
        LocalDate localDate = displayedLocalDate;
        WeekFields weekFields = WeekFields.of(locale);
        for (int i = 0; i <= 5; i++) {
            // set label
            weekLabels.add(localDate.get(weekFields.weekOfWeekBasedYear()));

            // next week
            localDate = localDate.plusDays(7);
        }

        // done
        return weekLabels;
    }

    /**
     * check if a certain weekday name is a certain day-of-the-week
     */
    protected boolean isWeekday(int idx, int weekdaynr) {
        // DayOfWeek.SUNDAY = 1 and DayOfWeek.SATURDAY = 7
        WeekFields weekFields = WeekFields.of(locale);
        int firstDayOfWeek = weekFields.getFirstDayOfWeek().getValue();
        LocalDate localDate = LocalDate.of(2009, 7, 4 + firstDayOfWeek); // july 5th 2009 is a Sunday
        localDate = localDate.plusDays(idx);
        int dayOfWeek = localDate.getDayOfWeek().getValue();

        // check
        return (dayOfWeek == weekdaynr);
    }

    /**
     * check if a certain weekday name is a certain day-of-the-week
     */
    protected boolean isWeekdayWeekend(int idx) {
        return (isWeekday(idx, DayOfWeek.SATURDAY.getValue()) || isWeekday(idx, DayOfWeek.SUNDAY.getValue()));
    }

    /**
     * determine on which day of week idx the first of the months is
     */
    protected int determineFirstOfMonthDayOfWeek() {
        // determine with which button to start
        WeekFields weekFields = WeekFields.of(locale);
        int firstDayOfWeek = weekFields.getFirstDayOfWeek().getValue();
        int firstOfMonthIdx = displayedLocalDate.get(weekFields.weekOfWeekBasedYear()) - firstDayOfWeek;
        while (firstOfMonthIdx < 0) {
            firstOfMonthIdx += 7;
        }
        return firstOfMonthIdx;
    }

    /**
     * determine the number of days in the month
     */
    protected int determineDaysInMonth() {
        return displayedLocalDate.plusMonths(1).minusDays(1).getDayOfMonth();
    }

    /**
     * determine if a date is today
     */
    protected boolean isToday(LocalDate v) {
        return LocalDate.now().equals(v);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SLookAndFeel.installDefault();
            SFrame.of(SHPanel.of(new SLocalDatePicker(), new SLocalDatePicker().locale(Locale.ENGLISH)))
                    .exitOnClose()
                    .sizeToPreferred()
                    .visible(true);
        });
    }
}
