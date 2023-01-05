package org.tbee.sway;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.swing.FontIcon;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.format.Format;
import org.tbee.sway.support.DebugUtil;
import org.tbee.sway.support.IconRegistry;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class TestApp {

    static public void main(String[] args) throws Exception {
        registerIcons();

        SwingUtilities.invokeAndWait(() -> {
            SMigLayoutPanel panel = new SMigLayoutPanel();

            panel.add(sTable());
            panel.add(sList());
            panel.add(sTextField());

            SContextMenu.install();

            JFrame jFrame = new JFrame();
            jFrame.setContentPane(panel);
            jFrame.setSize(1600, 800);
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setVisible(true);

            System.out.println(DebugUtil.componentTreeAsString(jFrame));
        });
    }

    private static void registerIcons() {
        // https://kordamp.org/ikonli/cheat-sheet-material2.html
        IconRegistry.register("copy", IconRegistry.Usage.MENU, createIcon(MaterialDesignC.CONTENT_COPY));
        IconRegistry.register("cut", IconRegistry.Usage.MENU, createIcon(MaterialDesignC.CONTENT_CUT));
        IconRegistry.register("paste", IconRegistry.Usage.MENU, createIcon(MaterialDesignC.CONTENT_PASTE));
        IconRegistry.register("filter", IconRegistry.Usage.MENU, createIcon(MaterialDesignF.FILTER));
    }
    private static Icon createIcon(Ikon ikon) {
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIkon(ikon);
        fontIcon.setIconSize(16);
        return fontIcon;
    }

    static private JPanel sTextField() {
        City bean = new City("test",12);
        BeanBinder<City> beanBinder = new BeanBinder<>(bean);

        STextField.ofStringBlankIsNull().binding(beanBinder, City.NAME).unbind();

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new MigLayout());

        jPanel.add(new JLabel("String"), new CC().alignX("right"));
        jPanel.add(STextField.ofString().value("abc"), new CC().wrap());

        jPanel.add(new JLabel("String -> bean.name"), new CC().alignX("right"));
        jPanel.add(STextField.ofString().bind(bean, City.NAME), new CC().wrap());

        jPanel.add(new JLabel("StringBlankIsNull -> bean.name"), new CC().alignX("right"));
        jPanel.add(STextField.ofStringBlankIsNull().bind(beanBinder, City.NAME), new CC().wrap());

        jPanel.add(new JLabel("Integer -> bean.age"), new CC().alignX("right"));
        jPanel.add(STextField.ofInteger().bind(bean, City.DISTANCE), new CC().wrap());

        jPanel.add(new JLabel("Integer -> ofBind bean.age"), new CC().alignX("right"));
        jPanel.add(STextField.ofBind(bean, City.DISTANCE), new CC().wrap());

        jPanel.add(new JLabel("Integer -> ofBind beanBinder.age"), new CC().alignX("right"));
        jPanel.add(STextField.ofBind(beanBinder, City.DISTANCE), new CC().wrap());

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
        jButton.addActionListener(e -> bean.setName("name" + System.currentTimeMillis()));
        jPanel.add(jButton, new CC().skip(1).wrap());

        return jPanel;
    }

    static private SList<City> sList() {
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        City rome = new City("Rome", 1560);
        City paris = new City("Paris", 575);
        amsterdam.sisterCity(berlin);
        rome.sisterCity(paris);
        var cities = List.of(amsterdam, berlin, rome, paris);

        Format<City> cityFormat = new CityFormat(cities);

        var sList = new SList<City>() //
                .name("mySList") //

                .render(new CityFormat(cities)) // toValue is never call so the list can be empty

                // data
                .data(cities) //
                ;

        return sList;
    }
    static private STable<City> sTable() {

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
                //.filterHeaderEnabled(true) //

                // data
                .data(cities) //
         ;
        return sTable;
    }
}
