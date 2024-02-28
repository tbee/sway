package org.tbee.sway.text;

import org.tbee.sway.SOptionPane;

import javax.swing.JComponent;
import javax.swing.text.DocumentFilter;
import java.awt.Component;
import java.awt.Toolkit;
import java.util.function.Supplier;

/**
 * Limit any text component to a maximum length
 * To use DocumentFilterSize, create an instance and then attach it to a document using the setDocumentFilter method defined in the AbstractDocument class.
 * ((AbstractDocument)jTextArea.getDocument()).setDocumentFilter(new DocumentFilterSize( () -> 20 ));
 * <p>
 * Although it is possible to have documents that do not descend from AbstractDocument, by default Swing text components use AbstractDocument subclasses for their documents.
 */
public class DocumentFilterSize extends DocumentFilter {
    private final Component component;
    private final Supplier<Integer> maxsizeSupplier;
    private final boolean showPopup;

    public DocumentFilterSize(Component component, Supplier<Integer> maxsizeSupplier) {
        this(component, false, maxsizeSupplier);
    }

    public DocumentFilterSize(Component component, boolean showPopup, Supplier<Integer> maxsizeSupplier) {
        this.component = component;
        this.maxsizeSupplier = maxsizeSupplier;
        this.showPopup = showPopup;
    }

    /** maxsize */

    public void insertString(FilterBypass fb, int offs, String str, javax.swing.text.AttributeSet a)
            throws javax.swing.text.BadLocationException {
        Integer maxsize = maxsizeSupplier.get();

        // This rejects the entire insertion if it would make the contents too long.
        // Another option would be to truncate the inserted string so the contents would be exactly maxCharacters in length.
        if (fb == null || fb.getDocument() == null || str == null || (fb.getDocument().getLength() + str.length()) <= maxsize)
            super.insertString(fb, offs, str, a);
        else {
            Toolkit.getDefaultToolkit().beep();
            if (showPopup) {
                SOptionPane.ofError(component, "Error", "Max " + maxsize);
            }
        }
    }

    public void replace(FilterBypass fb, int offs, int length, String str, javax.swing.text.AttributeSet a)
            throws javax.swing.text.BadLocationException {
        Integer maxsize = maxsizeSupplier.get();

        // This rejects the entire replacement if it would make the contents too long.
        // Another option would be to truncate the replacement string so the contents would be exactly maxCharacters in length.
        if (fb == null || fb.getDocument() == null || str == null || (fb.getDocument().getLength() + str.length() - length) <= maxsize)
            super.replace(fb, offs, length, str, a);
        else {
            Toolkit.getDefaultToolkit().beep();
            if (showPopup) {
                SOptionPane.ofError(component, "Error", "Max " + maxsize);
            }
        }
    }
}
