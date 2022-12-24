package org.tbee.sway;

import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class STableTest {

    @Test
    public void basicDisplayTest() throws Exception {
        var sTableRef = new AtomicReference<STable>(null);
        JFrame frame = GuiActionRunner.execute(() -> {
            // GIVEN
            STable sTable = new STable<Bean1>() //
                    .name("table") //
                    .column(String.class).valueSupplier(Bean1::getName).valueConsumer(Bean1::setName).table()
                    .column(Integer.class).valueSupplier(d -> d.getDistance()).table();
            sTableRef.set(sTable);

            sTable.setData(List.of(new Bean1().name("Tom").distance(52), new Bean1().name("Corine").distance(48)));

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(new JScrollPane(sTable));
            jFrame.pack();
            jFrame.setVisible(true);
            return jFrame;
        });

        FrameFixture window = new FrameFixture(frame);
        window.show(); // shows the frame to test
        window.table("table").enterValue(TableCell.row(0).column(0), "123");
        Assertions.assertEquals("123", sTableRef.get().getValueAt(0, 0));
    }
}
