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
        Bean1 bean1 = new Bean1().name("Tom").distance(52);
        Bean1 bean2 = new Bean1().name("Corine").distance(48);
        List<Bean1> data = List.of(bean1, bean2);
        construct(() -> {
            sTable = new STable<Bean1>() //
                    .name("table") //
                    .column(String.class).valueSupplier(Bean1::getName).valueConsumer(Bean1::setName).table()
                    .column(Integer.class).valueSupplier(d -> d.getDistance()).table();

            sTable.data(data);

            return TestUtil.inJFrame(sTable, focusMeComponent());
        });

        // WHEN
        frameFixture.table("table.sTable").enterValue(TableCell.row(0).column(0), "abc");
        moveFocus();

        // THEN
        Assertions.assertEquals("abc", sTable.sTable().getValueAt(0, 0));
        Assertions.assertEquals("abc", bean1.getName());
        Assertions.assertEquals("Corine", bean2.getName());
    }

    @Test
    public void happyStringPropertyTest() throws Exception {

        // GIVEN
        Bean1 bean1 = new Bean1().name("Tom").distance(52);
        Bean1 bean2 = new Bean1().name("Corine").distance(48);
        List<Bean1> data = List.of(bean1, bean2);
        construct(() -> {
            sTable = new STable<Bean1>() //
                    .name("table") //
                    .columns(Bean1.class, Bean1.NAME, Bean1.DISTANCE);

            sTable.data(data);

            return TestUtil.inJFrame(sTable, focusMeComponent());
        });

        // WHEN
        frameFixture.table("table.sTable").enterValue(TableCell.row(0).column(0), "abc");
        moveFocus();

        // THEN
        Assertions.assertEquals("abc", sTable.sTable().getValueAt(0, 0));
        Assertions.assertEquals("abc", bean1.getName());
        Assertions.assertEquals("Corine", bean2.getName());
    }

    @Test
    public void happyStringMonitorTest() throws Exception {

        // GIVEN
        Bean1 bean1 = new Bean1().name("Tom").distance(52);
        Bean1 bean2 = new Bean1().name("Corine").distance(48);
        List<Bean1> data = List.of(bean1, bean2);
        construct(() -> {
            sTable = new STable() //
                    .name("table") //
                    .columns(Bean1.class, Bean1.NAME, Bean1.DISTANCE);

            sTable.data(data);

            return TestUtil.inJFrame(sTable, focusMeComponent());
        });

        // WHEN
        SwingUtilities.invokeAndWait(() -> {
            bean1.setName("def");
        });

        // THEN
        Assertions.assertEquals("def", sTable.sTable().getValueAt(0, 0));
        Assertions.assertEquals("Corine", bean2.getName());
    }

    // test per column editor/renderer
}
