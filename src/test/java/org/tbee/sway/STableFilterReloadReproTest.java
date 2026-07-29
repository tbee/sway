package org.tbee.sway;

import org.assertj.swing.edt.GuiActionRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

public class STableFilterReloadReproTest {

    @Test
    public void reload1000WithFilterHeader() throws Exception {
        Assertions.assertFalse(GraphicsEnvironment.isHeadless(), "needs a display");

        GuiActionRunner.execute(() -> {
            // mirror createEdifactTable(): filterHeaderEnabled BEFORE columns, empty initial data
            STable<City> sTable = new STable<City>()
                    .selectionMode(STable.SelectionMode.MULTIPLE)
                    .filterHeaderEnabled(true)
                    .column(String.class).valueSupplier(City::getName).table()
                    .column(Integer.class).valueSupplier(City::getDistance).table();

            JFrame frame = new JFrame();
            frame.add(new JScrollPane(sTable.getSTableCore()));
            frame.pack();
            frame.setVisible(true);

            // initial empty reload
            sTable.items(List.of());

            // now the big reload
            List<City> data = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                data.add(City.of("City" + i, i));
            }
            try {
                sTable.items(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Assertions.assertEquals(1000, sTable.getSTableCore().getModel().getRowCount());
            Assertions.assertEquals(1000, sTable.getSTableCore().getRowCount()); // view row count (no active filter)
            Assertions.assertTrue(sTable.isFilterHeaderEnabled());

            frame.dispose();
            return null;
        });
    }
}
