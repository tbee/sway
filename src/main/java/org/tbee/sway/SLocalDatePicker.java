package org.tbee.sway;

import net.miginfocom.layout.AlignX;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatToString;
import org.tbee.sway.mixin.ExceptionHandlerDefaultMixin;
import org.tbee.sway.mixin.SelectionMixin;
import org.tbee.sway.mixin.ValueMixin;
import org.tbee.sway.support.ColorUtil;
import org.tbee.sway.support.HAlign;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
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

import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_NEXTMONTH;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_NEXTYEAR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_PREVMONTH;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_PREVYEAR;

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

    private final STextField<Integer> yearTextField = STextField.ofInteger()
            .columns(8)
            .transparentAsLabel()
            .hAlign(HAlign.LEADING);
    private final Format<LocalDate> monthFormat = new FormatToString<>() {
        @Override
        public String toString(LocalDate value) {
            return MMMM.withLocale(locale).format(value);
        }
    };
    private final STextField<LocalDate> monthTextField = STextField.of(monthFormat)
            .columns(yearTextField.getColumns())
            .transparentAsLabel()
            .focusable(false)
            .editable(false)
            .hAlign(HAlign.TRAILING);
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

        // year
        yearTextField.value(displayedLocalDate.getYear());
        yearTextField.value$().onChange((Consumer<Integer>) v -> {
            displayedLocalDate = displayedLocalDate.withYear(v);
            refreshDisplayedDateBasedComponents();
        });
        setFontForHeader(yearTextField);

        // month
        monthTextField.value(displayedLocalDate);
        setFontForHeader(monthTextField);

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
        SMigPanel headerJPanel = SMigPanel.of().fillX().noMargins();
        // TODO: this panel causes the click to be wide
        headerJPanel.add(iconButton(DATEPICKER_PREVYEAR, this::prevYear));
        headerJPanel.add(iconButton(DATEPICKER_PREVMONTH, this::prevMonth));
        headerJPanel.addField(monthTextField).sizeGroup("monthyear");
        headerJPanel.addField(yearTextField).sizeGroup("monthyear").alignX(AlignX.LEADING);
        headerJPanel.add(iconButton(DATEPICKER_NEXTMONTH, this::nextMonth));
        headerJPanel.add(iconButton(DATEPICKER_NEXTYEAR, this::nextYear));
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

    private SButton iconButton(SIconRegistry.SwayInternallyUsedIcon icon, Runnable runnable) {
        return SButton.of(SIconRegistry.find(icon))
                .asImageButton()
                .onAction(e -> runnable.run());
    }

    private void prevYear() {
        displayedLocalDate = displayedLocalDate.withYear(displayedLocalDate.getYear() - 1);
        refreshDisplayedDateBasedComponents();
    }

    private void prevMonth() {
        int month = displayedLocalDate.getMonthValue();
        if (month == 1) {
            displayedLocalDate = displayedLocalDate.withMonth(12).withYear(displayedLocalDate.getYear() - 1);
        } else {
            displayedLocalDate = displayedLocalDate.withMonth(month - 1);
        }
        refreshDisplayedDateBasedComponents();
    }

    private void nextMonth() {
        int month = displayedLocalDate.getMonthValue();
        if (month == 12) {
            displayedLocalDate = displayedLocalDate.withMonth(1).withYear(displayedLocalDate.getYear() + 1);
        } else {
            displayedLocalDate = displayedLocalDate.withMonth(month + 1);
        }
        refreshDisplayedDateBasedComponents();
    }

    private void nextYear() {
        displayedLocalDate = displayedLocalDate.withYear(displayedLocalDate.getYear() + 1);
        refreshDisplayedDateBasedComponents();
    }

    private void setFontForHeader(STextField<?> sTextField) {
        sTextField.font(sTextField.getFont().deriveFont(sTextField.getFont().getSize() * 1.5f));
    }

    private void dayClicked(ActionEvent e) {
        // extract the date that was clicked
        String dateStr = ((JToggleButton) e.getSource()).getActionCommand();
        LocalDate clickedLocalDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

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

        // refresh the rest
        refreshDisplayedDateBasedComponents();
    }

    protected void refreshDisplayedDateBasedComponents() {

        // year
        int year = displayedLocalDate.getYear();
        yearTextField.setValue(year);

        // month
        int displayedMonth = displayedLocalDate.getMonthValue();
        monthTextField.setValue(displayedLocalDate);

        // setup the weekLabels
        List<Integer> weekLabels = getWeekLabels();
        for (int i = 0; i <= 5; i++) {
            weeknumberLabels[i].setText(weekLabels.get(i).toString()); // TODO: move this into getWeekLabels or vice versa?
        }

        // setup the buttons [0..(6*7)-1]
        // determine with which button to start
        int firstOfMonthIdx = determineFirstOfMonthDayOfWeek();

        // hide the preceeding buttons
        Color borderColor = ColorUtil.brighterOrDarker(toggleButtonForColor.getForeground(), 0.5);
        Border todayBorder = BorderFactory.createLineBorder(borderColor, 2);
        Border notTodayBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        Color altForgroundColor = ColorUtil.brighterOrDarker(toggleButtonForColor.getForeground(), 0.2);
        Color altBackgroundColor = ColorUtil.brighterOrDarker(toggleButtonForColor.getBackground(), 0.2);
        LocalDate startDate = displayedLocalDate.minusDays(firstOfMonthIdx - 1);
        for (int idx = 0; idx < 6 * 7; idx++) {
            LocalDate localDate = startDate.plusDays(idx);
            dateToggleButton[idx].setText("" + localDate.getDayOfMonth());
            dateToggleButton[idx].setActionCommand(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

            // highlight today
            dateToggleButton[idx].setBorder(isToday(localDate) ? todayBorder : notTodayBorder);
            boolean otherMonth = (localDate.getMonthValue() != displayedMonth);
            dateToggleButton[idx].setForeground(otherMonth ? altForgroundColor : toggleButtonForColor.getForeground());
            dateToggleButton[idx].setBackground(otherMonth ? altBackgroundColor : toggleButtonForColor.getBackground());
        }

        // also update the selection
        refreshSelection();
    }

    public void refreshSelection() {

        // setup the buttons [0..(6*7)-1]
        // determine with which button to start
        int firstOfMonthIdx = determineFirstOfMonthDayOfWeek();

        // set the month buttons
        LocalDate startDate = displayedLocalDate.minusDays(firstOfMonthIdx - 1);
        for (int idx = 0; idx < 6 * 7; idx++) {
            LocalDate localDate = startDate.plusDays(idx);
            dateToggleButton[idx].setSelected(isSelected(localDate));
        }
    }


    // =============================================================================
    // SUPPORT

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
