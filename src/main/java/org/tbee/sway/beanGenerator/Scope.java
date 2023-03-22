package org.tbee.sway.beanGenerator;

public enum Scope {
    PUBLIC("public"), PROTECTED("protected"), PACKAGE(""), PRIVATE("private");
    final String code;

    private Scope(String code) {
        this.code = code;
    }
}
