package org.tbee.sway;

import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Collection;
import java.util.List;

// TBEERNOT this class needs just a bit more work
public class SButtonPanel extends SPanelExtendable<SButtonPanel> implements
        JComponentMixin<SButtonPanel> {
	
	/**
	 * 
	 */
	public SButtonPanel() {
		super();
		construct();
	}

	/**
	 * @param components
	 */
	public SButtonPanel(Collection<? extends JComponent> components) {
		super();
		add(components);
		construct();
	}

	/**
	 * @param components
	 */
	public SButtonPanel(JComponent... components) {
		super();
		add(components);
		construct();
	}
	
	private void construct() {
		setLayout(new LayoutManager() {
			
			/**
			 *
			 */
			public void addLayoutComponent(String string, Component comp) {
			}

			/**
			 * Actually layout the components
			 */
			public void layoutContainer(Container container) {
				ButtonsInfo buttons = buttonsInfo(container);

				// if horizontal buttonbars are trailing aligned, we need the total height of all buttons to determine the starting point (totalwidth - usedwidth)
				// otherwise 0 is the starting point
				Insets insets = container.getInsets();
				int usedWidth = getAlignment() == Alignment.LEADING
							  ? container.getWidth() - insets.left
							  : (buttons.maxPreferredWidth * buttons.numberVisible) + (gap * (buttons.numberVisible - 1));
				// vertical buttonbars are top aligned, so we can just step down
				//int lUsedHeight = maxHeight * visibleCount + iGap * (visibleCount - 1);

				// set all childeren's location and size
				Component[] children = container.getComponents();
				for (int i = 0, c = children.length; i < c; i++) {
					// only visible
					if (children[i].isVisible()) {
						// horizontal means: distance from right side, so totalwidth - usedWidth - right margin = left boundary, from there step to the left
						if (getOrientation() == Orientation.HORIZONTAL) {
							children[i].setBounds( (container.getWidth() - usedWidth - insets.right) + ( i * (buttons.maxPreferredWidth + gap) ), insets.top, buttons.maxPreferredWidth, buttons.maxPreferredHeight);
						}
						// vertical means: distance from top side, so top margin and then step down
						else {
							children[i].setBounds(insets.left, (insets.top) + (i * (buttons.maxPreferredHeight + gap) ), buttons.maxPreferredWidth, buttons.maxPreferredHeight);
						}
					}
				}
			}

			/**
			 *
			 */
			public Dimension minimumLayoutSize(Container c) {
				return preferredLayoutSize(c);
			}

			/**
			 * Preferred size is sum of all buttons and gaps
			 */
			public Dimension preferredLayoutSize(Container container) {
				ButtonsInfo buttons = buttonsInfo(container);

				// total max width or height is number of buttons and the gaps (being number of buttons - 1)
				int ourPreferredWidth = (buttons.maxPreferredWidth * buttons.numberVisible) + (gap * (buttons.numberVisible - 1));
				int ourPreferredHeight = (buttons.maxPreferredHeight * buttons.numberVisible) + (gap * (buttons.numberVisible - 1));

				// determine the size
				Insets insets = container.getInsets();
				return getOrientation() == Orientation.HORIZONTAL
				     ? new Dimension(insets.left + ourPreferredWidth + insets.right, insets.top + buttons.maxPreferredHeight + insets.bottom)
				     : new Dimension(insets.left + buttons.maxPreferredWidth + insets.right, insets.top + ourPreferredHeight + insets.bottom);
			}

			private ButtonsInfo buttonsInfo(Container container) {
				Component[] children = container.getComponents();

				// calculate some aggregations
				int maxButtonPreferredWidth = 0;
				int maxButtonPreferredHeight = 0;
				int numberOfVisibleButtons = 0;
				for (int i = 0, c = children.length; i < c; i++) {
					// only for visible components
					if (children[i].isVisible()) {
						numberOfVisibleButtons++;

						// we want to know the maximum preferred width and height
						Dimension componentPreferredSize = children[i].getPreferredSize();
						maxButtonPreferredWidth = Math.max(maxButtonPreferredWidth, componentPreferredSize.width);
						maxButtonPreferredHeight = Math.max(maxButtonPreferredHeight, componentPreferredSize.height);
					}
				}
				return new ButtonsInfo(maxButtonPreferredWidth, maxButtonPreferredHeight, numberOfVisibleButtons);
			}
			record ButtonsInfo(int maxPreferredWidth, int maxPreferredHeight, int numberVisible) {}

			/**
			 *
			 */
			public void removeLayoutComponent(Component c) {
			}
		});
	}


	// ===================================================================================================
	// PROPERTIES

	/** gap */
	public int getGap() { 
		return gap; 
	}
	public void setGap(int value) { 
		gap = value; 
	}
	public SButtonPanel gap(int value) { 
		setGap(value); 
		return this; 
	}
	private int gap = 2;

	/** Orientation */
	public Orientation getOrientation() { 
		return orientation; 
	}
	public void setOrientation(Orientation value) { 
		orientation = value; 
	}
	public SButtonPanel orientation(Orientation value) { 
		setOrientation(value); 
		return this; 
	}
	private Orientation orientation = Orientation.HORIZONTAL;
	public enum Orientation {HORIZONTAL, VERTICAL};

	/** Alignment */
	public Alignment getAlignment() { 
		return alignment; 
	}
	public void setAlignment(Alignment value) { 
		alignment = value; 
	}
	public SButtonPanel alignment(Alignment value) { 
		setAlignment(value); 
		return this; 
	}
	private Alignment alignment = Alignment.TRAILING;
	public enum Alignment {LEADING, TRAILING};
	
	
	// ===================================================================================================
	// CONVENIENCE

	static public SButtonPanel of() {
		return new SButtonPanel();
	}

	static public SButtonPanel of(AbstractButton... buttons) {
		return SButtonPanel.of().add(buttons);
	}

	static public SButtonPanel of(List<? extends AbstractButton> buttons) {
		return SButtonPanel.of().add(buttons);
	}	
	
	public SButtonPanel horizontal() {
		setOrientation(Orientation.HORIZONTAL);
		return this;
	}
	public SButtonPanel vertical() {
		setOrientation(Orientation.VERTICAL);
		return this;
	}
}
