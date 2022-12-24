package org.tbee.sway;

import javax.swing.*;
import java.util.List;

public class STableApp {

    static public void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            STable sTable = new STable<Bean1>() //

                    // add columns via lambda's (no reflection)
                    .column(String.class).title("Name CT").valueSupplier(d -> d.getName()).valueConsumer((d,v) -> d.setName(v)).bindToProperty(Bean1.NAME).table() //
                    .column(Integer.class).title("Distance CT").valueSupplier(d -> d.getDistance()).valueConsumer((d, v) -> d.setDistance(v)).bindToProperty(Bean1.DISTANCE).table() //

                    // add columns via method references (no reflection)
                    .column(String.class).title("Name MR").valueSupplier(Bean1::getName).valueConsumer(Bean1::setName).bindToProperty(Bean1.NAME).table() //
                    .column(Integer.class).title("Distance MR").valueSupplier(Bean1::getDistance).valueConsumer(Bean1::setDistance).bindToProperty(Bean1.DISTANCE).id("marker").table() //

                    // add columns using BeanInfo (uses reflection)
                    .columns(Bean1.class, "name", Bean1.NAME, Bean1.DISTANCE, Bean1.DISTANCEINT, Bean1.ROUNDTRIP)

                    // automatically update (uses reflection)
                    .bindToBean(Bean1.class)

                    // find column
                    .findColumnById("marker").title("DistanceMR*").table()
             ;

            sTable.setData(List.of(new Bean1().name("Tom").distance(52), new Bean1().name("Corine").distance(48)));

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(new JScrollPane(sTable));
            jFrame.pack();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setVisible(true);
        });
    }
}
