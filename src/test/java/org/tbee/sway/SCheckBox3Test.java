package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SCheckBox3Test extends TestBase {

    SCheckBox3 sCheckBox;

    @Test
    public void happyTest() throws Exception {

        // GIVEN
        City city = new City("Amsterdam", 150);
        construct(() -> {
            sCheckBox = new SCheckBox3() //
                    .bindTo(city.cityRights$()) //
                    .name("sCheckBox");
            return TestUtil.inJFrame(sCheckBox, focusMeComponent());
        });
        Assertions.assertEquals(null, sCheckBox.getSelected3());
        Assertions.assertEquals(null, city.getCityRights());

        // WHEN
        frameFixture.checkBox("sCheckBox").click();
        // THEN
        Assertions.assertEquals(false, sCheckBox.getSelected3());
        Assertions.assertEquals(false, city.getCityRights());

        // WHEN
        frameFixture.checkBox("sCheckBox").click();
        // THEN
        Assertions.assertEquals(true, sCheckBox.getSelected3());
        Assertions.assertEquals(true, city.getCityRights());

        // WHEN
        frameFixture.checkBox("sCheckBox").click();
        // THEN
        Assertions.assertEquals(null, sCheckBox.getSelected3());
        Assertions.assertEquals(null, city.getCityRights());
    }

    @Test
    public void happyNotAllowUndeterminedTest() throws Exception {

        // GIVEN
        City city = new City("Amsterdam", 150);
        construct(() -> {
            sCheckBox = new SCheckBox3() //
                    .bindTo(city.cityRights$()) //
                    .allowUndetermined(false) //
                    .name("sCheckBox");
            return TestUtil.inJFrame(sCheckBox, focusMeComponent());
        });
        Assertions.assertEquals(false, sCheckBox.getSelected3());
        Assertions.assertEquals(false, city.getCityRights());

        // WHEN
        frameFixture.checkBox("sCheckBox").click();
        // THEN
        Assertions.assertEquals(true, sCheckBox.getSelected3());
        Assertions.assertEquals(true, city.getCityRights());

        // WHEN
        frameFixture.checkBox("sCheckBox").click();
        // THEN
        Assertions.assertEquals(false, sCheckBox.getSelected3());
        Assertions.assertEquals(false, city.getCityRights());
    }
}
