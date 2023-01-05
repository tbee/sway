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

    public enum Usage { MENU // normally 16x16
    }

    final static Map<String, Icon> icons = new HashMap<>();

    synchronized static public void register(String name, Usage usage, Icon icon) {
        icons.put(key(name, usage), icon);
    }
    synchronized static public void unregister(String name, Usage usage) {
        icons.remove(key(name, usage));
    }
    synchronized static public Icon find(String name, Usage usage) {
        String key = key(name, usage);
        Icon icon = icons.get(key);
        if (!icons.containsKey(key) && logger.isDebugEnabled()) {
            icons.put(key, null); // prevent the message from appearing again
            logger.debug("FYI: a Sway component is looking for an icon '" + name + "' to be used in " + usage + ", but none is registered.");
        }
        return icon;
    }

    private static String key(String name, Usage usage) {
        return name + "@" + usage;
    }
}
