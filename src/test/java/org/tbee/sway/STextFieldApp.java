package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Locale;

public class STextFieldApp {

    static public void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            Bean1 bean1 = new Bean1().name("test").age(12);
            BeanBinder<Bean1> beanBinder = new BeanBinder<>(bean1);

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new FlowLayout());
            jPanel.add(STextField.ofString().value("abc").bind(bean1, Bean1.NAME));
            jPanel.add(STextField.ofString().value("def").bind(bean1, Bean1.NAME));
            jPanel.add(STextField.ofStringBlankIsNull().name("ofStringBlankIsNull").value("abc").bind(beanBinder, Bean1.NAME));
            jPanel.add(STextField.ofInteger().value(123));
            jPanel.add(STextField.ofPercent().value(1.23));
            jPanel.add(STextField.ofCurrency().value(1.23));
            jPanel.add(STextField.ofCurrency(Locale.JAPAN).value(1.23));
            jPanel.add(STextField.ofCurrency(Currency.getInstance("EUR")).value(1.23));
            jPanel.add(STextField.ofBigInteger().value(BigInteger.ONE));
            jPanel.add(STextField.ofBigDecimal().value(BigDecimal.TEN.ONE));

            JButton jButton = new JButton("set name");
            jButton.addActionListener(e -> bean1.setName("name" + System.currentTimeMillis()));
            jPanel.add(jButton);

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(jPanel);
            jFrame.pack();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setVisible(true);
        });
    }
}
