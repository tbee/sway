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
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        List<City> cities = List.of(amsterdam, berlin);
        var ref = new AtomicReference<SList<City>>();
        construct(() -> {
            var sList = new SList<City>() //
                    .render(new CityFormat(cities)) //
                    .items(cities);
            ref.set(sList);
            return TestUtil.inSFrame(sList, focusMeComponent());
        });
        var sList = ref.get();

        System.out.println(DebugUtil.componentTreeAsString(sList));
        // Not sure how to validate the actual component
    }

    @Test
    public void happySelectionSingleTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> cities = List.of(berlin, bredevoort, amsterdam, rome, paris);

        var ref = new AtomicReference<SList<City>>();
        construct(() -> {
            var sList = new SList<City>() //
                    .name("sList") //
                    .render(new CityFormat(cities)) //
                    .selectionMode(SList.SelectionMode.SINGLE) //
                    .items(cities);
            ref.set(sList);
            return TestUtil.inSFrame(sList);
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
