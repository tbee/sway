package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.util.List;

public class STreeTest extends TestBase {

    private STree sTree;

    @Test
    public void selectionModeMultiple() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City rome = City.of("Rome", 1560);
        City paris = City.of("Paris", 575);
        amsterdam.addPartnerCity(berlin);
        amsterdam.addPartnerCity(rome);
        rome.addPartnerCity(paris);
        var cities = List.of(amsterdam, berlin, rome, paris);
        construct(() -> {
            sTree = new STree<City>()
                    .name("tree")
                    .render(new CityFormat(cities))
                    .root(amsterdam)
                    .childrenOf(City::getPartnerCities)
                    .selectionMode(STree.SelectionMode.MULTIPLE);
            return TestUtil.inJFrame(sTree, focusMeComponent());
        });

        // WHEN
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam");
        // THEN
        Assertions.assertEquals(1, sTree.getSelection().size());
        Assertions.assertEquals("Amsterdam", ((City)sTree.getSelection().get(0)).getName());

        // WHEN
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam/Berlin");
        // THEN
        Assertions.assertEquals(1, sTree.getSelection().size());
        Assertions.assertEquals("Berlin", ((City)sTree.getSelection().get(0)).getName());

        // WHEN shift select
        frameFixture.pressKey(KeyEvent.VK_SHIFT);
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam");
        frameFixture.releaseKey(KeyEvent.VK_SHIFT);
        // THEN
        Assertions.assertEquals(2, sTree.getSelection().size());
        Assertions.assertTrue(sTree.getSelection().contains(amsterdam));
        Assertions.assertTrue(sTree.getSelection().contains(berlin));
    }
}
