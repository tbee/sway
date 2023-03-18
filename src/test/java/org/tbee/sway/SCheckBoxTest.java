package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SCheckBoxTest extends TestBase {

    SCheckBox sCheckBox;

    @Test
    public void happyTest() throws Exception {

        // GIVEN
        City city = City.of("Amsterdam", 150);
        construct(() -> {
            sCheckBox = new SCheckBox() //
                    .bindTo(city.growing$()) //
                    .name("sCheckBox");
            return TestUtil.inJFrame(sCheckBox, focusMeComponent());
        });
        Assertions.assertEquals(true, sCheckBox.isSelected());
        Assertions.assertEquals(true, city.getGrowing());

        // WHEN
        frameFixture.checkBox("sCheckBox").click();
        // THEN
        Assertions.assertEquals(false, sCheckBox.isSelected());
        Assertions.assertEquals(false, city.getGrowing());

        // WHEN
        frameFixture.checkBox("sCheckBox").click();
        // THEN
        Assertions.assertEquals(true, sCheckBox.isSelected());
        Assertions.assertEquals(true, city.getGrowing());
    }
}
