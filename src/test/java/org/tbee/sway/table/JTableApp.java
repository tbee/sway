package org.tbee.sway.table;

import javax.swing.*;
import java.util.List;

public class JTableApp {

    static public void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            // GIVEN
            JTable jTable = new JTable<Bean1>() //
                    // basic column creation, but the whole generics is not pretty; should we even make this public?
                    .column(new TableColumn<Bean1, String>(String.class).title("Name").valueSupplier(d -> d.getName())) //
                    .column(new TableColumn<Bean1, Integer>(Integer.class).title("Age").valueSupplier(d -> d.getAge())) //
                    // using the columns-with-class method, no generics, but requires a close table() call for fluent api
                    .column(String.class).title("Name").valueSupplier(d -> d.getName()).table() //
                    .column(Integer.class).title("Age").valueSupplier(d -> d.getAge()).table() //
                    // TODO: bean property
             ;

            jTable.setData(List.of(new Bean1("Tom", 52), new Bean1("Corine", 48)));

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(new JScrollPane(jTable));
            jFrame.pack();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setVisible(true);
        });
    }

    public static class Bean1 {
        String name;
        int age;

        public Bean1(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
