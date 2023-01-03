package org.tbee.sway;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SListTest extends TestBase {

    @Test
    public void happyBasicTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        List<City> cities = List.of(amsterdam, berlin);
        var ref = new AtomicReference<SList<City>>();
        construct(() -> {
            var sList = new SList<City>() //
                    .data(cities);
            ref.set(sList);
            return TestUtil.inJFrame(sList, focusMeComponent());
        });
        var sList = ref.get();
        sleep(3000);
    }
}
