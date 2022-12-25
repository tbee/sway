package org.tbee.sway.comedia.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;

/**
 * Copy from CoMedia cbeans
 */
public class CNumericDocument extends PlainDocument {
    public static final int INTEGER = 0;
    public static final int FLOAT = 1;
    public static final int HEXDECIMAL = 1;
    private int mode;
    private boolean modified;

    public CNumericDocument() {
        this(0);
    }

    public CNumericDocument(int var1) {
        this.mode = 0;
        this.modified = false;
        this.mode = var1;
    }

    public boolean isModified() {
        return this.modified;
    }

    public void setModified() {
        this.modified = this.modified;
    }

    public void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException {
        for(int var4 = 0; var4 < var2.length(); ++var4) {
            if ((var2.charAt(var4) < '0' || var2.charAt(var4) > '9') && (this.mode == 1 || var2.charAt(var4) != '-') && (this.mode != 1 || (var2.charAt(var4) < 'A' || var2.charAt(var4) > 'F') && (var2.charAt(var4) < 'a' || var2.charAt(var4) > 'f')) && (this.mode != 1 || var2.charAt(var4) != System.getProperty("decimal.separator", ".").charAt(0))) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }

        this.modified = true;
        super.insertString(var1, var2, var3);
    }
}
