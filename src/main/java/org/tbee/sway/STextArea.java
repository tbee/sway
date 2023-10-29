package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.support.FocusInterpreter;
import org.tbee.util.ExceptionUtil;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Component;

public class STextArea extends SBorderPanel {

    final static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(STextArea.class);

    final private JTextArea jTextArea;
	final private FocusInterpreter focusInterpreter;
	final private FocusInterpreter.FocusInterpreterListener focusInterpreterListener;
	
	public STextArea() {
		this(3, 20);
	}
	
	public STextArea(int rows, int cols) {
		jTextArea = new JTextArea(rows, cols);
		center(new JScrollPane(jTextArea));
		
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
    public STextArea exceptionHandler(ExceptionHandler v) {
        setExceptionHandler(v);
        return this;
    }
    final static public String EXCEPTIONHANDLER = "exceptionHandler";
    ExceptionHandler exceptionHandler = this::handleException;
    public BindingEndpoint<ExceptionHandler> exceptionHandler$() {
        return BindingEndpoint.of(this, EXCEPTIONHANDLER, exceptionHandler);
    }

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

    public STextArea name(String v) {
        setName(v);
        return this;
    }

    public STextArea visible(boolean value) {
        setVisible(value);
        return this;
    }

    public SBorderPanel overlayWith(Component overlayComponent) {
        SOverlayPane.overlayWith(this, overlayComponent);
        return this;
    }
    public SBorderPanel removeOverlay(Component overlayComponent) {
        SOverlayPane.removeOverlay(this, overlayComponent);
        return this;
    }

    /**
     * Binds the default property 'text'
     */
    public STextArea bindTo(BindingEndpoint<String> bindingEndpoint) {
        text$().bindTo(bindingEndpoint);
        return this;
    }

    /**
     * Binds to the default property 'text'.
     * Binding in this way is not type safe!
     */
    public STextArea bindTo(Object bean, String propertyName) {
        return bindTo(BindingEndpoint.of(bean, propertyName));
    }

    /**
     * Binds to the default property 'text'.
     * Binding in this way is not type safe!
     */
    public STextArea bindTo(BeanBinder<?> beanBinder, String propertyName) {
        return bindTo(BindingEndpoint.of(beanBinder, propertyName));
    }
}
