package org.tbee.sway;

import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;

import javax.swing.Icon;
import java.util.HashMap;
import java.util.Map;

import static org.tbee.sway.support.IkonliUtil.createIcon;

/**
 * This is where the components in Sway come looking for their icons.
 * The user can register them here.
 */
public class SIconRegistry {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SIconRegistry.class);

    final static Map<String, Icon> icons = new HashMap<>();

    static {
        // Register default icons for those that are required visually
        // https://kordamp.org/ikonli/cheat-sheet-materialdesign2.html
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.TEXTFIELD_POPUP, createIcon(MaterialDesignM.MENU, SIconRegistry.SwayInternallyUsedIcon.TEXTFIELD_POPUP.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_NEXTMONTH, createIcon(MaterialDesignA.ARROW_RIGHT_BOLD, SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_NEXTMONTH.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_PREVMONTH, createIcon(MaterialDesignA.ARROW_LEFT_BOLD, SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_PREVMONTH.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_NEXTYEAR, createIcon(MaterialDesignA.ARROW_RIGHT_BOLD, SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_NEXTYEAR.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_PREVYEAR, createIcon(MaterialDesignA.ARROW_LEFT_BOLD, SIconRegistry.SwayInternallyUsedIcon.DATEPICKER_PREVYEAR.typicalSize()));
    }

    public enum SwayInternallyUsedIcon { //
    	BUTTON_OK("ok@button", 16), //
    	BUTTON_CANCEL("cancel@button", 16), //
    	BUTTON_YES("yes@button", 16), //
    	BUTTON_NO("no@button", 16), //
    	CHECKBOX_SELECTED("selected@checkbox", 16), //
    	CHECKBOX_UNSELECTED("unselected@checkbox", 16), //
    	CHECKBOX_UNDETERMINED("undetermined@checkbox", 16),
        MENU_COPY("copy@menu", 16), //
        MENU_CUT("cut@menu", 16), //
        MENU_PASTE("paste@menu", 16), //
        MENU_FILTER("filter@menu", 16), //
        MENU_SELECTION("selection@menu", 16), //
        OVERLAY_LOADING("loading@overlay", 48),
        TEXTFIELD_POPUP("popup@textfield", 16),
        DATEPICKER_NEXTMONTH("nextMonth@textfield", 20),
        DATEPICKER_PREVMONTH("prevMonth@textfield", 20),
        DATEPICKER_NEXTYEAR("nextYear@textfield", 28),
        DATEPICKER_PREVYEAR("prevYear@textfield", 28),
        ; //

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
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.MENU_COPY, createIcon(MaterialDesignC.CONTENT_COPY, SIconRegistry.SwayInternallyUsedIcon.MENU_COPY.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.MENU_CUT, createIcon(MaterialDesignC.CONTENT_CUT, SIconRegistry.SwayInternallyUsedIcon.MENU_CUT.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.MENU_PASTE, createIcon(MaterialDesignC.CONTENT_PASTE, SIconRegistry.SwayInternallyUsedIcon.MENU_PASTE.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.MENU_FILTER, createIcon(MaterialDesignF.FILTER, SIconRegistry.SwayInternallyUsedIcon.MENU_FILTER.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.MENU_SELECTION, createIcon(MaterialDesignS.SELECTION, SIconRegistry.SwayInternallyUsedIcon.MENU_SELECTION.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING, createIcon(MaterialDesignR.REFRESH, SIconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.TEXTFIELD_POPUP, createIcon(MaterialDesignM.MENU, SIconRegistry.SwayInternallyUsedIcon.TEXTFIELD_POPUP.typicalSize()));

        registerCheckboxIcons();
    }

    /**
     * SCheckBox3 usually has poor rendering of all three states, register icons to make fix that.
     */
    static public void registerCheckboxIcons() {
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED, createIcon(MaterialDesignC.CHECKBOX_MARKED, SIconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED, createIcon(MaterialDesignC.CHECKBOX_BLANK_OUTLINE, SIconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED.typicalSize()));
        SIconRegistry.register(SIconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNDETERMINED, createIcon(MaterialDesignC.CHECKBOX_BLANK_OFF_OUTLINE, SIconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNDETERMINED.typicalSize()));
    }
}
