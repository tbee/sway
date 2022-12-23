package org.tbee.sway;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.tbee.sway.binding.BeanBinder;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Locale;

public class STextFieldApp {

    static public void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            Bean1 bean1 = new Bean1().name("test").age(12);
            BeanBinder<Bean1> beanBinder = new BeanBinder<>(bean1);

            STextField.ofStringBlankIsNull().bind(beanBinder, Bean1.NAME).unbind(beanBinder, Bean1.NAME);

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new MigLayout());

            jPanel.add(new JLabel("String"), new CC().alignX("right"));
            jPanel.add(STextField.ofString().value("abc"), new CC().wrap());

            jPanel.add(new JLabel("String -> bean.name"), new CC().alignX("right"));
            jPanel.add(STextField.ofString().bind(bean1, Bean1.NAME), new CC().wrap());

            jPanel.add(new JLabel("StringBlankIsNull -> bean.name"), new CC().alignX("right"));
            jPanel.add(STextField.ofStringBlankIsNull().bind(beanBinder, Bean1.NAME), new CC().wrap());

            jPanel.add(new JLabel("Integer -> bean.age"), new CC().alignX("right"));
            jPanel.add(STextField.ofInteger().bind(bean1, Bean1.AGE), new CC().wrap());

            jPanel.add(new JLabel("Long"), new CC().alignX("right"));
            jPanel.add(STextField.ofLong().value(123l), new CC().wrap());

            jPanel.add(new JLabel("Double"), new CC().alignX("right"));
            jPanel.add(STextField.ofDouble().value(1.23), new CC().wrap());

            jPanel.add(new JLabel("Percent"), new CC().alignX("right"));
            jPanel.add(STextField.ofPercent().value(1.23), new CC().wrap());

            jPanel.add(new JLabel("Currency"), new CC().alignX("right"));
            jPanel.add(STextField.ofCurrency().value(1.23), new CC().wrap());

            jPanel.add(new JLabel("Currency JAPAN"), new CC().alignX("right"));
            jPanel.add(STextField.ofCurrency(Locale.JAPAN).value(1.23), new CC().wrap());

            jPanel.add(new JLabel("Currency EURO"), new CC().alignX("right"));
            jPanel.add(STextField.ofCurrency(Currency.getInstance("EUR")).value(1.23), new CC().wrap());

            jPanel.add(new JLabel("BigInteger"), new CC().alignX("right"));
            jPanel.add(STextField.ofBigInteger().value(BigInteger.ONE), new CC().wrap());

            jPanel.add(new JLabel("BigDecimal"), new CC().alignX("right"));
            jPanel.add(STextField.ofBigDecimal().value(BigDecimal.TEN.ONE), new CC().wrap());

            jPanel.add(new JLabel("LocalDate"), new CC().alignX("right"));
            jPanel.add(STextField.ofLocalDate().value(LocalDate.now()), new CC().wrap());

            jPanel.add(new JLabel("LocalDateTime"), new CC().alignX("right"));
            jPanel.add(STextField.ofLocalDateTime().value(LocalDateTime.now()), new CC().wrap());

            jPanel.add(new JLabel("ZonedDateTime"), new CC().alignX("right"));
            jPanel.add(STextField.ofZonedDateTime().value(ZonedDateTime.now()), new CC().wrap());

            jPanel.add(new JLabel("OffsetDateTime"), new CC().alignX("right"));
            jPanel.add(STextField.ofOffsetDateTime().value(OffsetDateTime.now()), new CC().wrap());

            JButton jButton = new JButton("set name");
            jButton.addActionListener(e -> bean1.setName("name" + System.currentTimeMillis()));
            jPanel.add(jButton, new CC().skip(1).wrap());

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(jPanel);
            jFrame.pack();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setVisible(true);
        });
    }
}
