package org.tbee.sway.preference;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

public class PreferenceHelper {

    private final Object object;
    private final Supplier<String> nameForPreferenceSupplier;

    public PreferenceHelper(Object object, Supplier<String> nameForPreferenceSupplier) {
        this.object = object;
        this.nameForPreferenceSupplier = nameForPreferenceSupplier;
    }

    private String getNameForPreferences() {
        return nameForPreferenceSupplier.get();
    }

    private String key(String id) {
        return id + "@" + getNameForPreferences();
    }

    public void rememberPreference(String id, Supplier<String> supplier) {
        if (!registerChangesToPreferences()) {
            return;
        }
        ignoreChangesToPreferences(() -> {
            Preferences preferences = Preferences.userNodeForPackage(object.getClass());
            String value = supplier.get();
            System.out.println("!!! rememberPreference " + id + " = " + value);
            preferences.put(key(id), value);
        });
    }

    public void restorePreference(String id, Consumer<String> consumer) {
        if (!registerChangesToPreferences()) {
            return;
        }
        ignoreChangesToPreferences(() -> {
            Preferences preferences = Preferences.userNodeForPackage(object.getClass());
            String value = preferences.get(key(id), null);
            if (value != null) {
                System.out.println("!!! restorePreference " + id + " = " + value);
                consumer.accept(value);
            }
        });
    }

    private int ignoreChangesToPreferences = 0;  // changes to swing components should all be done in the EDT, so all changes to this variable is single threaded
    public void ignoreChangesToPreferences(Runnable runnable) {
        ignoreChangesToPreferences++;
        try {
            runnable.run();
        }
        finally {
            ignoreChangesToPreferences--;
        }
    }
    private boolean registerChangesToPreferences() {
        return ignoreChangesToPreferences == 0 && !getNameForPreferences().isBlank();
    }
}
