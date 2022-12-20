package org.tbee.sway;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
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
            jPanel.add(STextField.ofBigInteger().value(BigInteger.ONE));
            jPanel.add(STextField.ofBigDecimal().value(BigDecimal.TEN.ONE));

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(jPanel);
            jFrame.pack();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setVisible(true);
        });
    }
}
