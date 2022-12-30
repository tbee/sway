package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;

public class SLabelTest extends TestBase {

    private SLabel sLabel;

    @Test
    public void textBindHappyTest() throws Exception {

        // GIVEN
        final City city = new City().name("name");
        construct(() -> {
            sLabel = new SLabel("initial").bindText(city, City.NAME);
            return TestUtil.inJFrame(sLabel, focusMeComponent());
        });

        // WHEN
        SwingUtilities.invokeAndWait(() -> {
            city.setName("def");
        });

        // THEN
        Assertions.assertEquals("def", sLabel.getText());
    }

    @Test
    public void textUnbindHappyTest() throws Exception {

        // GIVEN
        final City city = new City().name("name");
        construct(() -> {
            sLabel = new SLabel("initial");
            return TestUtil.inJFrame(sLabel, focusMeComponent());
        });

        // WHEN bind and unbind
        SwingUtilities.invokeAndWait(() -> sLabel.bindingForText(city, City.NAME).unbind());
        // THEN there was a sync when binding, so label was changed
        Assertions.assertEquals("name", sLabel.getText());

        // WHEN changing the bean
        SwingUtilities.invokeAndWait(() -> city.setName("changed"));
        // THEN label's value should be unchanged
        Assertions.assertEquals("name", sLabel.getText());
    }
}
