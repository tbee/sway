package org.tbee.sway;

import javax.swing.*;
import java.awt.*;
import java.util.Currency;
import java.util.Locale;

public class STextFieldApp {

    static public void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new FlowLayout());
            jPanel.add(STextField.ofString().value("abc"));
            jPanel.add(STextField.ofStringBlankIsNull().value("abc"));
            jPanel.add(STextField.ofInteger().value(123));
            jPanel.add(STextField.ofPercent().value(1.23));
            jPanel.add(STextField.ofCurrency().value(1.23));
            jPanel.add(STextField.ofCurrency(Locale.JAPAN).value(1.23));
            jPanel.add(STextField.ofCurrency(Currency.getInstance("EUR")).value(1.23));

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(jPanel);
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
            System.out.println("setName " + name);
            this.name = name;
        }

        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            System.out.println("setAge " + age);
            this.age = age;
        }

        public Integer getAgeInt() {
            return getAge();
        }
        public void setAgeInt(Integer age) {
            setAge(age);
        }

        public Integer getCalc() {
            return age * 2;
        }
    }

    public static class Bean2 {

    }
}
