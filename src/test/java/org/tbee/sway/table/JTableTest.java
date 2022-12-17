package org.tbee.sway.table;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;

public class JTableTest {

    public static record Record1(String name, int age) {
    }

    public static record Record2(String name, int age, double length) {
    }

    @Test
    public void basicDisplayTest() throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            // GIVEN
            JTable jTable = new JTable<Record1>() //
                    .column(new TableColumn<Record1, String>(String.class).valueSupplier(d -> d.name())) // TBEERNOT can column creation be done compacter?
                    .column(new TableColumn<Record1, Integer>(Integer.class).valueSupplier(d -> d.age()));
            jTable.setData(List.of(new Record1("Tom", 52), new Record1("Corine", 48)));
            JFrame jFrame = new JFrame();
            jFrame.setContentPane(new JScrollPane(jTable));
            jFrame.pack();
            jFrame.setVisible(true);

            // THEN
            Assertions.assertEquals("Tom", jTable.getValueAt(0, 0));
            Assertions.assertEquals(52, jTable.getValueAt(0, 1));
            Assertions.assertEquals("Corine", jTable.getValueAt(1, 0));
            Assertions.assertEquals(48, jTable.getValueAt(1, 1));
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
