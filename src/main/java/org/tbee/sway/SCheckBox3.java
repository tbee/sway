/*
 * Maintenance tip - There were some tricks to getting this code
 * working:
 *
 * 1. You have to overwite addMouseListener() to do nothing
 * 2. You have to add a mouse event on mousePressed by calling super.addMouseListener()
 * 3. You have to replace the UIActionMap for the keyboard event "pressed" with your own one.
 * 4. You have to remove the UIActionMap for the keyboard event "released".
 * 5. You have to grab focus when the next state is entered, otherwise clicking on the component won't get the focus.
 * 6. You have to make a ButtonModelThreeState as a button model that wraps the original button model and does state management.
 */

package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindUtil;
import org.tbee.sway.binding.Binding;
import org.tbee.sway.support.IconRegistry;
import org.tbee.util.ExceptionUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * This creates a 3 state checkbox, use getSelected3 to determine whether the checkbox is true, false or null
 * It draws its "null" states using the "armed" mode of JCheckBox.
 * If the SELECTED, UNSELECTED, UNDETERMINED icons are specified in the IconRegistry, then these will be drawn.
 */
public class SCheckBox3 extends JCheckBox {
	static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SCheckBox3.class);


	/**
	 *
	 */
	public SCheckBox3() {
		super();
		construct();
		updateIcon();
	}

	/**
	 *
	 * @param text
	 */
	public SCheckBox3(String text) {
		super(text);
		construct();
		updateIcon();
	}

	/**
	 *
	 * @param icon
	 */
	public SCheckBox3(Icon icon) {
		super(icon);
		construct();
		explicitIcon = true;
	}

	/**
	 *
	 * @param text
	 * @param icon
	 */
	public SCheckBox3(String text, Icon icon) {
		super(text, icon);
		construct();
		explicitIcon = true;
	}

	private void construct() {

		// geen interne insets
		setMargin(new Insets(0, 0, 0, 0));

		// Add a listener for when the mouse is pressed
		super.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!isEnabled()) {
					return;
				}
				grabFocus();
				nextState();
			}
		});

		// Reset the keyboard action map
		ActionMap map = new ActionMapUIResource();
		map.put("pressed", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!isEnabled()) {
					return;
				}
				grabFocus();
				nextState();
			}
		});
		map.put("released", null);
		SwingUtilities.replaceUIActionMap(this, map);
	}

	private boolean explicitIcon = false;

	/**
	 * No one may add mouse listeners, not even Swing!
	 */
	public void addMouseListener(MouseListener l) { }

	// ==================================================
	// SELECTED3

	/**
	 * Selected3 to not conflict with selected
	 */
	public void setSelected3(Boolean v) {
		Boolean oldValue = getSelected3();
		ButtonModel buttonModel = getModel();
		if (v == null) {
			buttonModel.setArmed(true);
			buttonModel.setPressed(true);
			buttonModel.setSelected(false);
			super.setSelected(false); // keep in sync
		}
		else if (v) {
			buttonModel.setArmed(false);
			buttonModel.setPressed(false);
			buttonModel.setSelected(true);
			super.setSelected(true); // keep in sync
		}
		else {
			buttonModel.setArmed(false);
			buttonModel.setPressed(false);
			buttonModel.setSelected(false);
			super.setSelected(false); // keep in sync
		}
		firePropertyChange(SELECTED3, oldValue, v);
		updateIcon();
	}
	public Boolean getSelected3() {
		ButtonModel buttonModel = getModel();
		if (buttonModel.isSelected() && !buttonModel.isArmed()) {
			// normal black tick
			return Boolean.TRUE;
		}
		else if (!buttonModel.isSelected() && buttonModel.isArmed()) {
			// don't care grey tick
			return null;
		}
		else {
			// normal deselected
			return Boolean.FALSE;
		}
	}
	final static public String SELECTED3 = "selected3";

	private void updateIcon() {
		if (explicitIcon) {
			return;
		}
		Boolean v = getSelected3();
		if (v == null) {
			super.setIcon(IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNDETERMINED));
		}
		else if (v) {
			super.setIcon(IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED));
		}
		else {
			super.setIcon(IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED));
		}
	}

	/**
	 * We rotate between TRUE, FALSE and NULL.
	 */
	private void nextState() {
		Boolean selected3 = getSelected3();
		if (selected3 == null) {
			setSelected3(false);
		}
		else if (!selected3) {
			setSelected3(true);
		}
		else {
			setSelected3(getAllowUndetermined() ? null : false);
		}
	}

	/**
	 * This property should not be used, but if you do, it will behave like AllowUndetermined = false
	 * @param v
	 */
	@Override
	public void setSelected(boolean v) {
		super.setSelected(v);
		setSelected3(v);
	}

	// ==================================================
	// PROPERTIES

	/** AllowUndetermined */
	public void setAllowUndetermined(boolean v) {
		firePropertyChange(ALLOWUNDETERMINED, allowUndetermined, allowUndetermined = v);
		if (!v && getSelected3() == null) {
			nextState();
		}
	}
	public boolean getAllowUndetermined() {
		return allowUndetermined;
	}
	private boolean allowUndetermined = true;
	final static public String ALLOWUNDETERMINED = "allowUndetermined";
	public SCheckBox3 allowUndetermined(boolean v) {
		setAllowUndetermined(v);
		return this;
	}

	@Override
	public void setIcon(Icon v) {
		super.setIcon(v);
		explicitIcon = true;
	}

	// ==================================================
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
		return BindUtil.bind(this, SELECTED3, bean, propertyName, this::handleException);
	}

	/**
	 * Will create a binding to a specific bean/property.
	 * Use bind(BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
	 *
	 * @param bean
	 * @param propertyName
	 * @return this, for fluent API
	 */
	public SCheckBox3 bind(Object bean, String propertyName) {
		Binding binding = binding(bean, propertyName);
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
	public Binding binding(BeanBinder beanBinder, String propertyName) {
		return BindUtil.bind(this, SELECTED3, beanBinder, propertyName, this::handleException);
	}

	/**
	 * Bind to a bean wrapper's property.
	 * This will allow the swap the bean (in the BeanBinder) without having to rebind.
	 * @param beanBinder
	 * @param propertyName
	 * @return this, for fluent API
	 */
	public SCheckBox3 bind(BeanBinder beanBinder, String propertyName) {
		Binding binding = binding(beanBinder, propertyName);
		return this;
	}

	protected boolean handleException(Throwable e, JComponent component, Object oldValue, Object newValue) {
		return handleException(e);
	}
	protected boolean handleException(Throwable e) {
		// Force focus back
		SwingUtilities.invokeLater(() -> this.grabFocus());

		// Display the error
		if (logger.isDebugEnabled()) logger.debug(e.getMessage(), e);
		JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

		// Mark exception as handled
		return true;
	}

	// ==============================================
	// FLUENT API

	public SCheckBox3 name(String v) {
		setName(v);
		return this;
	}

	public SCheckBox3 toolTipText(String t) {
		super.setToolTipText(t);
		return this;
	}

	public SCheckBox3 enabled(boolean v) {
		super.setEnabled(v);
		return this;
	}

	public SCheckBox3 margin(Insets m) {
		super.setMargin(m);
		return this;
	}

	public SCheckBox3 onAction(ActionListener l) {
		super.addActionListener(l);
		return this;
	}

	public SCheckBox3 action(Action v) {
		super.setAction(v);
		return this;
	}

	public SCheckBox3 icon(Icon v) {
		super.setIcon(v);
		return this;
	}

	public SCheckBox3 text(String v) {
		super.setText(v);
		return this;
	}

	public SCheckBox3 visible(boolean value) {
		setVisible(value);
		return this;
	}
}

