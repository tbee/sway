package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;

public class SButtonGroupTest extends TestBase {
    private SButtonGroup<Integer> sButtonGroup;

    @Test
    public void happyJToggleButtonTest() throws Exception {

        // GIVEN
        construct(() -> {
            var button1 = new SToggleButton("1").name("b1");
            var button2 = new SToggleButton("2").name("b2");
            var button3 = new SToggleButton("3").name("b3");
            sButtonGroup = new SButtonGroup<Integer>() //
                    .add(button1, 1) //
                    .add(button2, 2) //
                    .add(button3, 3);
            return TestUtil.inJFrame(button1, button2, button3);
        });

        // THEN
        Assertions.assertEquals(null, sButtonGroup.getValue());

        // WHEN click on 2
        frameFixture.toggleButton("b2").click();
        // THEN
        Assertions.assertEquals(2, sButtonGroup.getValue());

        // WHEN click on 3
        frameFixture.toggleButton("b3").click();
        // THEN
        Assertions.assertEquals(3, sButtonGroup.getValue());

        // WHEN click on 3 again
        frameFixture.toggleButton("b3").click();
        // THEN
        Assertions.assertEquals(3, sButtonGroup.getValue());

        // WHEN change value programmatically to 1
        SwingUtilities.invokeAndWait(() -> sButtonGroup.setValue(1));
        // THEN
        Assertions.assertEquals(1, sButtonGroup.getValue());
    }

    @Test
    public void happyBindingTest() throws Exception {

        // GIVEN
        City city = new City().distance(1);
        construct(() -> {
            var button1 = new SToggleButton("1").name("b1");
            var button2 = new SToggleButton("2").name("b2");
            var button3 = new SToggleButton("3").name("b3");
            sButtonGroup = new SButtonGroup<Integer>() //
                    .add(button1, 1) //
                    .add(button2, 2) //
                    .add(button3, 3) //
                    .bind(city, City.DISTANCE);
            return TestUtil.inJFrame(button1, button2, button3);
        });

        // THEN there was an initial sync upon binding
        Assertions.assertEquals(1, city.getDistance());

        // WHEN click on 2
        frameFixture.toggleButton("b2").click();
        // THEN
        Assertions.assertEquals(2, city.getDistance());

        // WHEN change value programmatically to 3
        SwingUtilities.invokeAndWait(() -> city.distance(3));
        // THEN
        Assertions.assertEquals(3, sButtonGroup.getValue());
    }
}
