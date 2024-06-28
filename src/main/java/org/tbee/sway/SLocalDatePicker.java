package org.tbee.sway;

import net.miginfocom.layout.AlignX;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatToString;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.SelectionMixin;
import org.tbee.sway.mixin.ValueMixin;
import org.tbee.sway.support.ColorUtil;
import org.tbee.sway.support.HAlign;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import static org.tbee.sway.SLocalDatePicker.Mode.MULTIPLE;
import static org.tbee.sway.SLocalDatePicker.Mode.RANGE;
import static org.tbee.sway.SLocalDatePicker.Mode.SINGLE;

public class SLocalDatePicker extends JPanel implements
        ValueMixin<SLocalDatePicker, LocalDate>,
        SelectionMixin<SLocalDatePicker, LocalDate>,
        ExceptionHandlerMixin<SLocalDatePicker>,
        JComponentMixin<SLocalDatePicker> {

    public static final DateTimeFormatter MMM = DateTimeFormatter.ofPattern("MMM");
    public static final DateTimeFormatter E = DateTimeFormatter.ofPattern("E");
    public static final LocalDate MONDAY = java.time.LocalDate.of(2009, 7, 6); // This is a monday

    private final STextField<Integer> yearTextField = STextField.ofInteger()
            .columns(3)
            .transparentAsLabel()
            .hAlign(HAlign.LEADING);
    private final Format<LocalDate> monthFormat = new FormatToString<>() {
        @Override
        public String toString(LocalDate value) {
            return MMM.withLocale(locale).format(value);
        }
    };
    private final STextField<LocalDate> monthTextField = STextField.of(monthFormat)
            .columns(4)
            .transparentAsLabel()
            .focusable(false)
            .editable(false)
            .hAlign(HAlign.TRAILING);
    private final SLabel[] daynameLabels = new SLabel[7]; // seven days in a week
    private final SLabel[] weeknumberLabels = new SLabel[6]; // we required a maximum of 6 weeks if the 1st of the month of a 31 days month falls on the last weekday
    private final SToggleButton[] dateToggleButton = new SToggleButton[6 * 7]; // we required a maximum of 6 weeks if the 1st of the month of a 31 days month falls on the last weekday

    // ===========================================================================================================
    // CONSTRUCTOR

    public SLocalDatePicker() {
        this(null);
    }

    public SLocalDatePicker(LocalDate localDate) {

        // daynames
        for (int i = 0; i < 7; i++) {
            daynameLabels[i] = SLabel.of("d" + i).margin(0, 0, 0, 1)
                    .hAlign(HAlign.CENTER);
            weekdayFont(daynameLabels[i]);
        }

        // weeknumbers
        for (int i = 0; i < 6; i++) {
            weeknumberLabels[i] = SLabel.of("w" + i)
                    .hAlign(HAlign.CENTER);
            weeknumberFont(weeknumberLabels[i]);
        }

        // year
        yearTextField.value(displayedLocalDate.getYear());
        yearTextField.value$().onChange((Consumer<Integer>) v -> {
            displayedLocalDate = displayedLocalDate.withYear(v);
            refreshDisplayedDateBasedComponents();
        });
        headerFont(yearTextField);

        // month
        monthTextField.value(displayedLocalDate);
        headerFont(monthTextField);

        // dates
        ActionListener dayActionListener = e -> dayClicked(e);
        Insets lEmptyInsets = new Insets(0, 0, 0, 0);
        for (int i = 0; i < 6 * 7; i++) {
            dateToggleButton[i] = SToggleButton.of("" + i);
            dateToggleButton[i].setMargin(lEmptyInsets);
            dateToggleButton[i].addActionListener(dayActionListener);
        }

        // goto-today button
        SButton todayButton = SButton.of(SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_TODAY))
                .asImageButton()
                .onAction(evt -> {
                    displayedLocalDate(LocalDate.now());
                });

        // setup defaults
        mode(SINGLE);
        locale(Locale.getDefault());
        displayedLocalDate(localDate != null ? localDate : LocalDate.now());
        refreshLabels();
        value(localDate);

        // construct GUI; we use always available layout managers. Miglayout would have been better.
        setLayout(new BorderLayout());

        // layout header
        SMigPanel headerJPanel = SMigPanel.of().fillX().noGaps().margin(0, 0, 5, 0);
        headerJPanel.addComponent(iconButton(DATEPICKER_PREVYEAR, this::prevYear));
        headerJPanel.addComponent(iconButton(DATEPICKER_PREVMONTH, this::prevMonth));
        headerJPanel.addComponent(monthTextField).sizeGroup("monthyear").alignX(AlignX.TRAILING).gapAfter("2px").growX().pushX();
        headerJPanel.addComponent(yearTextField).sizeGroup("monthyear").alignX(AlignX.LEADING).growX().pushX();
        headerJPanel.addComponent(iconButton(DATEPICKER_NEXTMONTH, this::nextMonth));
        headerJPanel.addComponent(iconButton(DATEPICKER_NEXTYEAR, this::nextYear));
        add(headerJPanel, BorderLayout.NORTH);

        // layout center
        JPanel contentJPanel = new JPanel();
        contentJPanel.setLayout(new GridLayout(7, 8, 0, 0));
        contentJPanel.add(todayButton);
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

    private <T> STextField<T> headerFont(STextField<T> sTextField) {
        return sTextField.font(sTextField.getFont().deriveFont(sTextField.getFont().getSize() * 1.3f).deriveFont(Font.BOLD));
    }
    private SLabel weekdayFont(SLabel sLabel) {
        return sLabel.font(sLabel.getFont().deriveFont(Font.BOLD));
    }
    private SLabel weeknumberFont(SLabel sLabel) {
        return sLabel.font(sLabel.getFont().deriveFont(Font.ITALIC));
    }

    private void dayClicked(ActionEvent e) {
        // extract the date that was clicked
        String dateStr = ((JToggleButton) e.getSource()).getActionCommand();
        final LocalDate clickedLocalDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

        // the resulting values
        List<LocalDate> selection = new ArrayList<LocalDate>(this.selection);
        LocalDate newValue = clickedLocalDate;

        // what modifiers were pressed?
        boolean clickedIsInSelection = selection.contains(clickedLocalDate);
        boolean shiftPressed = (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0; // for a range
        boolean ctrlPressed = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0;
        LocalDate rangeStart = (value != null ? value : clickedLocalDate);
        LocalDate fromLocalDate = rangeStart.isBefore(clickedLocalDate) ? rangeStart : clickedLocalDate;
        LocalDate toLocalDate = rangeStart.isBefore(clickedLocalDate) ? clickedLocalDate : rangeStart;

        // If we're not extending a selection, clear the selection
        if (mode == SINGLE || (mode == RANGE && !shiftPressed) || (mode == MULTIPLE && !shiftPressed && !ctrlPressed)) {
            selection.clear();
        }

        // If clicked an already selected date
        if (clickedIsInSelection && !shiftPressed) {
            // remove from selection
            selection.remove(clickedLocalDate);
            newValue = null;
        }
        else {
            if (!selection.contains(clickedLocalDate)) {
                selection.add(clickedLocalDate);
            }
        }

        // Add range
        if (shiftPressed && (mode == RANGE || mode == MULTIPLE)) {
            // add all dates to a new range
            LocalDate localDate = fromLocalDate;
            while (!localDate.isAfter(toLocalDate)) {
                if (!selection.contains(localDate)) {
                    selection.add(localDate);
                }
                localDate = localDate.plusDays(1);
            }
        }

        // set
        setSelectionInternal(selection);
        setValueInternal(newValue);
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
        if (v == null && this.value != null && selection.contains(this.value)) {
            removeFromSelection(this.value);
        }

        setValueInternal(v);

        if (v != null && !selection.contains(v)) {
            setSelection(List.of(v));
        }
        refreshDisplayedDateBasedComponents();
    }
    public void setValueInternal(LocalDate v) {
        try {
            // update calendar
            fireVetoableChange(VALUE, this.value, v);
            firePropertyChange(VALUE, this.value, this.value = v);
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
        setSelectionInternal(v);

        // update calendar
        if (this.value != null && !selection.contains(this.value) && selection.size() > 0) {
            setValue(selection.get(0));
        }

        // refresh
        refreshSelection();
    }
    public void setSelectionInternal(List<LocalDate> v) {
        try {
            fireVetoableChange(SELECTION, this.selection, v);
            firePropertyChange(SELECTION, this.selection, this.selection = v);
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
    private Color weekendLabelColor = ColorUtil.brighterOrDarker(new JLabel().getForeground(), 0.2);
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
        Color normalDayColor = new JLabel().getForeground();
        for (int i = 0; i < 7; i++) {
            LocalDate localDate = MONDAY.plusDays(i);

            // assign day
            daynameLabels[i].setText(dateTimeFormatter.format(localDate));

            // highlight weekend
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            daynameLabels[i].setForeground(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY ? weekendLabelColor : normalDayColor);
        }

        // month
        final LocalDate JAN1ST = LocalDate.of(2024, 1, 1);
        int maxLen = 0;
        for (int i = 0; i < 12; i++) {
            int len = monthFormat.toString(JAN1ST.plusMonths(i)).length();
            if (len > maxLen) {
                maxLen = len;
            }
        }
        monthTextField.columns(maxLen);

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
        JToggleButton toggleButtonForColor = new JToggleButton();
        Color background = toggleButtonForColor.getBackground();
        Color todayBorderColor = ColorUtil.brighterOrDarker(background, 0.1);
        Border todayBorder = BorderFactory.createLineBorder(todayBorderColor, 2);
        Border notTodayBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        Color altBackgroundColor = ColorUtil.brighterOrDarker(background, 0.1);
        LocalDate startDate = displayedLocalDate.minusDays(firstOfMonthIdx - 1);
        for (int idx = 0; idx < 6 * 7; idx++) {
            LocalDate localDate = startDate.plusDays(idx);
            String localDateISO = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            dateToggleButton[idx]
                    .name(localDateISO)
                    .text("" + localDate.getDayOfMonth())
                    .actionCommand(localDateISO);

            // highlight today and buttons outside of current month
            boolean otherMonth = (localDate.getMonthValue() != displayedMonth);
            dateToggleButton[idx]
                    .border(isToday(localDate) ? todayBorder : notTodayBorder)
                    .background(otherMonth ? altBackgroundColor : background);
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
