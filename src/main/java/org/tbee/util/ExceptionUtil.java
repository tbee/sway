package org.tbee.util;

public class ExceptionUtil {
    /**
     * Create a displayable exception test
     *
     * @param t
     * @return
     */
    static public String determineMessage(Throwable t) {
        String message = null;
        message = t.getLocalizedMessage();
        if (message != null && message.trim().length() > 0) {
            return message.trim();
        }
        message = t.getMessage();
        if (message != null && message.trim().length() > 0) {
            return message.trim();
        }
        return t.getClass().getName();
    }

    /**
     * @param t
     * @param clazz
     * @return
     */
    static public Throwable findThrowableInChain(Throwable t, Class clazz) {
        Throwable throwable = t;
        while (throwable != null) {
            if (clazz.isAssignableFrom(throwable.getClass())) {
                return throwable;
            }
            throwable = throwable.getCause();
        }
        return null;
    }
}
