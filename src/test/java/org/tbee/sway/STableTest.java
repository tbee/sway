package org.tbee.sway;

import org.assertj.swing.core.MouseButton;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.fixture.JTableFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.sway.format.FormatRegistry;

import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.List;

public class STableTest extends TestBase {

    // https://joel-costigliola.github.io/assertj/assertj-swing-input.html
    // https://joel-costigliola.github.io/assertj/assertj-swing-advanced.html#custom-editors

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
                    .column(Integer.class).valueSupplier(d -> d.getDistance()).table() //
                    .data(data);
            return TestUtil.inJFrame(sTable, focusMeComponent());
        });


        // WHEN
        frameFixture.table("table.sTableCore").enterValue(TableCell.row(0).column(0), "Rotterdam");
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
                    .columns(City.class, City.NAME, City.DISTANCE) //
                    .data(data);
            return TestUtil.inJFrame(sTable, focusMeComponent());
        });

        // WHEN
        frameFixture.table("table.sTableCore").enterValue(TableCell.row(0).column(0), "Rotterdam");
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
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE)
                    .data(data);
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

    @Test
    public void happyEditorTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        City rome = new City("Rome", 1560);
        City paris = new City("Paris", 575);
        amsterdam.sisterCity(berlin);
        List<City> data = List.of(amsterdam, berlin, rome, paris);

        CityFormat cityFormat = new CityFormat(data);

        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.SISTERCITY) //
                    .<City>findColumnById(City.SISTERCITY).renderer(cityFormat).editor(cityFormat).table() //
                    .data(data);
            return TestUtil.inJFrame(sTable, focusMeComponent());
        });

        // WHEN
        frameFixture.table("table.sTableCore").enterValue(TableCell.row(0).column(1), "Rome");
        moveFocus();

        // THEN
        Assertions.assertEquals(rome, sTable.sTable().getValueAt(0, 1));
    }

    @Test
    public void happyEditorViaFormatRegistryTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        City rome = new City("Rome", 1560);
        City paris = new City("Paris", 575);
        amsterdam.sisterCity(berlin);
        List<City> data = List.of(amsterdam, berlin, rome, paris);

        FormatRegistry.register(City.class, new CityFormat(data));
        try {
            construct(() -> {
                sTable = new STable<City>() //
                        .name("table") //
                        .columns(City.class, City.NAME, City.SISTERCITY) //
                        .<City>findColumnById(City.SISTERCITY).table() //
                        .data(data);
                return TestUtil.inJFrame(sTable, focusMeComponent());
            });

            // WHEN
            frameFixture.table("table.sTableCore").enterValue(TableCell.row(0).column(1), "Rome");
            moveFocus();

            // THEN
            Assertions.assertEquals(rome, sTable.sTable().getValueAt(0, 1));
        }
        finally {
            Assertions.assertTrue(FormatRegistry.unregister(City.class));
        }
    }

    @Test
    public void happySortTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        City bredevoort = new City("Bredevoort", 5);
        City paris = new City("Paris", 575);
        City rome = new City("Rome", 1560);
        amsterdam.sisterCity(berlin);
        bredevoort.sisterCity(rome);
        List<City> data = List.of(berlin, bredevoort, amsterdam, rome, paris);

        CityFormat cityFormat = new CityFormat(data);

        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE, City.SISTERCITY) //
                    .<City>findColumnById(City.SISTERCITY).renderer(cityFormat).editor(cityFormat).sorting(Comparator.comparing(City::getName)).table() // Sort on name
                    .column(String.class).title("Name sort 2nd").valueSupplier(City::getName).sorting(Comparator.comparing(o -> o.substring(1))).table() // sort starting on the 2nd letter of the name
                    .data(data);
            return TestUtil.inJFrame(sTable, focusMeComponent());
        });

        // WHEN Sort on name
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(0);
        // THEN
        Assertions.assertEquals(amsterdam.getName(), sTable.sTable().getValueAt(0, 0)); // first row
        Assertions.assertEquals(rome.getName(), sTable.sTable().getValueAt(4, 0)); // last row

        // WHEN Sort on distance
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(1);
        // THEN
        Assertions.assertEquals(bredevoort.getName(), sTable.sTable().getValueAt(0, 0)); // first row
        Assertions.assertEquals(rome.getName(), sTable.sTable().getValueAt(4, 0)); // last row

        // WHEN Sort on sisterCity
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(2);

        // THEN
        Assertions.assertEquals(berlin.getName(), sTable.sTable().getValueAt(0, 0)); // first row
        Assertions.assertEquals(bredevoort.getName(), sTable.sTable().getValueAt(4, 0)); // last row

        // WHEN Sort on 2nd letter of name
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(3);
        // THEN
        Assertions.assertEquals(paris.getName(), sTable.sTable().getValueAt(0, 0)); // first row
        Assertions.assertEquals(bredevoort.getName(), sTable.sTable().getValueAt(4, 0)); // last row

        // WHEN Sort on 2nd letter of name DESC
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(3);
        // THEN
        Assertions.assertEquals(bredevoort.getName(), sTable.sTable().getValueAt(0, 0)); // first row
        Assertions.assertEquals(paris.getName(), sTable.sTable().getValueAt(4, 0)); // last row
    }

    @Test
    public void selectionWhileSortedSingleTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        City bredevoort = new City("Bredevoort", 5);
        City paris = new City("Paris", 575);
        City rome = new City("Rome", 1560);
        amsterdam.sisterCity(berlin);
        bredevoort.sisterCity(rome);
        List<City> data = List.of(berlin, bredevoort, amsterdam, rome, paris);

        CityFormat cityFormat = new CityFormat(data);

        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE, City.SISTERCITY) //
                    .<City>findColumnById(City.SISTERCITY).renderer(cityFormat).editor(cityFormat).sorting(Comparator.comparing(City::getName)).table() // Sort on name
                    .selectionMode(STable.SelectionMode.SINGLE)
                    .data(data);
            return TestUtil.inJFrame(sTable, focusMeComponent());
        });
        JTableFixture tableFixture = frameFixture.table("table.sTableCore");
        List<City> selection;

        // WHEN
        tableFixture.tableHeader().clickColumn(0); // Sort on name
        tableFixture.click(TableCell.row(1).column(1), MouseButton.LEFT_BUTTON); // Select 2nd row
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(berlin, selection.get(0));

        // WHEN
        tableFixture.click(TableCell.row(2).column(0), MouseButton.LEFT_BUTTON); // Select 3rd row
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(bredevoort, selection.get(0));

        // WHEN
        tableFixture.tableHeader().clickColumn(2); // Sort on sister city
        tableFixture.click(TableCell.row(4).column(1), MouseButton.LEFT_BUTTON); // Select last row
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(bredevoort, selection.get(0));
    }


    @Test
    public void selectionWhileSortedMultipleTest() throws Exception {

        // GIVEN
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        City bredevoort = new City("Bredevoort", 5);
        City paris = new City("Paris", 575);
        City rome = new City("Rome", 1560);
        amsterdam.sisterCity(berlin);
        bredevoort.sisterCity(rome);
        List<City> data = List.of(berlin, bredevoort, amsterdam, rome, paris);

        CityFormat cityFormat = new CityFormat(data);

        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE, City.SISTERCITY) //
                    .<City>findColumnById(City.SISTERCITY).renderer(cityFormat).editor(cityFormat).sorting(Comparator.comparing(City::getName)).table() // Sort on name
                    .selectionMode(STable.SelectionMode.MULTIPLE)
                    .data(data);
            return TestUtil.inJFrame(sTable, focusMeComponent());
        });
        JTableFixture tableFixture = frameFixture.table("table.sTableCore");
        List<City> selection;

        // WHEN
        tableFixture.tableHeader().clickColumn(0); // Sort on name
        tableFixture.click(TableCell.row(1).column(1), MouseButton.LEFT_BUTTON); // Select 2nd row
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(berlin, selection.get(0));

        // WHEN
        tableFixture.pressKey(KeyEvent.VK_CONTROL);
        tableFixture.click(TableCell.row(3).column(0), MouseButton.LEFT_BUTTON); // ALSO Select 4th row
        tableFixture.releaseKey(KeyEvent.VK_CONTROL);
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(2, selection.size());
        Assertions.assertEquals(berlin, selection.get(0));
        Assertions.assertEquals(paris, selection.get(1));

        // WHEN
        tableFixture.click(TableCell.row(4).column(1), MouseButton.LEFT_BUTTON); // Select last row
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(rome, selection.get(0));
    }
}
