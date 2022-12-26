package org.tbee.sway;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.Comparator;
import java.util.List;

public class STableApp {

    static public void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            var sTable = new STable<City>() //

                    // add columns via lambda's (no reflection)
                    .column(String.class).title("Name CT").valueSupplier(d -> d.getName()).valueConsumer((d,v) -> d.setName(v)).monitorProperty(City.NAME).table() //
                    .column(Integer.class).title("Distance CT").valueSupplier(d -> d.getDistance()).valueConsumer((d, v) -> d.setDistance(v)).monitorProperty(City.DISTANCE).table() //

                    // add columns via method references (no reflection)
                    .column(String.class).title("Name MR sort2e").valueSupplier(City::getName).valueConsumer(City::setName).monitorProperty(City.NAME).sortBy(Comparator.comparing(o -> o.substring(1))).table() //
                    .column(Integer.class).title("Distance MR").valueSupplier(City::getDistance).valueConsumer(City::setDistance).monitorProperty(City.DISTANCE).id("marker").table() //

                    // add columns using BeanInfo (uses reflection)
                    .columns(City.class, "name", City.NAME, City.DISTANCE, City.DISTANCEINT, City.ROUNDTRIP)

                    // automatically update (uses reflection)
                    .monitorBean(City.class) //

                    // find column
                    .findColumnById("marker").title("DistanceMR*").table() //

                    // selection
                    .selectionMode(STable.SelectionMode.MULTIPLE) //
                    .onSelectionChanged(selection -> System.out.println("onSelectionChanged: " + selection)) //

                    // data
                    .data(List.of(new City("Amsterdam", 150), new City("Berlin", 560), new City("Rome", 1560), new City("Paris", 575))) //
             ;

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(sTable);
            jFrame.pack();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setVisible(true);
        });
    }
}
