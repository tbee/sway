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

public class SwayTestApp {

    static public void main(String[] args) throws Exception {
        registerIcons();

        SwingUtilities.invokeAndWait(() -> {
            SMigPanel panel = new SMigPanel() //
                    //.debug()
                    ;

            panel.addField(sTable()).grow();
            panel.addField(sList()).grow();
            panel.addField(sComboBox()).growX();
            panel.addField(sTextField()).grow();
            panel.addField(sTextArea()).grow();
            panel.addField(sCheckBox()).grow();

            SContextMenu.install();

            SFrame jFrame = SFrame.of(panel) //
                    .exitOnClose() //
                    .sizeToPreferred()
                    //.maximize() //
                    .visible();

            System.out.println(DebugUtil.componentTreeAsString(jFrame));
        });
    }

    private static void registerIcons() {
        // https://kordamp.org/ikonli/cheat-sheet-material2.html
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_COPY, createIcon(MaterialDesignC.CONTENT_COPY, IconRegistry.SwayInternallyUsedIcon.MENU_COPY.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_CUT, createIcon(MaterialDesignC.CONTENT_CUT, IconRegistry.SwayInternallyUsedIcon.MENU_CUT.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_PASTE, createIcon(MaterialDesignC.CONTENT_PASTE, IconRegistry.SwayInternallyUsedIcon.MENU_PASTE.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_FILTER, createIcon(MaterialDesignF.FILTER, IconRegistry.SwayInternallyUsedIcon.MENU_FILTER.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_SELECTION, createIcon(MaterialDesignS.SELECTION, IconRegistry.SwayInternallyUsedIcon.MENU_SELECTION.typicalSize()));

//        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED, createIcon(MaterialDesignS.SELECT, IconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED.typicalSize()));
//        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED, createIcon(MaterialDesignS.SELECT_INVERSE, IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED.typicalSize()));
//        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNDETERMINED, createIcon(MaterialDesignS.SELECT_OFF, IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNDETERMINED.typicalSize()));
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
        migPanel.addField(STextField.ofBigDecimal().value(BigDecimal.TEN));
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

    static private SVerticalPanel sList() {
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        City rome = new City("Rome", 1560);
        City paris = new City("Paris", 575);
        amsterdam.sisterCity(berlin);
        rome.sisterCity(paris);
        var cities = List.of(amsterdam, berlin, rome, paris);

        var sList = new SList<City>() //
                .name("mySList") //
                .render(new CityFormat(cities))
                .onSelectionChanged(cs -> System.out.println("List: " + cs))
                .data(cities) //
                ;

        return SVerticalPanel.of(sList);
    }

    static private SVerticalPanel sComboBox() {
        City amsterdam = new City("Amsterdam", 150);
        City berlin = new City("Berlin", 560);
        City rome = new City("Rome", 1560);
        City paris = new City("Paris", 575);
        amsterdam.sisterCity(berlin);
        rome.sisterCity(paris);
        var cities = List.of(amsterdam, berlin, rome, paris);

        var sComboBox = SComboBox.<City>of() //
                .name("mySComboBox") //
                .render(new CityFormat(cities))
                .onValueChanged(c -> System.out.println("Combobox: " + c))
                .data(cities);

        // The combobox is bound to the textfield, so it should have the value
        STextField<City> sTextFieldDisplay = STextField.of(new CityFormat(cities)).value(cities.get(0)).enabled(false);
        sComboBox.bind(sTextFieldDisplay, STextField.VALUE);

        // The textfield is bound to the combobox, so the combobox should has the value
        STextField<City> sTextField = STextField.of(new CityFormat(cities));
        sTextField.bind(sComboBox, STextField.VALUE);

        return SVerticalPanel.of(sComboBox, sTextFieldDisplay, sTextField);
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
                
                // must be last
                .preferencesId("mySTable") //
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
    
    static private SMigPanel sTextArea() {
        City city = new City("test",12);

        SMigPanel migPanel = new SMigPanel();
    	migPanel.addLabelAndField("bind 1", new STextArea().bind(city,  City.NAME)).wrap();
    	migPanel.addLabelAndField("bind 2", new STextArea(5, 10).bind(city,  City.NAME)).wrap();
        return migPanel;
    }
}
