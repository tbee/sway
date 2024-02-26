package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.support.FocusInterpreter;
import org.tbee.util.ExceptionUtil;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

public class STextArea extends JPanel implements
        ComponentMixin<STextArea>,
        ExceptionHandlerMixin<STextArea>,
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
	 * 
	 * @param v
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
    final static public String TEXT = "text";
    public STextArea text(String v) {
    	setText(v);
    	return this;
    }
    public BindingEndpoint<String> text$() {
        return BindingEndpoint.of(this, TEXT, exceptionHandler);
    }


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
    ExceptionHandler exceptionHandler = this::handleException;

    private boolean handleException(Throwable e, JComponent component, Object oldValue, Object newValue) {
        return handleException(e);
    }
    private boolean handleException(Throwable e) {

        // Force focus back
        SwingUtilities.invokeLater(() -> this.grabFocus());

        // Display the error
        if (LOGGER.isDebugEnabled()) LOGGER.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }


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
