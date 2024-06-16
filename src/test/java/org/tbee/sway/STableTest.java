package org.tbee.sway;

import org.assertj.swing.core.MouseButton;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.fixture.JTableFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.List;

public class STableTest extends TestBase {

    // https://joel-costigliola.github.io/assertj/assertj-swing-input.html
    // https://joel-costigliola.github.io/assertj/assertj-swing-advanced.html#custom-editors

    // NOTES:
    // - it is difficult to test TableFilter, so we trust Coderazzi to have done that for us. Does leave the enhancements like the automatic Renderer based on Format.

    private STable sTable;

    @Test
    public void happyStringBindingTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        List<City> data = List.of(amsterdam, berlin);
        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .column(String.class).valueSupplier(City::getName).valueConsumer(City::setName).table()
                    .column(Integer.class).valueSupplier(d -> d.getDistance()).table() //
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
        });


        // WHEN
        frameFixture.table("table.sTableCore").enterValue(TableCell.row(0).column(0), "Rotterdam");
        moveFocus();

        // THEN
        Assertions.assertEquals("Rotterdam", sTable.getSTableCore().getValueAt(0, 0));
        Assertions.assertEquals("Rotterdam", amsterdam.getName());
        Assertions.assertEquals("Berlin", berlin.getName());
    }

    @Test
    public void happyStringPropertyTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        List<City> data = List.of(amsterdam, berlin);
        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE) //
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
        });

        // WHEN
        frameFixture.table("table.sTableCore").enterValue(TableCell.row(0).column(0), "Rotterdam");
        moveFocus();

        // THEN
        Assertions.assertEquals("Rotterdam", sTable.getSTableCore().getValueAt(0, 0));
        Assertions.assertEquals("Rotterdam", amsterdam.getName());
        Assertions.assertEquals("Berlin", berlin.getName());
    }

    @Test
    public void happyStringMonitorTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        List<City> data = List.of(amsterdam, berlin);
        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE)
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
        });

        // WHEN (in a differtent thread!)
        amsterdam.setName("Rome");
        SwingUtilities.invokeAndWait(() -> amsterdam.getName()); // dummy code, but this is scheduled after the invokeLater in STree, so nicely waits

        // THEN
        Assertions.assertEquals("Rome", sTable.getSTableCore().getValueAt(0, 0));
        Assertions.assertEquals("Berlin", berlin.getName());
    }

    @Test
    public void happyEditorTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City rome = City.of("Rome", 1560);
        City paris = City.of("Paris", 575);
        amsterdam.sisterCity(berlin);
        List<City> data = List.of(amsterdam, berlin, rome, paris);

        CityFormat cityFormat = new CityFormat(data);

        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.SISTERCITY) //
                    .<City>findColumnById(City.SISTERCITY).renderer(cityFormat).editor(cityFormat).table() //
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
        });

        // WHEN
        frameFixture.table("table.sTableCore").enterValue(TableCell.row(0).column(1), "Rome");
        moveFocus();

        // THEN
        Assertions.assertEquals(rome, sTable.getSTableCore().getValueAt(0, 1));
    }

    @Test
    public void happyEditorViaFormatRegistryTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City rome = City.of("Rome", 1560);
        City paris = City.of("Paris", 575);
        amsterdam.sisterCity(berlin);
        List<City> data = List.of(amsterdam, berlin, rome, paris);

        SFormatRegistry.register(City.class, new CityFormat(data));
        try {
            construct(() -> {
                sTable = new STable<City>() //
                        .name("table") //
                        .columns(City.class, City.NAME, City.SISTERCITY) //
                        .<City>findColumnById(City.SISTERCITY).table() //
                        .items(data);
                return TestUtil.inSFrame(sTable, focusMeComponent());
            });

            // WHEN
            frameFixture.table("table.sTableCore").enterValue(TableCell.row(0).column(1), "Rome");
            moveFocus();

            // THEN
            Assertions.assertEquals(rome, sTable.getSTableCore().getValueAt(0, 1));
        }
        finally {
            Assertions.assertTrue(SFormatRegistry.unregister(City.class));
        }
    }

    @Test
    public void happySortTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
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
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
        });

        // WHEN Sort on name
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(0);
        // THEN
        Assertions.assertEquals(amsterdam.getName(), sTable.getSTableCore().getValueAt(0, 0)); // first row
        Assertions.assertEquals(rome.getName(), sTable.getSTableCore().getValueAt(4, 0)); // last row

        // WHEN Sort on distance
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(1);
        // THEN
        Assertions.assertEquals(bredevoort.getName(), sTable.getSTableCore().getValueAt(0, 0)); // first row
        Assertions.assertEquals(rome.getName(), sTable.getSTableCore().getValueAt(4, 0)); // last row

        // WHEN Sort on sisterCity
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(2);

        // THEN
        Assertions.assertEquals(berlin.getName(), sTable.getSTableCore().getValueAt(0, 0)); // first row
        Assertions.assertEquals(bredevoort.getName(), sTable.getSTableCore().getValueAt(4, 0)); // last row

        // WHEN Sort on 2nd letter of name
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(3);
        // THEN
        Assertions.assertEquals(paris.getName(), sTable.getSTableCore().getValueAt(0, 0)); // first row
        Assertions.assertEquals(bredevoort.getName(), sTable.getSTableCore().getValueAt(4, 0)); // last row

        // WHEN Sort on 2nd letter of name DESC
        frameFixture.table("table.sTableCore").tableHeader().clickColumn(3);
        // THEN
        Assertions.assertEquals(bredevoort.getName(), sTable.getSTableCore().getValueAt(0, 0)); // first row
        Assertions.assertEquals(paris.getName(), sTable.getSTableCore().getValueAt(4, 0)); // last row
    }

    @Test
    public void happyGetSelectionSingleWhileSortedTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
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
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
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
    public void happyGetSelectionMultipleWhileSortedTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> data = List.of(berlin, bredevoort, amsterdam, rome, paris);

        CityFormat cityFormat = new CityFormat(data);

        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE) //
                    .selectionMode(STable.SelectionMode.MULTIPLE)
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
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

    @Test
    public void happySetSelectionSingleWhileSortedTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> data = List.of(berlin, bredevoort, amsterdam, rome, paris);

        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE) //
                    .selectionMode(STable.SelectionMode.SINGLE)
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
        });
        JTableFixture tableFixture = frameFixture.table("table.sTableCore");
        List<City> selection;

        // GIVEN
        tableFixture.tableHeader().clickColumn(0); // Sort on name

        // WHEN
        SwingUtilities.invokeAndWait(() -> sTable.setSelection(List.of(berlin)));
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(berlin, selection.get(0));

        // WHEN
        SwingUtilities.invokeAndWait(() -> sTable.setSelection(List.of(paris)));
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(paris, selection.get(0));

        // WHEN
        SwingUtilities.invokeAndWait(() -> sTable.setSelection(List.of(bredevoort, rome)));
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(rome, selection.get(0)); // Last one is selected
    }

    @Test
    public void happySetSelectionMultipleWhileSortedTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> data = List.of(berlin, bredevoort, amsterdam, rome, paris);

        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE) //
                    .selectionMode(STable.SelectionMode.MULTIPLE)
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
        });
        JTableFixture tableFixture = frameFixture.table("table.sTableCore");
        List<City> selection;

        // GIVEN
        tableFixture.tableHeader().clickColumn(0); // Sort on name

        // WHEN
        SwingUtilities.invokeAndWait(() -> sTable.setSelection(List.of(amsterdam, rome)));
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(2, selection.size());
        Assertions.assertEquals(amsterdam, selection.get(0));
        Assertions.assertEquals(rome, selection.get(1));

        // WHEN
        SwingUtilities.invokeAndWait(() -> sTable.setSelection(List.of(paris)));
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(paris, selection.get(0));
    }

    @Test
    public void happySetSelectionIntervalWhileSortedTest() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City bredevoort = City.of("Bredevoort", 5);
        City paris = City.of("Paris", 575);
        City rome = City.of("Rome", 1560);
        List<City> data = List.of(berlin, bredevoort, amsterdam, rome, paris);

        construct(() -> {
            sTable = new STable<City>() //
                    .name("table") //
                    .columns(City.class, City.NAME, City.DISTANCE) //
                    .selectionMode(STable.SelectionMode.INTERVAL)
                    .items(data);
            return TestUtil.inSFrame(sTable, focusMeComponent());
        });
        JTableFixture tableFixture = frameFixture.table("table.sTableCore");
        List<City> selection;

        // GIVEN
        tableFixture.tableHeader().clickColumn(0); // Sort on name

        // WHEN not interval able selection is provided
        SwingUtilities.invokeAndWait(() -> sTable.setSelection(List.of(amsterdam, paris, rome)));
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(2, selection.size());  // only last interval is selected
        Assertions.assertEquals(paris, selection.get(0));
        Assertions.assertEquals(rome, selection.get(1));

        // WHEN
        SwingUtilities.invokeAndWait(() -> sTable.setSelection(List.of(berlin, bredevoort)));
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(2, selection.size());
        Assertions.assertEquals(berlin, selection.get(0));
        Assertions.assertEquals(bredevoort, selection.get(1));

        // WHEN
        SwingUtilities.invokeAndWait(() -> sTable.setSelection(List.of(amsterdam)));
        // THEN
        selection = sTable.getSelection();
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals(amsterdam, selection.get(0));
    }
}
