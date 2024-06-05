package org.tbee.sway;

import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ExceptionHandlerDefaultMixin;
import org.tbee.sway.mixin.SelectionMixin;
import org.tbee.sway.mixin.ValueMixin;
import org.tbee.sway.support.ColorUtil;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.beans.PropertyVetoException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * @author user
 */
public class SLocalDatePicker extends JPanel implements
        ValueMixin<SLocalDatePicker, LocalDate>,
        SelectionMixin<SLocalDatePicker, LocalDate>,
        ExceptionHandlerDefaultMixin<SLocalDatePicker> {

    public static final DateTimeFormatter MMMM = DateTimeFormatter.ofPattern("MMMM");
    public static final DateTimeFormatter E = DateTimeFormatter.ofPattern("E");
    public static final LocalDate MONDAY = java.time.LocalDate.of(2009, 7, 6); // This is a monday

    private final SSpinner<Integer> yearSpinner = SSpinner.ofInteger().columns(5).editable(true);;
    private final List<String> monthNames = new ArrayList<String>();
    private final SSpinner<String> monthSpinner;
    private final JLabel[] daynameLabels = new JLabel[7]; // seven days in a week
    private final JLabel[] weeknumberLabels = new JLabel[6]; // we required a maximum of 6 weeks if the 1st of the month of a 31 days month falls on the last weekday
    private final JToggleButton[] dateToggleButton = new JToggleButton[6 * 7]; // we required a maximum of 6 weeks if the 1st of the month of a 31 days month falls on the last weekday
    private final JLabel labelForColor = new JLabel(); // use to get the default colors
    private final JToggleButton toggleButtonForColor = new JToggleButton(); // use to get the default colors

    // ===========================================================================================================
    // CONSTRUCTOR

    public SLocalDatePicker() {
        this(null);
    }

    public SLocalDatePicker(LocalDate localDate) {

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

        // year spinner
        yearSpinner.value(displayedLocalDate.getYear());
        yearSpinner.value$().onChange((Consumer<Integer>) v -> {
            displayedLocalDate = displayedLocalDate.withYear(v);
            refreshDisplayedDateBasedComponents();
        });

        // month spinner
        populateMonthNames();
        monthSpinner = SSpinner.of(monthNames).value(populateMonthNames().get(displayedLocalDate.getMonthValue() - 1));
        monthSpinner.value$().onChange((Consumer<String>) v -> {
            displayedLocalDate = displayedLocalDate.withMonth(populateMonthNames().indexOf(v) + 1);
            refreshDisplayedDateBasedComponents();
        });

        // dates
        ActionListener dayActionListener = e -> dayClicked(e);
        Insets lEmptyInsets = new Insets(0, 0, 0, 0);
        for (int i = 0; i < 6 * 7; i++) {
            dateToggleButton[i] = new JToggleButton("" + i);
            dateToggleButton[i].setMargin(lEmptyInsets);
            dateToggleButton[i].addActionListener(dayActionListener);
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

        // setup defaults
        mode(Mode.SINGLE);
        locale(Locale.getDefault());
        displayedLocalDate(localDate != null ? localDate : LocalDate.now());
        refreshLabels();
        value(localDate);

        // construct GUI; we use always available layout managers. Miglayout would have been better.
        setLayout(new BorderLayout());

        // layout header
        JPanel headerJPanel = new JPanel();
        headerJPanel.setLayout(new GridLayout(1, 2, 2, 2));
        headerJPanel.add(yearSpinner);
        headerJPanel.add(monthSpinner);
        add(headerJPanel, BorderLayout.NORTH);

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
        add(contentJPanel, BorderLayout.CENTER);
    }

    private void dayClicked(ActionEvent e) {
        // extract the date that was clicked
        int dayIdx = Integer.parseInt(((JToggleButton) e.getSource()).getText());
        LocalDate clickedLocalDate = displayedLocalDate.withDayOfMonth(dayIdx);

        // current calendar
        LocalDate currentLocalDate = value;
        if (currentLocalDate == null) {
            currentLocalDate = clickedLocalDate;
        }

        // the new collection
        List<LocalDate> localDates = new ArrayList<LocalDate>(selection);

        // what modifiers were pressed?
        boolean shiftPressed = (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0;
        boolean ctrlPressed = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0;
        LocalDate fromLocalDate = currentLocalDate.isBefore(clickedLocalDate) ? currentLocalDate : clickedLocalDate;
        LocalDate toLocalDate = currentLocalDate.isBefore(clickedLocalDate) ? clickedLocalDate : currentLocalDate;

        // Single
        if (mode == Mode.SINGLE) {
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
        if ((mode == Mode.RANGE) || (mode == Mode.MULTIPLE && !ctrlPressed)) {
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
        if (mode == Mode.MULTIPLE && ctrlPressed) {
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
        setSelection(localDates);
        value(clickedLocalDate);

        // refresh
        refreshSelection();
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

    // ===========================================================================================================
    // PROPERTIES

    /**
     * LocalDate: the last selected
     */
    public LocalDate getValue() {
        return value;
    }
    public void setValue(LocalDate v) {
        try {
            // update calendar
            fireVetoableChange(VALUE, this.value, v);
            firePropertyChange(VALUE, this.value, this.value = v);

            // sync selection
            if (v == null && this.value != null && selection.contains(this.value)) {
				removeFromSelection(this.value);
			}
            if (v != null && !selection.contains(v)) {
                setSelection(List.of(v));
            }

            // refresh
            if (v != null) {
                setDisplayedLocalDate(v);
            }
            refreshDisplayedDateBasedComponents();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private LocalDate value = null;

    /**
     * values: all selected calendars (depends on the selection mode)
     */
    public List<LocalDate> getSelection() {
        return selection;
    }

    public void setSelection(List<LocalDate> v) {
        try {
            fireVetoableChange(SELECTION, this.selection, v);
            firePropertyChange(SELECTION, this.selection, this.selection = v);

            // update calendar
            if (!selection.contains(this.value) && selection.size() > 0) {
                setValue(selection.get(0));
            }

            // refresh
            refreshSelection();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private List<LocalDate> selection = List.of();

    public SLocalDatePicker addToSelection(LocalDate v) {
        List<LocalDate> newValues = new ArrayList<LocalDate>(selection);
        newValues.add(v);
        setSelection(newValues);
        return this;
    }

    public SLocalDatePicker removeFromSelection(LocalDate v) {
        List<LocalDate> newValues = new ArrayList<LocalDate>(selection);
        newValues.remove(v);
        setSelection(newValues);
        return this;
    }


    /**
     * Locale: determines the language of the labels
     */
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale v) {
        try {
            fireVetoableChange(LOCALE, this.locale, v);
            firePropertyChange(LOCALE, this.locale, this.locale = v);

            refreshLabels();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private Locale locale = Locale.getDefault();
    final static public String LOCALE = "locale";
    public SLocalDatePicker locale(Locale v) {
        setLocale(v);
        return this;
    }

    /**
     * Mode: single, range or multiple
     */
    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode v) {
        try {
            fireVetoableChange(MODE, this.mode, v);
            firePropertyChange(MODE, this.mode, this.mode = v);

            // update calendars
            List<LocalDate> values = new ArrayList<>();
            if (value != null) {
                values.add(value);
            }
            setSelection(values);
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    public SLocalDatePicker mode(Mode v) {
        setMode(v);
        return this;
    }
    private Mode mode = null;
    final static public String MODE = "mode";

    public enum Mode {SINGLE, RANGE, MULTIPLE}

    /**
     * determine if a date is selected
     */
    public boolean isSelected(LocalDate v) {
        if (v == null) {
            return false;
        }
        return selection.contains(v);
    }

    /**
     * WeekendLabelColor: single, range or multiple
     */
    public Color getWeekendLabelColor() {
        return weekendLabelColor;
    }
    public void setWeekendLabelColor(Color v) {
        try {
            fireVetoableChange(WEEKENDLABELCOLOR, this.weekendLabelColor, v);
            firePropertyChange(WEEKENDLABELCOLOR, this.weekendLabelColor, this.weekendLabelColor = v);

            // update calendars
            List<LocalDate> localDates = new ArrayList<LocalDate>();
            if (value != null) {
                localDates.add(value);
            }
            setSelection(localDates);
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private Color weekendLabelColor = (new JTable()).getSelectionBackground();
    final static public String WEEKENDLABELCOLOR = "weekendLabelColor";
    public SLocalDatePicker weekendLabelColor(Color v) {
        setWeekendLabelColor(v);
        return this;
    }


    // ===========================================================================================================
    // LAYOUT

    /**
     * displayedLocalDate: determines how the component looks like e.g. first day of week
     */
    public LocalDate getDisplayedLocalDate() {
        return displayedLocalDate;
    }

    public void setDisplayedLocalDate(LocalDate v) {
        // the displayed localdate always points to the 1st of the month
        if (v == null) {
            throw new IllegalArgumentException("NULL not allowed");
        }
        v = v.withDayOfMonth(1);

        try {
			// set it
			fireVetoableChange(DISPLAYEDLOCALDATE, displayedLocalDate, v);
			firePropertyChange(DISPLAYEDLOCALDATE, displayedLocalDate, displayedLocalDate = v);
			refreshDisplayedDateBasedComponents();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private LocalDate displayedLocalDate = LocalDate.now();
    final static public String DISPLAYEDLOCALDATE = "displayedLocalDate";
    public SLocalDatePicker displayedLocalDate(LocalDate v) {
        setDisplayedLocalDate(v);
        return this;
    }

    protected void refreshLabels() {
        // we're not setup yet
        if (yearSpinner == null) {
            return;
        }

        // setup the dayLabels monday to sunday
        DateTimeFormatter dateTimeFormatter = E.withLocale(locale);
        Color normalDayColor = labelForColor.getForeground();
        for (int i = 0; i < 7; i++) {
            LocalDate localDate = MONDAY.plusDays(i);

            // assign day
            daynameLabels[i].setText(dateTimeFormatter.format(localDate));

            // highlight weekend
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            daynameLabels[i].setForeground(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY ? weekendLabelColor : normalDayColor);
        }

        // set month labels
        populateMonthNames();
        monthSpinner.refresh();

        // refresh the rest
        refreshDisplayedDateBasedComponents();
    }

    protected void refreshDisplayedDateBasedComponents() {
        // we not setup yet
        if (yearSpinner == null) {
            return;
        }

        // year
        int year = displayedLocalDate.getYear();
        yearSpinner.setValue(year);

        // month
        int monthIdx = displayedLocalDate.getMonthValue();
        monthSpinner.setValue(populateMonthNames().get(monthIdx - 1));

        // setup the weekLabels
        List<Integer> weekLabels = getWeekLabels();
        for (int i = 0; i <= 5; i++) {
            weeknumberLabels[i].setText(weekLabels.get(i).toString()); // TODO: move this into getWeekLabels or vice versa?
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
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate localDate = displayedLocalDate.withDayOfMonth(i);

            // determine the index in the button
            int idx = firstOfMonthIdx + (i - 1) - 1;

            // update the button
            dateToggleButton[idx].setVisible(true);
            dateToggleButton[idx].setText("" + i);

            // highlight today
            dateToggleButton[idx].setForeground(isToday(localDate) ? ColorUtil.brighterOrDarker(toggleButtonForColor.getForeground(), 0.3) : toggleButtonForColor.getForeground());
        }

        // hide the trailing buttons
        for (int i = firstOfMonthIdx + daysInMonth - 1; i < 6 * 7; i++) {
            dateToggleButton[i].setVisible(false);
        }

        // also update the selection
        refreshSelection();
    }

    public void refreshSelection() {
        // we not setup yet
        if (yearSpinner == null) {
            return;
        }

        // setup the buttons [0..(6*7)-1]
        // determine with which button to start
        int firstOfMonthIdx = determineFirstOfMonthDayOfWeek();

        // set the month buttons
        int daysInMonth = determineDaysInMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate localDate = displayedLocalDate.withDayOfMonth(i);

            // determine the index in the buttons
            int idx = firstOfMonthIdx + (i - 1) - 1;

            // is this date selected
            dateToggleButton[idx].setSelected(isSelected(localDate));
        }
    }


    // =============================================================================
    // SUPPORT

    protected List<String> populateMonthNames() {
        DateTimeFormatter dateTimeFormatter = MMMM.withLocale(locale);
        monthNames.clear();
        for (int i = 0; i < 12; i++) {
            LocalDate localDate = LocalDate.of(2009, i + 1, 1);
            monthNames.add(dateTimeFormatter.format(localDate));
        }
        return monthNames;
    }

    /**
     * Get a list with the weeklabels
     */
    protected List<Integer> getWeekLabels() {
        List<Integer> weekLabels = new ArrayList<Integer>();

        // determine the week-of-year
        LocalDate localDate = displayedLocalDate;
        WeekFields weekFields = WeekFields.of(locale);
        for (int i = 0; i <= 5; i++) {
            weekLabels.add(localDate.get(weekFields.weekOfWeekBasedYear()));

            // next week
            localDate = localDate.plusDays(7);
        }
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
        int firstOfMonthDayOfWeekIdx = displayedLocalDate.withDayOfMonth(1).getDayOfWeek().getValue();
        return firstOfMonthDayOfWeekIdx;
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


    static public SLocalDatePicker of() {
        return new SLocalDatePicker();
    }
}
