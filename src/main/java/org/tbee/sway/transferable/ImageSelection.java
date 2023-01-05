/*
 * Copyright: (c) TBEE.ORG
 * Version:   $Revision: 1.1 $
 * Modified:  $Date: 2007/10/23 15:00:39 $
 * By:        $Author: toeukpap $
 */
package org.tbee.sway.transferable;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class ImageSelection implements Transferable {
    public ImageSelection() {
    }

    public ImageSelection(Image i) {
        image = i;
    }

    public void setImage(Image value) {
        image = value;
    }

    public Image getImage() {
        return image;
    }

    private Image image;

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(DataFlavor.imageFlavor)) {
            return image;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(DataFlavor.imageFlavor)) {
            return true;
        }
        return false;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }
}
