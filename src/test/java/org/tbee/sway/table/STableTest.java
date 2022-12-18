package org.tbee.sway.table;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;

public class STableTest {

    public static record Record1(String name, int age) {
    }

    public static record Record2(String name, int age, double length) {
    }

    @Test
    public void basicDisplayTest() throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            // GIVEN
            STable sTable = new STable<Record1>() //
                    .column(new TableColumn<Record1, String>(String.class).valueSupplier(d -> d.name())) // TBEERNOT can column creation be done compacter?
                    .column(new TableColumn<Record1, Integer>(Integer.class).valueSupplier(d -> d.age()));
            sTable.setData(List.of(new Record1("Tom", 52), new Record1("Corine", 48)));
            JFrame jFrame = new JFrame();
            jFrame.setContentPane(new JScrollPane(sTable));
            jFrame.pack();
            jFrame.setVisible(true);

            // THEN
            Assertions.assertEquals("Tom", sTable.getValueAt(0, 0));
            Assertions.assertEquals(52, sTable.getValueAt(0, 1));
            Assertions.assertEquals("Corine", sTable.getValueAt(1, 0));
            Assertions.assertEquals(48, sTable.getValueAt(1, 1));
        });
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
