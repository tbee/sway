package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
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
            sTree = STree.of(amsterdam)
                    .name("tree")
                    .render(new CityFormat(cities))
                    .childrenOf(City::getPartnerCities)
                    .selectionMode(STree.SelectionMode.MULTIPLE);
            return TestUtil.inSFrame(sTree, focusMeComponent());
        });

        // WHEN
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam");
        // THEN
        Assertions.assertEquals(1, sTree.getSelection().size());
        Assertions.assertEquals(amsterdam, sTree.getSelection().get(0));

        // WHEN
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam/Rome");
        // THEN
        Assertions.assertEquals(1, sTree.getSelection().size());
        Assertions.assertEquals(rome, sTree.getSelection().get(0));

        // WHEN control select
        frameFixture.pressKey(KeyEvent.VK_CONTROL);
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam");
        frameFixture.releaseKey(KeyEvent.VK_CONTROL);
        // THEN
        Assertions.assertEquals(2, sTree.getSelection().size());
        Assertions.assertTrue(sTree.getSelection().contains(amsterdam));
        Assertions.assertTrue(sTree.getSelection().contains(rome));
    }

    @Test
    public void multipleTypes() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        Street kalverstraat = amsterdam.addStreet(Street.of("Kalverstraat"));
        kalverstraat.addBuilding(Building.of(1));
        kalverstraat.addBuilding(Building.of(3));
        kalverstraat.addBuilding(Building.of(5));
        Street leidseplein = amsterdam.addStreet(Street.of("Leidseplein"));
        leidseplein.addBuilding(Building.of(2));
        leidseplein.addBuilding(Building.of(4));
        leidseplein.addBuilding(Building.of(8));

        City berlin = City.of("Berlin", 560);
        berlin.addStreet(Street.of("Kurfurstendam"));

        City rome = City.of("Rome", 1560);
        rome.addStreet(Street.of("Colloseo"));

        City paris = City.of("Paris", 575);

        var cities = List.of(amsterdam, berlin, rome, paris);

        construct(() -> {
            sTree = STree.of(cities)
                    .name("tree")
                    .childrenOf(City.class, City::getStreets)
                    .childrenOf(Street.class, Street::getBuildings)
                    .registerFormat(City.class, new CityFormat())
                    .registerFormat(Street.class, new StreetFormat())
                    .registerFormat(Building.class, new BuildingFormat())
                    .monitorBeans(true);
            return TestUtil.inSFrame(sTree, focusMeComponent());
        });

        // WHEN
        frameFixture.tree("tree.sTreeCore").doubleClickPath("Amsterdam");
        // THEN
        Assertions.assertEquals(1, sTree.getSelection().size());
        Assertions.assertEquals(amsterdam, sTree.getSelection().get(0));

        // WHEN
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam/Leidseplein");
        // THEN
        Assertions.assertEquals(1, sTree.getSelection().size());
        Assertions.assertEquals(leidseplein, sTree.getSelection().get(0));

        // WHEN shift select
        frameFixture.pressKey(KeyEvent.VK_SHIFT);
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam");
        frameFixture.releaseKey(KeyEvent.VK_SHIFT);
        // THEN
        Assertions.assertEquals(3, sTree.getSelection().size());
        Assertions.assertTrue(sTree.getSelection().contains(amsterdam));
        Assertions.assertTrue(sTree.getSelection().contains(kalverstraat));
        Assertions.assertTrue(sTree.getSelection().contains(leidseplein));

        // WHEN
        leidseplein.setName("Leidscheplein");
        SwingUtilities.invokeAndWait(() -> leidseplein.getName()); // dummy code, but this is scheduled after the invokeLater in STree, so nicely waits
        // THEN view is updated
        Assertions.assertEquals("Leidscheplein", frameFixture.tree("tree.sTreeCore").valueAt("Amsterdam/Leidscheplein"));
    }


    @Test
    public void selectionModeCyclic() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City rome = City.of("Rome", 1560);
        City paris = City.of("Paris", 575);
        amsterdam.addPartnerCity(berlin);
        berlin.addPartnerCity(rome);
        rome.addPartnerCity(amsterdam);
        var cities = List.of(amsterdam, berlin, rome, paris);
        construct(() -> {
            sTree = new STree<City>()
                    .name("tree")
                    .render(new CityFormat(cities))
                    .root(amsterdam)
                    .childrenOf(City::getPartnerCities)
                    .selectionMode(STree.SelectionMode.MULTIPLE);
            return TestUtil.inSFrame(sTree, focusMeComponent());
        });

        // WHEN
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam");
        // THEN
        Assertions.assertEquals(1, sTree.getSelection().size());
        Assertions.assertEquals(amsterdam, sTree.getSelection().get(0));

        // WHEN control select
        frameFixture.pressKey(KeyEvent.VK_CONTROL);
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam/Berlin");
        frameFixture.releaseKey(KeyEvent.VK_CONTROL);
        // THEN
        Assertions.assertEquals(2, sTree.getSelection().size());
        Assertions.assertTrue(sTree.getSelection().contains(amsterdam));
        Assertions.assertTrue(sTree.getSelection().contains(berlin));

        // WHEN control select
        frameFixture.pressKey(KeyEvent.VK_CONTROL);
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam/Berlin/Rome");
        frameFixture.releaseKey(KeyEvent.VK_CONTROL);
        // THEN
        Assertions.assertEquals(3, sTree.getSelection().size());
        Assertions.assertTrue(sTree.getSelection().contains(amsterdam));
        Assertions.assertTrue(sTree.getSelection().contains(berlin));
        Assertions.assertTrue(sTree.getSelection().contains(rome));

        // WHEN control select
        frameFixture.pressKey(KeyEvent.VK_CONTROL);
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam/Berlin/Rome/Amsterdam");
        frameFixture.releaseKey(KeyEvent.VK_CONTROL);
        // THEN
        Assertions.assertEquals(4, sTree.getSelection().size());
        Assertions.assertTrue(sTree.getSelection().contains(amsterdam));
        Assertions.assertTrue(sTree.getSelection().contains(berlin));
        Assertions.assertTrue(sTree.getSelection().contains(rome));
        Assertions.assertEquals(2, sTree.getSelection().stream().filter(c -> c.equals(amsterdam)).count());
    }
}
