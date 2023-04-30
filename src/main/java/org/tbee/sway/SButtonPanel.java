package org.tbee.sway;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

// TBEERNOT this class needs just a bit more work
public class SButtonPanel extends SPanelExtendable<SButtonPanel> {
	
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
				// get inserts and childeren
				Insets lInsets = container.getInsets();
				Component[] lChildren = container.getComponents();

				// calculate some aggrigations
				int lMaxWidth = 0;
				int lMaxHeight = 0;
				int lVisibleCount = 0;
				for (int i = 0, c = lChildren.length; i < c; i++) {
					// only visible components are taken into account
					if (lChildren[i].isVisible()) {
						lVisibleCount++;

						// we want to know the maximum preferred width and height
						Dimension lComponentPreferredSize = lChildren[i].getPreferredSize();
						lMaxWidth = Math.max(lMaxWidth, lComponentPreferredSize.width);
						lMaxHeight = Math.max(lMaxHeight, lComponentPreferredSize.height);
					}
				}

				// if horizontal buttonbars are trailing aligned, we need the total height of all buttons to determine the starting point (totalwidth - usedwidth)
				// otherwise 0 is the starting point
				int lUsedWidth = getAlignment() == Alignment.LEADING ? container.getWidth() - container.getInsets().left : lMaxWidth * lVisibleCount + gap * (lVisibleCount - 1);
				// vertical buttonbars are top aligned, so we can just step down
				//int lUsedHeight = maxHeight * visibleCount + iGap * (visibleCount - 1);

				// set all childeren's location and size
				for (int i = 0, c = lChildren.length; i < c; i++) {
					// only visible
					if (lChildren[i].isVisible()) {
						// horizontal means: distance from right side, so totalwidth - usedWidth - right margin = left boundary, from there step to the left
						if (getOrientation() == Orientation.HORIZONTAL) lChildren[i].setBounds( (container.getWidth() - lUsedWidth - lInsets.right) + ( i * (lMaxWidth + gap) ), lInsets.top, lMaxWidth, lMaxHeight);
						// vertical means: distance from top side, so top margin and then step down
						else lChildren[i].setBounds(lInsets.left, (lInsets.top) + (i * (lMaxHeight + gap) ), lMaxWidth, lMaxHeight);
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
				// get insets and childeren
				Insets lInsets = container.getInsets();
				Component[] lChildren = container.getComponents();

				// calculate some aggrigations
				int lMaxWidth = 0;
				int lMaxHeight = 0;
				int lVisibleCount = 0;
				for (int i = 0, c = lChildren.length; i < c; i++) {
					// only for visible components
					if (lChildren[i].isVisible()) {
						lVisibleCount++;

						// we want to know the maximum preferred width and height
						Dimension componentPreferredSize = lChildren[i].getPreferredSize();
						lMaxWidth = Math.max(lMaxWidth, componentPreferredSize.width);
						lMaxHeight = Math.max(lMaxHeight, componentPreferredSize.height);
					}
				}

				// total max width or height is number of button and the intermitting gaps (being number of buttons - 1)
				int lUsedWidth = (lMaxWidth * lVisibleCount) + (gap * (lVisibleCount - 1));
				int lUsedHeight = (lMaxHeight * lVisibleCount) + (gap * (lVisibleCount - 1));

				// determine the size
				Dimension lDimension = getOrientation() == Orientation.HORIZONTAL
				                     ? new Dimension(lInsets.left + lUsedWidth + lInsets.right, lInsets.top + lMaxHeight + lInsets.bottom)
				                     : new Dimension(lInsets.left + lMaxWidth + lInsets.right, lInsets.top + lUsedHeight + lInsets.bottom);
				                     ;
				return lDimension;
			}

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
