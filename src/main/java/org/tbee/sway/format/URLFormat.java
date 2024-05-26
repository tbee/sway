package org.tbee.sway.format;

import java.net.MalformedURLException;
import java.net.URL;

public class URLFormat implements Format<URL> {

    @Override
    public String toString(URL value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public URL toValue(String string) {
        try {
            return string.isBlank() ? null : new URL(string);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}