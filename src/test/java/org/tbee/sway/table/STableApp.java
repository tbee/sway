package org.tbee.sway.table;

import org.tbee.sway.Bean1;
import org.tbee.sway.STable;

import javax.swing.*;
import java.util.List;

public class STableApp {

    static public void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            STable sTable = new STable<Bean1>() //
                    // basic column creation, but the whole generics is not pretty; should we even make this public?
                    .column(new TableColumn<Bean1, String>(String.class).title("Name BSC").valueSupplier(d -> d.getName())) //
                    .column(new TableColumn<Bean1, Integer>(Integer.class).title("Age BSC").valueSupplier(d -> d.getAge())) //

                    // using the columns-with-class method, no generics, but requires a closing table() call for fluent api
                    .column(String.class).title("Name CT").valueSupplier(d -> d.getName()).valueConsumer((d,v) -> d.setName(v)).table() //
                    .column(Integer.class).title("Age CT").valueSupplier(d -> d.getAge()).valueConsumer((d,v) -> d.setAge(v)).table() //

                    // using the columns-with-class method, no generics, but requires a closing table() call for fluent api
                    .column(String.class).title("Name MR").valueSupplier(Bean1::getName).valueConsumer(Bean1::setName).table() //
                    .column(Integer.class).title("Age MR").valueSupplier(Bean1::getAge).valueConsumer(Bean1::setAge).id("marker").table() //

                    // Using reflection via BeanInfo
                    .columns(Bean1.class, "name", Bean1.NAME, Bean1.AGE, Bean1.AGEINT, Bean1.CALC)

                    // find
                    .findColumnById("marker").title("AgeMR*").table()
             ;

            sTable.setData(List.of(new Bean1().name("Tom").age(52), new Bean1().name("Corine").age(48)));

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(new JScrollPane(sTable));
            jFrame.pack();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setVisible(true);
        });
    }
}
