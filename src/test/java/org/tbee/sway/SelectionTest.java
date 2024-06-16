package org.tbee.sway;

import org.assertj.swing.core.MouseButton;
import org.assertj.swing.data.TableCell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SelectionTest extends TestBase {

    @Test
    public void treeTableListSelectionMultiple() throws Exception {

        // GIVEN
        City amsterdam = City.of("Amsterdam");
        City berlin = City.of("Berlin");
        City rome = City.of("Rome");
        City paris = City.of("Paris");
        amsterdam.addPartnerCity(berlin);
        amsterdam.addPartnerCity(rome);
        rome.addPartnerCity(paris);
        var cities = List.of(amsterdam, berlin, rome, paris);

        // GIVEN
        SFormatRegistry.register(City.class, new CityFormat(cities));

        // GIVEN
        var tableRef = new AtomicReference<STable<City>>();
        var listRef = new AtomicReference<SList<City>>();
        var treeRef = new AtomicReference<STree<City>>();
        construct(() -> {

            var sTable = new STable<City>()
                    .name("table")
                    .columns(City.class, City.NAME)
                    .selectionMode(STable.SelectionMode.MULTIPLE)
                    .items(cities);
            tableRef.set(sTable);

            var sList = new SList<City>()
                    .name("list")
                    .selectionMode(SList.SelectionMode.MULTIPLE)
                    .items(cities);
            sList.selection$().bindTo(sTable.selection$());
            listRef.set(sList);

            var sTree = new STree<City>()
                    .name("tree")
                    .selectionMode(STree.SelectionMode.MULTIPLE)
                    .root(amsterdam)
                    .childrenOf(City::getPartnerCities);
            sTree.selection$().bindTo(sTable.selection$());
            treeRef.set(sTree);

            return TestUtil.inSFrame(sTable, sList, sTree, focusMeComponent());
        });
        STable<City> sTable = tableRef.get();
        SList<City> sList = listRef.get();
        STree<City> sTree = treeRef.get();

        // WHEN
        frameFixture.table("table.sTableCore").click(TableCell.row(0).column(0), MouseButton.LEFT_BUTTON);;
        // THEN
        assertSize(1, sList.getSelection(), sTable.getSelection(), sTree.getSelection());
        assertContains(amsterdam, sList.getSelection(), sTable.getSelection(), sTree.getSelection());

        // WHEN
        frameFixture.list("list.sListCore").clickItem("Rome");
        // THEN
        assertSize(1, sList.getSelection(), sTable.getSelection(), sTree.getSelection());
        assertContains(rome, sList.getSelection(), sTable.getSelection(), sTree.getSelection());

        // WHEN control select
        frameFixture.pressKey(KeyEvent.VK_CONTROL);
        frameFixture.tree("tree.sTreeCore").clickPath("Amsterdam");
        frameFixture.releaseKey(KeyEvent.VK_CONTROL);
        // THEN
        assertSize(2, sList.getSelection(), sTable.getSelection(), sTree.getSelection());
        assertContains(amsterdam, sList.getSelection(), sTable.getSelection(), sTree.getSelection());
        assertContains(rome, sList.getSelection(), sTable.getSelection(), sTree.getSelection());
    }

    private void assertSize(int size, List<City>... selections) {
        for (List<City> selection : selections) {
            Assertions.assertEquals(size, selection.size());
        }
    }

    private void assertContains(City city, List<City>... selections) {
        for (List<City> selection : selections) {
            Assertions.assertTrue(selection.contains(city));
        }
    }
}
