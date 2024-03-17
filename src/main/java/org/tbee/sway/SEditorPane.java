package org.tbee.sway;

import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.EditableMixin;
import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.JEditorPane;

public class SEditorPane extends JEditorPane
implements ComponentMixin<SEditorPane>,
        EditableMixin<SEditorPane>,
        JComponentMixin<SEditorPane> {

    public SEditorPane text(String v) {
        setText(v);
        return this;
    }


    public SEditorPane contentType(String v) {
        setContentType(v);
        return this;
    }

    static public SEditorPane of() {
        return new SEditorPane();
    }

    static public SEditorPane ofText() {
        return new SEditorPane().contentType("text/plain");
    }

    static public SEditorPane ofHtml() {
        return new SEditorPane().contentType("text/html");
    }

    static public SEditorPane ofRichText() {
        return new SEditorPane().contentType("text/rtf");
    }
}
