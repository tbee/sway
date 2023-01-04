package org.tbee.sway;

import org.tbee.sway.format.Format;
import org.tbee.sway.support.DebugUtil;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.List;

public class SListApp {

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

            var sTable = new SList<City>() //
                    .name("mySList") //

                    .render(new CityFormat(List.of())) // toValue is never call so the list can be empty

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
