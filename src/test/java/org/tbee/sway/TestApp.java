package org.tbee.sway;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;
import org.kordamp.ikonli.swing.FontIcon;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.format.Format;
import org.tbee.sway.support.DebugUtil;
import org.tbee.sway.support.IconRegistry;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
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
            SMigPanel panel = new SMigPanel() //
                    //.debug()
                    ;

            panel.addField(sTable()).grow();
            panel.addField(sList()).grow();
            panel.addField(sTextField()).grow();
            panel.addField(sCheckBox()).grow();

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
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.COPY, IconRegistry.Usage.MENU, createIcon(MaterialDesignC.CONTENT_COPY, IconRegistry.Usage.MENU.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.CUT, IconRegistry.Usage.MENU, createIcon(MaterialDesignC.CONTENT_CUT, IconRegistry.Usage.MENU.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.PASTE, IconRegistry.Usage.MENU, createIcon(MaterialDesignC.CONTENT_PASTE, IconRegistry.Usage.MENU.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.FILTER, IconRegistry.Usage.MENU, createIcon(MaterialDesignF.FILTER, IconRegistry.Usage.MENU.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.SELECTION, IconRegistry.Usage.MENU, createIcon(MaterialDesignS.SELECTION, IconRegistry.Usage.MENU.typicalSize()));

//        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.SELECTED, IconRegistry.Usage.COMPONENT, createIcon(MaterialDesignS.SELECT, IconRegistry.Usage.COMPONENT.typicalSize()));
//        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.UNSELECTED, IconRegistry.Usage.COMPONENT, createIcon(MaterialDesignS.SELECT_INVERSE, IconRegistry.Usage.COMPONENT.typicalSize()));
//        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.UNDETERMINED, IconRegistry.Usage.COMPONENT, createIcon(MaterialDesignS.SELECT_OFF, IconRegistry.Usage.COMPONENT.typicalSize()));
    }
    private static Icon createIcon(Ikon ikon, int size) {
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIkon(ikon);
        fontIcon.setIconSize(size);
        return fontIcon;
    }

    static private JPanel sTextField() {
        City bean = new City("test",12);
        BeanBinder<City> beanBinder = new BeanBinder<>(bean);

        STextField.ofStringBlankIsNull().binding(beanBinder, City.NAME).unbind(); // test unbind

        SMigPanel migPanel = new SMigPanel();

        migPanel.addLabel(new SLabel("String"));
        migPanel.addField(STextField.ofString().value("abc"));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("String -> bean.name"));
        migPanel.addField(STextField.ofString().bind(bean, City.NAME));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("StringBlankIsNull -> bean.name"));
        migPanel.addField(STextField.ofStringBlankIsNull().bind(beanBinder, City.NAME));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("Integer -> bean.age"));
        migPanel.addField(STextField.ofInteger().bind(bean, City.DISTANCE));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("Integer -> ofBind bean.age"));
        migPanel.addField(STextField.ofBind(bean, City.DISTANCE));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("Integer -> ofBind beanBinder.age"));
        migPanel.addField(STextField.ofBind(beanBinder, City.DISTANCE));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("Long"));
        migPanel.addField(STextField.ofLong().value(123l));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("Double"));
        migPanel.addField(STextField.ofDouble().value(1.23));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("Percent"));
        migPanel.addField(STextField.ofPercent().value(1.23));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("Currency"));
        migPanel.addField(STextField.ofCurrency().value(1.23));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("Currency JAPAN"));
        migPanel.addField(STextField.ofCurrency(Locale.JAPAN).value(1.23));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("Currency EURO"));
        migPanel.addField(STextField.ofCurrency(Currency.getInstance("EUR")).value(1.23));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("BigInteger"));
        migPanel.addField(STextField.ofBigInteger().value(BigInteger.ONE));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("BigDecimal"));
        migPanel.addField(STextField.ofBigDecimal().value(BigDecimal.TEN.ONE));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("LocalDate"));
        migPanel.addField(STextField.ofLocalDate().value(LocalDate.now()));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("LocalDateTime"));
        migPanel.addField(STextField.ofLocalDateTime().value(LocalDateTime.now()));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("ZonedDateTime"));
        migPanel.addField(STextField.ofZonedDateTime().value(ZonedDateTime.now()));
        migPanel.wrap();

        migPanel.addLabel(new SLabel("OffsetDateTime"));
        migPanel.addField(STextField.ofOffsetDateTime().value(OffsetDateTime.now()));
        migPanel.wrap();

        JButton jButton = new SButton("set name");
        jButton.addActionListener(e -> bean.setName("name" + System.currentTimeMillis()));
        migPanel.addField(jButton).skip(1);

        return migPanel;
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
                .beanFactory(() -> new City()) //
                .onRowAdded((b, i) -> System.out.println("added " + i + ": " + b)) //
         ;
        return sTable;
    }

    static private JPanel sCheckBox() {
        City city = new City("test",12);
        BeanBinder<City> beanBinder = new BeanBinder<>(city);

        SMigPanel migPanel = new SMigPanel();

        migPanel.addField(new SCheckBox("boolean"));
        migPanel.wrap();
        migPanel.addField(new SCheckBox("boolean bind").bind(city, City.GROWING));
        migPanel.addField(new SCheckBox("boolean beanBinder").bind(beanBinder, City.GROWING));
        migPanel.wrap();

        migPanel.addField(new SCheckBox3("Boolean"));
        migPanel.wrap();
        migPanel.addField(new SCheckBox3("Boolean bind").bind(city, City.CITYRIGHTS));
        migPanel.addField(new SCheckBox3("Boolean beanBinder").bind(beanBinder, City.CITYRIGHTS));
        migPanel.wrap();

        return migPanel;
    }
}
