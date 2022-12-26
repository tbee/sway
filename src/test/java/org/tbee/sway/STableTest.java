package org.tbee.sway;

import org.assertj.swing.data.TableCell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.List;

public class STableTest extends TestBase {

    private STable sTable;

    @Test
    public void happyStringBindingTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        List<City> data = List.of(amsterdam, berlin);
        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .column(String.class).valueSupplier(City::getName).valueConsumer(City::setName).table()
                    .column(Integer.class).valueSupplier(d -> d.getDistance()).table();

            sTable.data(data);

            return TestUtil.inJFrame(sTable, focusMeComponent());
        });

        // WHEN
        frameFixture.table("table.sTable").enterValue(TableCell.row(0).column(0), "Rotterdam");
        moveFocus();

        // THEN
        Assertions.assertEquals("Rotterdam", sTable.sTable().getValueAt(0, 0));
        Assertions.assertEquals("Rotterdam", amsterdam.getName());
        Assertions.assertEquals("Berlin", berlin.getName());
    }

    @Test
    public void happyStringPropertyTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        List<City> data = List.of(amsterdam, berlin);
        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE);

            sTable.data(data);

            return TestUtil.inJFrame(sTable, focusMeComponent());
        });

        // WHEN
        frameFixture.table("table.sTable").enterValue(TableCell.row(0).column(0), "Rotterdam");
        moveFocus();

        // THEN
        Assertions.assertEquals("Rotterdam", sTable.sTable().getValueAt(0, 0));
        Assertions.assertEquals("Rotterdam", amsterdam.getName());
        Assertions.assertEquals("Berlin", berlin.getName());
    }

    @Test
    public void happyStringMonitorTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        List<City> data = List.of(amsterdam, berlin);
        construct(() -> {
            sTable = new STable() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE);

            sTable.data(data);

            return TestUtil.inJFrame(sTable, focusMeComponent());
        });

        // WHEN
        SwingUtilities.invokeAndWait(() -> {
            amsterdam.setName("Rome");
        });

        // THEN
        Assertions.assertEquals("Rome", sTable.sTable().getValueAt(0, 0));
        Assertions.assertEquals("Berlin", berlin.getName());
    }

    // test per column editor/renderer
}
