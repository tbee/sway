package org.tbee.sway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.sway.support.DebugUtil;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SListTest extends TestBase {

    @Test
    public void happyTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        List<City> cities = List.of(amsterdam, berlin);
        var ref = new AtomicReference<SList<City>>();
        construct(() -> {
            var sList = new SList<City>() //
                    .render(new CityFormat(cities)) //
                    .data(cities);
            ref.set(sList);
            return TestUtil.inJFrame(sList, focusMeComponent());
        });
        var sList = ref.get();

        System.out.println(DebugUtil.componentTreeAsString(sList));
        // Not sure how to validate the actual component
    }

    @Test
    public void happySelectionSingleTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        City bredevoort = new City("Bredevoort", 5);
        City paris = new City("Paris", 575);
        City rome = new City("Rome", 1560);
        List<City> cities = List.of(berlin, bredevoort, amsterdam, rome, paris);

        var ref = new AtomicReference<SList<City>>();
        construct(() -> {
            var sList = new SList<City>() //
                    .name("sList") //
                    .render(new CityFormat(cities)) //
                    .selectionMode(SList.SelectionMode.SINGLE) //
                    .data(cities);
            ref.set(sList);
            return TestUtil.inJFrame(sList);
        });
        var sList = ref.get();

        // WHEN
        frameFixture.list("sList.sListCore").selectItem(1);
        // THEN
        Assertions.assertEquals(1, sList.getSelection().size());
        Assertions.assertEquals(bredevoort, sList.getSelection().get(0));

        // WHEN
        frameFixture.list("sList.sListCore").selectItem(4);
        // THEN
        Assertions.assertEquals(1, sList.getSelection().size());
        Assertions.assertEquals(paris, sList.getSelection().get(0));

        // WHEN
        SwingUtilities.invokeAndWait(() -> sList.setSelection(List.of(rome, amsterdam, paris)));
        // THEN
        Assertions.assertEquals(1, sList.getSelection().size());
        Assertions.assertEquals(paris, sList.getSelection().get(0)); // Last one is selected
    }
}
