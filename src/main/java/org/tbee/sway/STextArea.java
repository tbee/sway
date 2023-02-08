package org.tbee.sway;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindUtil;
import org.tbee.sway.binding.Binding;
import org.tbee.sway.support.FocusInterpreter;

public class STextArea extends SBorderPanel {

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


    // ===========================================================================
    // FLUENT API

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


    // ========================================================
    // BIND

    /**
     * Will create a binding to a specific bean/property.
     * Use binding(BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
     *
     * @param bean
     * @param propertyName
     * @return Binding, so unbind() can be called
     */
    public Binding binding(Object bean, String propertyName) {
        return BindUtil.bind(this, TEXT, bean, propertyName, null);
    }

    /**
     * Will create a binding to a specific bean/property.
     * Use bind(BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
     *
     * @param bean
     * @param propertyName
     * @return this, for fluent API
     */
    public STextArea bind(Object bean, String propertyName) {
        binding(bean, propertyName);
        return this;
    }

    /**
     * Bind to a bean wrapper's property.
     * This will allow the swap the bean (in the BeanBinder) without having to rebind.
     *
     * @param beanBinder
     * @param propertyName
     * @return Binding, so unbind() can be called
     */
    public Binding binding(BeanBinder<?> beanBinder, String propertyName) {
        return BindUtil.bind(this, TEXT, beanBinder, propertyName, null);
    }

    /**
     * Bind to a bean wrapper's property.
     * This will allow the swap the bean (in the BeanBinder) without having to rebind.
     * @param beanBinder
     * @param propertyName
     * @return this, for fluent API
     */
    public STextArea bind(BeanBinder<?> beanBinder, String propertyName) {
        binding(beanBinder, propertyName);
        return this;
    }
}
