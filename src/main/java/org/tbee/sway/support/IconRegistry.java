package org.tbee.sway.support;

import javax.swing.Icon;
import java.util.HashMap;
import java.util.Map;

/**
 * This is where the components in Sway come looking for their icons.
 * The user can register them here.
 */
public class IconRegistry {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(IconRegistry.class);

    public enum SwayInternallyUsedIcon { //
    	BUTTON_OK("ok@button", 16), //
    	BUTTON_CANCEL("cancel@button", 16), //
    	BUTTON_YES("yes@button", 16), //
    	BUTTON_NO("no@button", 16), //
    	CHECKBOX_SELECTED("selected@checkbox", 24), //
    	CHECKBOX_UNSELECTED("unselected@checkbox", 24), //
    	CHECKBOX_UNDETERMINED("undetermined@checkbox", 24),
        MENU_COPY("copy@menu", 16), //
        MENU_CUT("cut@menu", 16), //
        MENU_PASTE("paste@menu", 16), //
        MENU_FILTER("filter@menu", 16), //
        MENU_SELECTION("selection@menu", 16), //
        OVERLAY_LOADING("loading@overlay", 48),
        TEXTFIELD_POPUP("popup@textfield", 14),
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

    final static Map<String, Icon> icons = new HashMap<>();

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
}
