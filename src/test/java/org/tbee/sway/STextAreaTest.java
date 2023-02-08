package org.tbee.sway;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class STextAreaTest extends TestBase {

    private STextArea sTextArea;

    final static private ClipboardOwner dummyClipboardOwner = new ClipboardOwner() {
        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }
    };

    @Test
    public void simpleTest() throws Exception {

        // GIVEN
        construct(() -> {
        	sTextArea = new STextArea().name("sTextArea");
            return TestUtil.inJFrame(sTextArea, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextArea.jTextArea").enterText("123");
        moveFocus();

        // THEN
        Assertions.assertEquals("123", sTextArea.getText());
    }


    @Test
    public void bindHappyTest() throws Exception {

        // GIVEN
        final City city = new City();
        construct(() -> {
        	sTextArea = new STextArea().name("sTextArea").bind(city, City.NAME);
            return TestUtil.inJFrame(sTextArea, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextArea.jTextArea").enterText("abc");
        moveFocus();

        // THEN
        Assertions.assertEquals("abc", city.getName());

        // WHEN
        SwingUtilities.invokeAndWait(() -> city.setName("def"));

        // THEN
        Assertions.assertEquals("def", sTextArea.getText());
    }
}
