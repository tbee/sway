package org.tbee.sway;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.support.HAlign;
import org.tbee.sway.support.VAlign;
import org.tbee.util.ExceptionUtil;

public class SButton extends JButton {
    final static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SButton.class);

    public SButton() {
    }

    public SButton(Icon icon) {
        super(icon);
    }

    public SButton(String text) {
        super(text);
    }

    public SButton(Action a) {
        super(a);
    }

    public SButton(String text, Icon icon) {
        super(text, icon);
    }

    // ==============================================
    // JavaBean

    /**
     * Enum variant of HorizontalAlignment
     * @param v
     */
    public void setHAlign(HAlign v) {
        HAlign old = getHAlign();
        setHorizontalAlignment(v.getSwingConstant());
        firePropertyChange(HALIGN, old, v);
    }
    public HAlign getHAlign() {
        return HAlign.of(getHorizontalAlignment());
    }
    public SButton hAlign(HAlign v) {
        setHAlign(v);
        return this;
    }
    final static public String HALIGN = "hAlign";

    /**
     * Enum variant of VerticalAlignment
     * @param v
     */
    public void setVAlign(VAlign v) {
        VAlign old = getVAlign();
        setVerticalAlignment(v.getSwingConstant());
        firePropertyChange(VALIGN, old, v);
    }
    public VAlign getVAlign() {
        return VAlign.of(getHorizontalAlignment());
    }
    public SButton VAlign(VAlign v) {
        setVAlign(v);
        return this;
    }
    final static public String VALIGN = "vAlign";

    // ==============================================
    // ExceptionHandler
    
    /**
     * Set the ExceptionHandler used a.o. when the actionListeners are called.
     * @param v
     */
    public void setExceptionHandler(ExceptionHandler v) {
        firePropertyChange(EXCEPTIONHANDLER, exceptionHandler, exceptionHandler = v);
    }
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
    public SButton exceptionHandler(ExceptionHandler v) {
        setExceptionHandler(v);
        return this;
    }
    final static public String EXCEPTIONHANDLER = "exceptionHandler";
    ExceptionHandler exceptionHandler = this::handleException;
    
    private boolean handleException(Throwable e, Object oldValue, Object newValue) {
    	
        if (LOGGER.isDebugEnabled()) LOGGER.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }

    @Override
    protected void fireActionPerformed(ActionEvent event) {
    	try {
    		super.fireActionPerformed(event);
    	}
    	catch (Throwable t) {
    		if (exceptionHandler != null && exceptionHandler.handle(t, null, null)) {
    			return;
    		}
			throw t;
    	}
    }
    
    // ==============================================
    // FLUENT API

    public SButton name(String v) {
        setName(v);
        return this;
    }

    public SButton toolTipText(String t) {
        super.setToolTipText(t);
        return this;
    }

    public SButton enabled(boolean v) {
        super.setEnabled(v);
        return this;
    }

    public SButton margin(Insets m) {
        super.setMargin(m);
        return this;
    }

    public SButton onAction(ActionListener l) {
        super.addActionListener(l);
        return this;
    }

    public SButton action(Action v) {
        super.setAction(v);
        return this;
    }

    public SButton icon(Icon v) {
        super.setIcon(v);
        return this;
    }

    public SButton text(String v) {
        super.setText(v);
        return this;
    }

    public SButton visible(boolean value) {
        setVisible(value);
        return this;
    }
}
