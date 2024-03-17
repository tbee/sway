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

    static public SEditorPane of() {
        return new SEditorPane();
    }
}
