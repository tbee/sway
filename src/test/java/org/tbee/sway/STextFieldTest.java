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
        final City city = new City();
        construct(() -> {
            sTextField = STextField.ofString().name("sTextField").bind(city, City.NAME);
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN
        frameFixture.textBox("sTextField").enterText("abc");
        moveFocus();

        // THEN
        Assertions.assertEquals("abc", city.getName());

        // WHEN
        SwingUtilities.invokeAndWait(() -> {
            city.setName("def");
        });

        // THEN
        Assertions.assertEquals("def", sTextField.getText());
        Assertions.assertEquals("def", sTextField.getValue());
    }

    @Test
    public void integerBindHappyTest() throws Exception {

        // GIVEN
        final City city = new City();
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField").bind(city, City.DISTANCE);
            return TestUtil.inJFrame(sTextField, focusMeComponent());
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
        final City city = new City();
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField").bind(city, City.DISTANCE);
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

    @Test
    public void integerUnbindHappyTest() throws Exception {

        // GIVEN
        final City city = new City().distance(456);
        construct(() -> {
            sTextField = STextField.ofInteger().name("sTextField");
            return TestUtil.inJFrame(sTextField, focusMeComponent());
        });

        // WHEN bind and unbind
        SwingUtilities.invokeAndWait(() -> sTextField.binding(city, City.DISTANCE).unbind());
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
