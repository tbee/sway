package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;

public interface PreferenceMixin<T> {

    /**
     * The name-for-preferences must be unique in the application, but the same on every start.
     * So something like an application wide unique screen name, combined with a in-screen unique component name.
     */
    void setNameForPreferences(String v);
    default T nameForPreferences(String v) {
        setNameForPreferences(v);
        return (T)this;
    }
    String NAMEFORPREFERENCES = "nameForPreferences";
    default BindingEndpoint<String> nameForPreferences$() {
        return BindingEndpoint.of(this, NAMEFORPREFERENCES);
    }
}
