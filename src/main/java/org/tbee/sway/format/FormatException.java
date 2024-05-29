package org.tbee.sway.format;

public class FormatException extends RuntimeException {
    public FormatException() {
    }

    public FormatException(String message) {
        super(message);
    }

    public FormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormatException(Throwable cause) {
        super(cause);
    }
}
