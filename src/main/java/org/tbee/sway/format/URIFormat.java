package org.tbee.sway.format;

import java.net.URI;
import java.net.URISyntaxException;

public class URIFormat implements Format<URI> {

    @Override
    public String toString(URI value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public URI toValue(String string) {
        try {
            return string.isBlank() ? null : new URI(string);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}