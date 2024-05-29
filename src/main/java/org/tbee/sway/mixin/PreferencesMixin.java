package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;

public interface PreferencesMixin<T> {

    /**
     * The preferencesId must be unique in the application, but the same on every start.
     * So something like an application wide unique screen name, combined with a in-screen unique component id.
     */
    void setPreferencesId(String v);
    default T preferencesId(String v) {
        setPreferencesId(v);
        return (T)this;
    }
    String PREFERENCESID = "preferencesId";
    default BindingEndpoint<String> preferencesId$() {
        return BindingEndpoint.of(this, PREFERENCESID);
    }
}
