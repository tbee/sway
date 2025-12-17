package org.tbee.sway;

import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import org.kordamp.ikonli.materialdesign2.MaterialDesignN;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;

import javax.swing.Icon;
import java.util.HashMap;
import java.util.Map;

import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNDETERMINED;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_NEXTMONTH;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_NEXTYEAR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_PREVMONTH;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_PREVYEAR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_TODAY;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.MENU_COPY;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.MENU_CUT;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.MENU_FILTER;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.MENU_PASTE;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.MENU_SELECTION;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.OVERLAY_BUSY;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.FORMAT_NULL;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TEXTFIELD_POPUP;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_CLEAR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTHOUR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTMINUTE;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTSECOND;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVHOUR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVMINUTE;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVSECOND;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.ZONEPICKER_MINUS;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.ZONEPICKER_PLUS;
import static org.tbee.sway.support.IkonliUtil.createIcon;

/**
 * This is where the components in Sway come looking for their icons.
 * The user can register them here.
 */
public class SIconRegistry {
    static private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SIconRegistry.class);

    final static Map<String, Icon> icons = new HashMap<>();

    static {
        // Register default icons for those that are required visually
        // https://kordamp.org/ikonli/cheat-sheet-materialdesign2.html
        // https://kordamp.org/ikonli/cheat-sheet-material2.html
        register(TEXTFIELD_POPUP, createIcon(MaterialDesignM.MENU, TEXTFIELD_POPUP.typicalSize()));

        register(DATEPICKER_NEXTMONTH, createIcon(Material2AL.KEYBOARD_ARROW_RIGHT, DATEPICKER_NEXTMONTH.typicalSize() - 2));
        register(DATEPICKER_PREVMONTH, createIcon(Material2AL.KEYBOARD_ARROW_LEFT, DATEPICKER_PREVMONTH.typicalSize() - 2));
        register(DATEPICKER_NEXTYEAR, createIcon(Material2AL.KEYBOARD_ARROW_RIGHT, DATEPICKER_NEXTYEAR.typicalSize() + 2));
        register(DATEPICKER_PREVYEAR, createIcon(Material2AL.KEYBOARD_ARROW_LEFT, DATEPICKER_PREVYEAR.typicalSize() + 2));
        register(DATEPICKER_TODAY, createIcon(MaterialDesignC.CALENDAR_TODAY, DATEPICKER_TODAY.typicalSize()));

        register(TIMEPICKER_NEXTHOUR, createIcon(Material2AL.KEYBOARD_ARROW_UP, TIMEPICKER_NEXTHOUR.typicalSize()));
        register(TIMEPICKER_PREVHOUR, createIcon(Material2AL.KEYBOARD_ARROW_DOWN, TIMEPICKER_PREVHOUR.typicalSize()));
        register(TIMEPICKER_NEXTMINUTE, createIcon(Material2AL.KEYBOARD_ARROW_UP, TIMEPICKER_NEXTMINUTE.typicalSize()));
        register(TIMEPICKER_PREVMINUTE, createIcon(Material2AL.KEYBOARD_ARROW_DOWN, TIMEPICKER_PREVMINUTE.typicalSize()));
        register(TIMEPICKER_NEXTSECOND, createIcon(Material2AL.KEYBOARD_ARROW_UP, TIMEPICKER_NEXTSECOND.typicalSize()));
        register(TIMEPICKER_PREVSECOND, createIcon(Material2AL.KEYBOARD_ARROW_DOWN, TIMEPICKER_PREVSECOND.typicalSize()));
        register(TIMEPICKER_CLEAR, createIcon(Material2AL.CLEAR, TIMEPICKER_CLEAR.typicalSize()));
        register(ZONEPICKER_PLUS, createIcon(MaterialDesignP.PLUS, ZONEPICKER_PLUS.typicalSize()));
        register(ZONEPICKER_MINUS, createIcon(MaterialDesignM.MINUS, ZONEPICKER_PLUS.typicalSize()));
    }

    public enum SwayInternallyUsedIcon { //
    	BUTTON_OK("ok@button", 16), //
    	BUTTON_CANCEL("cancel@button", 16), //
    	BUTTON_YES("yes@button", 16), //
    	BUTTON_NO("no@button", 16), //

    	CHECKBOX_SELECTED("selected@checkbox", 16), //
    	CHECKBOX_UNSELECTED("unselected@checkbox", 16), //
    	CHECKBOX_UNDETERMINED("undetermined@checkbox", 16),

        FORMAT_NULL("null@format", 8),

        MENU_COPY("copy@menu", 16), //
        MENU_CUT("cut@menu", 16), //
        MENU_PASTE("paste@menu", 16), //
        MENU_FILTER("filter@menu", 16), //
        MENU_SELECTION("selection@menu", 16), //

        OVERLAY_BUSY("busy@overlay", 48),
        OVERLAY_LOADING("loading@overlay", 48),

        TEXTFIELD_POPUP("popup@textfield", 16),

        DATEPICKER_NEXTMONTH("nextMonth@datepicker", 20),
        DATEPICKER_PREVMONTH("prevMonth@datepicker", 20),
        DATEPICKER_NEXTYEAR("nextYear@datepicker", 20),
        DATEPICKER_PREVYEAR("prevYear@datepicker", 20),
        DATEPICKER_TODAY("today@datepicker", 16),

        TIMEPICKER_NEXTHOUR("nextHour@timepicker", 20),
        TIMEPICKER_PREVHOUR("prevHour@timepicker", 20),
        TIMEPICKER_NEXTMINUTE("nextMinute@timepicker", 20),
        TIMEPICKER_PREVMINUTE("prevMinute@timepicker", 20),
        TIMEPICKER_NEXTSECOND("nextSecond@timepicker", 20),
        TIMEPICKER_PREVSECOND("prevSecond@timepicker", 20),
        TIMEPICKER_CLEAR("clear@timepicker", 14),

        ZONEPICKER_PLUS("plus@zonepicker", 14),
        ZONEPICKER_MINUS("minus@zonepicker", 14),
        ;

        final String id;
        final int typicalSize;

    	SwayInternallyUsedIcon(String id, int typicalSize) {
    		this.id = id;
            this.typicalSize = typicalSize;
        }

    	public String id() {
    		return id;
    	}
        public int typicalSize() {
            return typicalSize;
        }
	}

    synchronized static public void register(SwayInternallyUsedIcon swayIcon, Icon icon) {
        register(swayIcon.id(), icon);
    }
    synchronized static public void register(String id, Icon icon) {
        icons.put(id, icon);
    }
    synchronized static public void unregister(String id) {
        icons.remove(id);
    }

    synchronized static public Icon find(SwayInternallyUsedIcon swayIcon) {
        return find(swayIcon.id());
    }
    synchronized static public Icon find(String id) {
        Icon icon = icons.get(id);
        if (!icons.containsKey(id) && logger.isDebugEnabled()) {
            icons.put(id, null); // prevent the message from appearing again
            logger.debug("FYI: a Sway component is looking for an icon '" + id + "', but none is registered.");
        }
        return icon;
    }

    static public void registerDefaultIcons() {
        // https://kordamp.org/ikonli/cheat-sheet-materialdesign2.html
        register(MENU_COPY, createIcon(MaterialDesignC.CONTENT_COPY, MENU_COPY.typicalSize()));
        register(MENU_CUT, createIcon(MaterialDesignC.CONTENT_CUT, MENU_CUT.typicalSize()));
        register(MENU_PASTE, createIcon(MaterialDesignC.CONTENT_PASTE, MENU_PASTE.typicalSize()));
        register(MENU_FILTER, createIcon(MaterialDesignF.FILTER, MENU_FILTER.typicalSize()));
        register(MENU_SELECTION, createIcon(MaterialDesignS.SELECTION, MENU_SELECTION.typicalSize()));
        register(OVERLAY_BUSY, createIcon(MaterialDesignP.PROGRESS_CLOCK, OVERLAY_BUSY.typicalSize()));
        register(OVERLAY_LOADING, createIcon(MaterialDesignR.REFRESH, OVERLAY_LOADING.typicalSize()));
        register(FORMAT_NULL, createIcon(MaterialDesignN.NULL, FORMAT_NULL.typicalSize()));

        registerCheckboxIcons();
    }

    /**
     * SCheckBox3 usually has poor rendering of all three states, register icons to make fix that.
     */
    static public void registerCheckboxIcons() {
        register(CHECKBOX_SELECTED, createIcon(MaterialDesignC.CHECKBOX_MARKED, CHECKBOX_SELECTED.typicalSize()));
        register(CHECKBOX_UNSELECTED, createIcon(MaterialDesignC.CHECKBOX_BLANK_OUTLINE, CHECKBOX_UNSELECTED.typicalSize()));
        register(CHECKBOX_UNDETERMINED, createIcon(MaterialDesignC.CHECKBOX_BLANK_OFF_OUTLINE, CHECKBOX_UNDETERMINED.typicalSize()));
    }
}
