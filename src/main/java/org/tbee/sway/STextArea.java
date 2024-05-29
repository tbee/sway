package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.EditableMixin;
import org.tbee.sway.mixin.ExceptionHandlerDefaultMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.TextMixin;
import org.tbee.sway.support.FocusInterpreter;
import org.tbee.sway.text.DocumentFilterSize;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;
import java.awt.BorderLayout;

public class STextArea extends JPanel implements
        JComponentMixin<STextArea>,
        ExceptionHandlerDefaultMixin<STextArea>,
        EditableMixin<STextArea>,
        TextMixin<STextArea>,
        BindToMixin<STextArea, String> {

    final static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(STextArea.class);

    final private JTextArea jTextArea;
	final private FocusInterpreter focusInterpreter;
	final private FocusInterpreter.FocusInterpreterListener focusInterpreterListener;
	
	public STextArea() {
		this(3, 20);
	}
	
	public STextArea(int rows, int cols) {
        setLayout(new BorderLayout());

		jTextArea = new JTextArea(rows, cols);
		add(new JScrollPane(jTextArea), BorderLayout.CENTER);

		// Detect focus lost
		// the FocusInterpreterListener must be kept in an instance variable, otherwise it will be cleared by the WeakArrayList used in the FocusInterpreter
		focusInterpreter = new FocusInterpreter(jTextArea);
		focusInterpreterListener = new FocusInterpreter.FocusInterpreterListener() {
			public void focusChanged(FocusInterpreter.FocusInterpreterEvent evt) {
				if (evt.getState() == FocusInterpreter.State.FOCUS_LOST) {
					
					// fire a PCE for binding
					firePropertyChange(TEXT, text, (text = getText())); 
				}
			}
		};
		focusInterpreter.addFocusListener(focusInterpreterListener);
	}

    // ===========================================================================================================================
    // For Mixins

    @Override
    public BindingEndpoint<String> defaultBindingEndpoint() {
        return text$();
    }

    // ===========================================================================
    // PROPERTIES

    /**
     * Text
     */
    public void setText(String v) {
    	String old = jTextArea.getText();
    	jTextArea.setText(v);
    	firePropertyChange(TEXT, old, text = v);
    }
    public String getText() {
    	return jTextArea.getText();
    }
	private String text = "";

    /**
     * editable
     */
    public void setEditable(boolean v) {
        jTextArea.setEditable(v);
    }
    public boolean isEditable() {
        return jTextArea.isEditable();
    }

    /**
     * maxLength
     */
    public void setMaxLength(int value) {
        maxLength = value;
        if (documentFilterSize == null) {
            documentFilterSize = new DocumentFilterSize(this, () -> maxLength);
            ((AbstractDocument) jTextArea.getDocument()).setDocumentFilter(documentFilterSize);
        }
    }
    private DocumentFilterSize documentFilterSize = null;

    public int getMaxLength() { return maxLength; }
    public STextArea maxLength(int value) {
        setMaxLength(value);
        return this;
    }
    volatile private int maxLength = -1;
    final static public String MAXLENGTH_PROPERTY_ID = "maxLength";

    // ========================================================
    // EXCEPTION HANDLER

    /**
     * Set the ExceptionHandler used a.o. in binding
     * @param v
     */
    public void setExceptionHandler(ExceptionHandler v) {
        firePropertyChange(EXCEPTIONHANDLER, exceptionHandler, exceptionHandler = v);
    }
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
    private ExceptionHandler exceptionHandler = this::handleException;


    // ===========================================================================
    // FLUENT API

    static public STextArea of() {
        return new STextArea();
    }

    static public STextArea of(int rows, int cols) {
        return new STextArea(rows, cols);
    }

    @Override
    public void setName(String v) {
        super.setName(v);
        jTextArea.setName(v + ".jTextArea"); // For tests we need to address the actual list
    }
}
