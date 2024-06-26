package org.tbee.sway;

import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class STextFieldTest extends TestBase {

    private STextField sTextField;

    final static private ClipboardOwner dummyClipboardOwner = new ClipboardOwner() {
        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }
    };

    @Test
    public void integerHappyTest() throws Exception {

        // GIVEN
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField");
            return TestUtil.inSFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("123");
        moveFocus();

        // THEN
        Assertions.assertEquals(123, sTextField.getValue());
    }


    @Test
    public void integerPasteTest() throws Exception {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // GIVEN
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField");
            return TestUtil.inSFrame(sTextField, focusMeComponent());
        });

        // WHEN
        clipboard.setContents(new StringSelection("123"), dummyClipboardOwner);
        SwingUtilities.invokeAndWait(() -> {
            sTextField.requestFocus();
            sTextField.selectAll();
            sTextField.paste();
        });
        moveFocus();
        // THEN
        Assertions.assertEquals(123, sTextField.getValue());

        // WHEN
        clipboard.setContents(new StringSelection("abc"), dummyClipboardOwner);
        SwingUtilities.invokeAndWait(() -> {
            sTextField.requestFocus();
            sTextField.selectAll();
            sTextField.paste();
        });
        moveFocus();
        // THEN
        JOptionPaneFixture optionPaneFixture = JOptionPaneFinder.findOptionPane().using(frameFixture.robot());
        optionPaneFixture.requireErrorMessage().requireMessage("Unparseable number: \"abc\"");
        optionPaneFixture.okButton().click();
        Assertions.assertTrue(sTextField.hasFocus());
        Assertions.assertEquals("abc", sTextField.getText());
        Assertions.assertEquals(123, sTextField.getValue());
    }

    @Test
    public void integerFormatErrorTest() throws Exception {

        // GIVEN
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField");
            return TestUtil.inSFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("abc");
        moveFocus();

        // THEN
        JOptionPaneFixture optionPaneFixture = JOptionPaneFinder.findOptionPane().using(frameFixture.robot());
        optionPaneFixture.requireErrorMessage().requireMessage("Unparseable number: \"abc\"");
        optionPaneFixture.okButton().click();
        Assertions.assertTrue(sTextField.hasFocus());
        Assertions.assertEquals("abc", sTextField.getText());
        Assertions.assertEquals(null, sTextField.getValue());
    }


    @Test
    public void stringBindHappyTest() throws Exception {

        // GIVEN
        final City city = City.of();
        construct(() -> {
            sTextField = STextField.ofString().name("sTextField").bindTo(city.name$());
            return TestUtil.inSFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("abc");
        moveFocus();

        // THEN
        Assertions.assertEquals("abc", city.getName());

        // WHEN
        SwingUtilities.invokeAndWait(() -> city.setName("def"));

        // THEN
        Assertions.assertEquals("def", sTextField.getText());
        Assertions.assertEquals("def", sTextField.getValue());
    }

    @Test
    public void integerBindHappyTest() throws Exception {

        // GIVEN
        final City city = City.of();
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField").bindTo(city.distance$());
            return TestUtil.inSFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("123");
        moveFocus();

        // THEN
        Assertions.assertEquals(123, city.getDistance());

        // WHEN
        SwingUtilities.invokeAndWait(() -> {
            city.setDistance(456);
        });

        // THEN
        Assertions.assertEquals("456", sTextField.getText());
        Assertions.assertEquals( 456, sTextField.getValue());
    }

    @Test
    public void integerBindSetterFailureTest() throws Exception {

        // GIVEN
        final City city = City.of();
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField").bindTo(city.distance$());
            return TestUtil.inSFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").deleteText().enterText("-12");
        moveFocus();

        // THEN
        JOptionPaneFixture optionPaneFixture = JOptionPaneFinder.findOptionPane().using(frameFixture.robot());
        optionPaneFixture.requireErrorMessage().requireMessage("Distance must be >= 0");
        optionPaneFixture.okButton().click();
        Assertions.assertTrue(sTextField.hasFocus());
        Assertions.assertEquals("0", sTextField.getText());
        Assertions.assertEquals(0, sTextField.getValue());
    }

    @Test
    public void integerUnbindHappyTest() throws Exception {

        // GIVEN
        final City city = City.of().distance(456);
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField");
            return TestUtil.inSFrame(sTextField, focusMeComponent());
        });

        // WHEN bind and unbind
        SwingUtilities.invokeAndWait(() -> sTextField.value$().bindTo(city.distance$()).unbind());
        // THEN there was a sync when binding, so textfield was changed
        Assertions.assertEquals("456", sTextField.getText());
        Assertions.assertEquals( 456, city.getDistance());

        // WHEN change the textfield
        frameFixture.textBox("sTextField").selectAll().enterText("123");
        moveFocus();
        // THEN bean's value should be unchanged
        Assertions.assertEquals("123", sTextField.getText());
        Assertions.assertEquals( 456, city.getDistance());

        // WHEN change the bean
        SwingUtilities.invokeAndWait(() -> city.setDistance(789));
        // THEN textfield's value should be unchanged
        Assertions.assertEquals("123", sTextField.getText());
        Assertions.assertEquals( 789, city.getDistance());
    }
}
