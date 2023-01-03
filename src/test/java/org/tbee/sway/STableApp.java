package org.tbee.sway;

import org.tbee.sway.format.Format;
import org.tbee.sway.support.DebugUtil;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.Comparator;
import java.util.List;

public class STableApp {

    static public void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            City amsterdam = new City("Amsterdam", 150);
            City berlin = new City("Berlin", 560);
            City rome = new City("Rome", 1560);
            City paris = new City("Paris", 575);
            amsterdam.sisterCity(berlin);
            rome.sisterCity(paris);
            var cities = List.of(amsterdam, berlin, rome, paris);

            Format<City> cityFormat = new CityFormat(cities);

            var sTable = new STable<City>() //
                    .name("mySTable") //

                    // add columns via lambda's (no reflection)
                    .column(String.class).title("Name CT").valueSupplier(d -> d.getName()).valueConsumer((d,v) -> d.setName(v)).monitorProperty(City.NAME).table() //
                    .column(Integer.class).title("Distance CT").valueSupplier(d -> d.getDistance()).valueConsumer((d, v) -> d.setDistance(v)).monitorProperty(City.DISTANCE).table() //

                    // add columns via method references (no reflection)
                    .column(String.class).title("Name MR sort2e").valueSupplier(City::getName).valueConsumer(City::setName).monitorProperty(City.NAME).sorting(Comparator.comparing(o -> o.substring(1))).table() //
                    .column(Integer.class).title("Distance MR").valueSupplier(City::getDistance).valueConsumer(City::setDistance).monitorProperty(City.DISTANCE).id("marker").table() //

                    // add columns using BeanInfo (uses reflection)
                    .columns(City.class, City.NAME)

                    // add columns using BeanInfo (uses reflection)
                    .columns(City.class)

                    // automatically update (uses reflection)
                    .monitorBean(City.class) //

                    // find column
                    .<Integer>findColumnById("marker").title("DistanceMR*").table() //
                    .<City>findColumnById("sisterCity").title("Sister City").renderer(cityFormat).editor(cityFormat).table() //

                    // selection
                    .selectionMode(STable.SelectionMode.MULTIPLE) //
                    .onSelectionChanged(selection -> System.out.println("onSelectionChanged: " + selection)) //

                    // filter
                    .filterHeaderEnabled(true) //

                    // data
                    .data(cities) //
             ;

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(sTable);
            jFrame.setSize(1600, 800);
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setVisible(true);

            System.out.println(DebugUtil.componentTreeAsString(jFrame));
        });
    }
}
