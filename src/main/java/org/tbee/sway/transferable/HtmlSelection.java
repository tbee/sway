/*
 * Copyright: (c) TBEE.ORG
 * Version:   $Revision: 1.2 $
 * Modified:  $Date: 2007/10/22 08:40:59 $
 * By:        $Author: toeukpap $
 */
package org.tbee.sway.transferable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class HtmlSelection implements Transferable {
    private static ArrayList flavors = new ArrayList();

    static {
        try {
            flavors.add(new DataFlavor("text/html;class=java.lang.String"));
            flavors.add(new DataFlavor("text/html;class=java.io.Reader"));
            flavors.add(new DataFlavor("text/html;charset=unicode;class=java.io.InputStream"));
        }
		catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private String html;

    public HtmlSelection(String html) {
        this.html = html;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return (DataFlavor[]) flavors.toArray(new DataFlavor[flavors.size()]);
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavors.contains(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (String.class.equals(flavor.getRepresentationClass())) {
            return html;
        }
		else if (Reader.class.equals(flavor.getRepresentationClass())) {
            return new StringReader(html);
        }
		else if (InputStream.class.equals(flavor.getRepresentationClass())) {
            return new StringBufferInputStream(html);
        }
        throw new UnsupportedFlavorException(flavor);
    }
}