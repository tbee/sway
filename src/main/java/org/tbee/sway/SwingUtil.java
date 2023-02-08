package org.tbee.sway;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;

public class SwingUtil {
    final static public Insets EMPTY_INSETS = new Insets(0,0,0,0);
    
	/**
	 * Center a window to the screen
	 * @param w
	 */
	static public void centerOnScreen(Window w) 
	{
	    Rectangle screen = w.getGraphicsConfiguration().getBounds();
	    w.setLocation(
	        screen.x + (screen.width - w.getWidth()) / 2,
	        screen.y + (screen.height - w.getHeight()) / 2
	    );
	}
}
