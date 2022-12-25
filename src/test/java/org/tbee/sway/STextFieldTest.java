package org.tbee.sway;

import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;

public class STextFieldTest extends TestBase {
    private STextField sTextField;

    @Test
    public void integerHappyTest() throws Exception {

        // GIVEN
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField");
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("123");
        moveFocus();

        // THEN
        Assertions.assertEquals(123, sTextField.getValue());
    }

    @Test
    public void integerFormatErrorTest() throws Exception {

        // GIVEN
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField");
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("abc");
        moveFocus();

        // THEN
        JOptionPaneFixture optionPaneFixture = JOptionPaneFinder.findOptionPane().using(frameFixture.robot());
        optionPaneFixture.requireErrorMessage().requireMessage("For input string: \"abc\"");
        optionPaneFixture.okButton().click();
        Assertions.assertTrue(sTextField.hasFocus());
        Assertions.assertEquals("abc", sTextField.getText());
        Assertions.assertEquals(null, sTextField.getValue());
    }


    @Test
    public void stringBindHappyTest() throws Exception {

        // GIVEN
        final Bean1 bean1 = new Bean1();
        construct(() -> {
            sTextField = STextField.ofString().name("sTextField").bind(bean1, Bean1.NAME);
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("abc");
        moveFocus();

        // THEN
        Assertions.assertEquals("abc", bean1.getName());

        // WHEN
        SwingUtilities.invokeAndWait(() -> {
            bean1.setName("def");
        });

        // THEN
        Assertions.assertEquals("def", sTextField.getText());
        Assertions.assertEquals("def", sTextField.getValue());
    }

    @Test
    public void integerBindHappyTest() throws Exception {

        // GIVEN
        final Bean1 bean1 = new Bean1();
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField").bind(bean1, Bean1.DISTANCE);
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("123");
        moveFocus();

        // THEN
        Assertions.assertEquals(123, bean1.getDistance());

        // WHEN
        SwingUtilities.invokeAndWait(() -> {
            bean1.setDistance(456);
        });

        // THEN
        Assertions.assertEquals("456", sTextField.getText());
        Assertions.assertEquals( 456, sTextField.getValue());
    }

    @Test
    public void integerBindSetterFailureTest() throws Exception {

        // GIVEN
        final Bean1 bean1 = new Bean1();
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField").bind(bean1, Bean1.DISTANCE);
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").deleteText().enterText("-12");
        moveFocus();

        // THEN
        JOptionPaneFixture optionPaneFixture = JOptionPaneFinder.findOptionPane().using(frameFixture.robot());
        optionPaneFixture.requireErrorMessage().requireMessage("Age must be >= 0");
        optionPaneFixture.okButton().click();
        Assertions.assertTrue(sTextField.hasFocus());
        Assertions.assertEquals("0", sTextField.getText());
        Assertions.assertEquals(0, sTextField.getValue());
    }

    // TBEERNOT TODO BeanBinder tests
}
