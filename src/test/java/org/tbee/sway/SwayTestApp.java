package org.tbee.sway;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;
import org.kordamp.ikonli.swing.FontIcon;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.format.Format;
import org.tbee.sway.support.IconRegistry;
import org.tbee.util.ExceptionUtil;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
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

import static org.tbee.sway.format.FileFormat.AllowedType.DIR;
import static org.tbee.sway.format.FileFormat.AllowedType.FILE;

public class SwayTestApp {

    static public void main(String[] args) throws Exception {
        new SwayTestApp().run();
    }

    private void run() throws InterruptedException, InvocationTargetException {
        SLookAndFeel.installDefault();

        registerIcons();

        SwingUtilities.invokeAndWait(() -> {
            STabbedPane<Void> tabbedPane = STabbedPane.<Void>of()
                    .tab("sTable", sTable())
                    .tab("sList", sList())
                    .tab("sTree", sTree())
                    .tab("sComboBox", sComboBox())
                    .tab("sTextField", sTextField())
                    .tab("sTextArea", sTextArea())
                    .tab("sCheckBox", sCheckBox())
                    .tab("sTabbedPane", sTabbedPane());

            SContextMenu.install();

            SFrame.of(tabbedPane)
                    .exitOnClose()
                    .sizeToPreferred()
                    .maximize()
                    .menuBar(this::createFrameMenu)
                    .visible(true);
        });
    }

    private void createFrameMenu(SMenuBar sMenuBar) {
        sMenuBar
            .add(SMenu.of("menu1", createIcon(MaterialDesignC.CACHED, 16))
                .add(SMenuItem.of("menuitem 1a", createIcon(MaterialDesignC.CALENDAR_CHECK, 16), SwayTestApp::menuEvent))
                .add(SMenuItem.of("menuitem 1b", SwayTestApp::menuEvent))
            )
            .add(SMenu.of("menu2")
                .add(SMenuItem.of("menuitem 2a", SwayTestApp::menuEvent))
                .add(SMenuItem.of("menuitem 2b", SwayTestApp::menuEvent))
                .add(SMenuItem.of("menuitem 2c", SwayTestApp::menuEvent))
            )
            .add(SMenu.of("menu3")
                .add(SMenuItem.of("menuitem 3a", SwayTestApp::menuEvent))
            );
    }
    static void menuEvent(ActionEvent e) {
        SOptionPane.ofInfo((Component) e.getSource(), "Menu event", e.getActionCommand());
    }

    private void registerIcons() {
        // https://kordamp.org/ikonli/cheat-sheet-material2.html
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_COPY, createIcon(MaterialDesignC.CONTENT_COPY, IconRegistry.SwayInternallyUsedIcon.MENU_COPY.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_CUT, createIcon(MaterialDesignC.CONTENT_CUT, IconRegistry.SwayInternallyUsedIcon.MENU_CUT.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_PASTE, createIcon(MaterialDesignC.CONTENT_PASTE, IconRegistry.SwayInternallyUsedIcon.MENU_PASTE.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_FILTER, createIcon(MaterialDesignF.FILTER, IconRegistry.SwayInternallyUsedIcon.MENU_FILTER.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.MENU_SELECTION, createIcon(MaterialDesignS.SELECTION, IconRegistry.SwayInternallyUsedIcon.MENU_SELECTION.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING, createIcon(MaterialDesignR.REFRESH, IconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING.typicalSize()));
        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.TEXTFIELD_POPUP, createIcon(MaterialDesignM.MENU, IconRegistry.SwayInternallyUsedIcon.TEXTFIELD_POPUP.typicalSize()));

//        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED, createIcon(MaterialDesignS.SELECT, IconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED.typicalSize()));
//        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED, createIcon(MaterialDesignS.SELECT_INVERSE, IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED.typicalSize()));
//        IconRegistry.register(IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNDETERMINED, createIcon(MaterialDesignS.SELECT_OFF, IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNDETERMINED.typicalSize()));
    }
    private Icon createIcon(Ikon ikon, int size) {
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIkon(ikon);
        fontIcon.setIconSize(size);
        return fontIcon;
    }

    static JPanel sTextField() {
        City bean = City.of("test",12);
        BeanBinder<City> beanBinder = new BeanBinder<>(bean);

        STextField.ofStringBlankIsNull().value$().bindTo(City.name$(beanBinder)).unbind(); // test unbind

        SMigPanel migPanel = SMigPanel.of();

        migPanel.addLabelAndField("String", STextField.ofString().value("abc"));
        migPanel.wrap();

        migPanel.addLabelAndField("String -> bean.name", STextField.ofString().bindTo(bean.name$()));
        migPanel.wrap();

        migPanel.addLabelAndField("StringBlankIsNull -> bean.name", STextField.ofStringBlankIsNull().bindTo(City.name$(beanBinder)));
        migPanel.wrap();

        migPanel.addLabelAndField("Integer -> bean.distance", STextField.ofInteger().bindTo(bean.distance$()));
        migPanel.wrap();

        migPanel.addLabelAndField("Integer -> beanbinder.distance", STextField.ofInteger().bindTo(City.distance$(beanBinder)));
        migPanel.wrap();

        migPanel.addLabelAndField("Integer -> ofBind bean.distance", STextField.ofBindTo(bean.distance$()));
        migPanel.wrap();

        migPanel.addLabelAndField("Integer -> ofBind beanBinder.distance", STextField.ofBindTo(City.distance$(beanBinder)));
        migPanel.wrap();

//        migPanel.addLabel(SLabel.of("Integer -> bean.distance add"));
//        STextField<Integer> integerSTextField = STextField.ofInteger();
//        integerSTextField.value$().add(2).bind(bean.distance$());
//        migPanel.addField(integerSTextField);
//        migPanel.wrap();

        migPanel.addLabelAndField("Long", STextField.ofLong().value(123l));
        migPanel.wrap();

        migPanel.addLabelAndField("Double", STextField.ofDouble().value(1.23));
        migPanel.wrap();

        migPanel.addLabelAndField("Percent", STextField.ofPercent().value(1.23));
        migPanel.wrap();

        migPanel.addLabelAndField("Currency", STextField.ofCurrency().value(1.23));
        migPanel.wrap();

        migPanel.addLabelAndField("Currency JAPAN", STextField.ofCurrency(Locale.JAPAN).value(1.23));
        migPanel.wrap();

        migPanel.addLabelAndField("Currency EURO", STextField.ofCurrency(Currency.getInstance("EUR")).value(1.23));
        migPanel.wrap();

        migPanel.addLabelAndField("BigInteger", STextField.ofBigInteger().value(BigInteger.ONE));
        migPanel.wrap();

        migPanel.addLabelAndField("BigDecimal", STextField.ofBigDecimal().value(BigDecimal.TEN));
        migPanel.wrap();

        migPanel.addLabelAndField("LocalDate", STextField.ofLocalDate().value(LocalDate.now()));
        migPanel.wrap();

        migPanel.addLabelAndField("LocalDateTime", STextField.ofLocalDateTime().value(LocalDateTime.now()));
        migPanel.wrap();

        migPanel.addLabelAndField("ZonedDateTime", STextField.ofZonedDateTime().value(ZonedDateTime.now()));
        migPanel.wrap();

        migPanel.addLabelAndField("OffsetDateTime", STextField.ofOffsetDateTime().value(OffsetDateTime.now()));
        migPanel.wrap();

        JButton jButton = SButton.of("set name", e -> bean.setName("name" + System.currentTimeMillis()));
        migPanel.addField(jButton).skip(1);
        migPanel.wrap();

        migPanel.addLabelAndField("Icon", STextField.ofLong().value(123l)
                .icon(IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.MENU_COPY))
                .onIconClick(evt -> SOptionPane.ofInfo(migPanel, "Icon clicked", "Icon clicked"))
        );
        migPanel.wrap();

        migPanel.addLabelAndField("File", SFileTextField.of());
        migPanel.wrap();

        migPanel.addLabelAndField("File mustExist", SFileTextField.of().mustExist(true));
        migPanel.wrap();

        migPanel.addLabelAndField("File fileOnly", SFileTextField.of().allowedType(FILE));
        migPanel.wrap();

        migPanel.addLabelAndField("File dirOnly", SFileTextField.of().allowedType(DIR));
        migPanel.wrap();

        return SVPanel.of(SLabel.of("STextField"), migPanel).margin(0);
    }

    static SVPanel sList() {
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City rome = City.of("Rome", 1560);
        City paris = City.of("Paris", 575);
        amsterdam.sisterCity(berlin);
        rome.sisterCity(paris);
        var cities = List.of(amsterdam, berlin, rome, paris);

        var sList = SList.of(cities) //
                .render(new CityFormat(cities))
                .onSelectionChanged(cs -> System.out.println("List selection: " + cs));

        return SVPanel.of(SLabel.of("SList"), sList).fillWidth(true).margin(0);
    }

    static SVPanel sTree() {

        City amsterdam = City.of("Amsterdam", 150);
        Street kalverstraat = amsterdam.addStreet(Street.of("Kalverstraat"));
        kalverstraat.addBuilding(Building.of(1));
        kalverstraat.addBuilding(Building.of(3));
        kalverstraat.addBuilding(Building.of(5));
        Street leidseplein = amsterdam.addStreet(Street.of("Leidseplein"));
        leidseplein.addBuilding(Building.of(2));
        leidseplein.addBuilding(Building.of(4));
        leidseplein.addBuilding(Building.of(8));

        City berlin = City.of("Berlin", 560);
        berlin.addStreet(Street.of("Kurfurstendam"));

        City rome = City.of("Rome", 1560);
        rome.addStreet(Street.of("Colloseo"));

        City paris = City.of("Paris", 575);

        var cities = List.of(amsterdam, berlin, rome, paris);

        var sTree = STree.of(cities)
                .childrenOf(City.class, City::getStreets)
                .childrenOf(Street.class, Street::getBuildings)
                .registerFormat(City.class, new CityFormat())
                .registerFormat(Street.class, new StreetFormat())
                .registerFormat(Building.class, new BuildingFormat())
                .monitorBeans(true)
                .onSelectionChanged(cs -> System.out.println("Tree selection: " + cs));

        STextField<String> leidsepleinNameSTextField = STextField.ofBindTo(leidseplein.name$());
        SButton addButton = SButton.of("Add street to Rome", e -> rome.addStreet(Street.of("Street" + System.currentTimeMillis())));
        return SVPanel.of(SLabel.of("STree"), sTree, leidsepleinNameSTextField, addButton).fillWidth(true).margin(0);
    }

    static SVPanel sComboBox() {
        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City rome = City.of("Rome", 1560);
        City paris = City.of("Paris", 575);
        amsterdam.sisterCity(berlin);
        rome.sisterCity(paris);
        var cities = List.of(amsterdam, berlin, rome, paris);

        var sComboBox = SComboBox.<City>of() //
                .render(new CityFormat(cities))
                .onValueChanged(c -> System.out.println("Combobox: " + c))
                .data(cities);

        // The combobox is bound to the textfield, so it should have the value
        STextField<City> sTextFieldDisplay = STextField.of(new CityFormat(cities)).value(cities.get(0)).enabled(false);
        sComboBox.bindTo(sTextFieldDisplay.value$());

        // The textfield is bound to the combobox, so the combobox should has the value
        STextField<City> sTextField = STextField.of(new CityFormat(cities));
        sTextField.bindTo(sComboBox.value$());

        return SVPanel.of(SLabel.of("SComboBox"), sComboBox, sTextFieldDisplay, sTextField).fillWidth(true).margin(0);
    }

    static SSplitPanel sTable() {

        City amsterdam = City.of("Amsterdam", 150);
        City berlin = City.of("Berlin", 560);
        City rome = City.of("Rome", 1560);
        City paris = City.of("Paris", 575);
        amsterdam.sisterCity(berlin);
        rome.sisterCity(paris);
        var cities = List.of(amsterdam, berlin, rome, paris);

        Format<City> cityFormat = new CityFormat(cities);

        var sTable = STable.of(cities) //

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
                .onSelectionChanged(selection -> System.out.println("Table selection: " + selection)) //

                // filter
                .filterHeaderEnabled(true) //

                // data
                .data(cities) //
                .beanFactory(() -> City.of()) //
                .onRowAdded((b, i) -> System.out.println("added " + i + ": " + b)) //
                
                // must be last
                .preferencesId("mySTable") //
         ;

        // Bind selection to a list
        var sList = SList.of(cities) //
                .render(new CityFormat(cities))
                .selectionMode(SList.SelectionMode.MULTIPLE);
        sList.selection$().bindTo(sTable.selection$());

        // Bind selection to a tree
        amsterdam.addPartnerCity(berlin);
        amsterdam.addPartnerCity(rome);
        rome.addPartnerCity(paris);
        var sTree = STree.of(amsterdam) //
                .childrenOf(City::getPartnerCities)
                .render(new CityFormat(cities))
                .selectionMode(STree.SelectionMode.MULTIPLE);
        sTree.selection$().bindTo(sTable.selection$());

        return SSplitPanel.of(SVPanel.of(SLabel.of("STable"), sTable).fillWidth(true).margin(0),
                              SVPanel.of(SLabel.of("Bound selection"), sList, sTree).fillWidth(true).margin(0))
                .nameForPreferences("mySplitpane");

//        return SVPanel.of(SLabel.of("STable"), sTable, SLabel.of("Bound selection"), sList, sTree).fillWidth(true).margin(0);
    }

    static JPanel sCheckBox() {
        City city = City.of("test",12);
        BeanBinder<City> beanBinder = new BeanBinder<>(city);

        SMigPanel migPanel = SMigPanel.of();

        migPanel.addField(SCheckBox.of("boolean"));
        migPanel.wrap();
        migPanel.addField(SCheckBox.of("boolean bind").bindTo(city.growing$()));
        migPanel.addField(SCheckBox.of("boolean beanBinder").bindTo(City.growing$(beanBinder)));
        migPanel.wrap();

        migPanel.addField(SCheckBox3.of("Boolean"));
        migPanel.wrap();
        migPanel.addField(SCheckBox3.of("Boolean bind").bindTo(city.cityRights$()));
        migPanel.addField(SCheckBox3.of("Boolean beanBinder").bindTo(City.cityRights$(beanBinder)));
        migPanel.wrap();

        return SVPanel.of(SLabel.of("SCheckbox"), migPanel).margin(0);
    }
    
    static JPanel sTextArea() {
        City city = City.of("test",12);

        SMigPanel migPanel = SMigPanel.of();
    	migPanel.addLabelAndField("bind 1", STextArea.of().bindTo(city.name$())).wrap();
    	migPanel.addLabelAndField("bind 2", STextArea.of(5, 10).bindTo(city.name$())).wrap();
        return SVPanel.of(SLabel.of("STextArea"), migPanel).margin(0);
    }

    static JPanel sTabbedPane() {

        STextField<String> masterSTextField = STextField.ofString().value("master");

        STextField<String> sync1Textfield = STextField.ofString();
        STextField<String> sync2Textfield = STextField.ofString();
        STextField<Integer> asyncTextfield = STextField.ofInteger();
        STextField<String> subsyncTextfield = STextField.ofString();
        STextField<Integer> subasyncTextfield = STextField.ofInteger();

        STabbedPane<String> sTabbedPane = STabbedPane.<String>of()
            .bindTo(masterSTextField.value$())
            .tab("tab1", SHPanel.of(sync1Textfield), (v, c) -> sync1Textfield.setValue("child1 " + v))
            .tab("tab2", SHPanel.of(sync2Textfield), (v, c) -> sync2Textfield.setValue("child2 " + v))
            .tab("tabAsync", SHPanel.of(asyncTextfield)
                    , value -> doSomeBackgroundStuff(value)
                    , (result, component) -> asyncTextfield.setValue(result)
                    , (throwable, component) -> showExceptionInDialog(throwable, masterSTextField))
            .pane("subtab", STabbedPane.<String>of()
                            .bindTo(masterSTextField.value$())
                            .tab("sync", SHPanel.of(subsyncTextfield), (v, c) -> subsyncTextfield.setValue("child1 " + v))
                            .tab("async", SHPanel.of(subasyncTextfield)
                                    , value -> doSomeBackgroundStuff(value)
                                    , (result, component) -> asyncTextfield.setValue(result))
            );



        return SBorderPanel.of(sTabbedPane).north(masterSTextField);
    }

    private static int doSomeBackgroundStuff(String value) {
        if (value.contains("exc")) throw new RuntimeException("oops");
        sleep(3000);
        return value.hashCode();
    }

    private static void showExceptionInDialog(Throwable throwable, STextField<String> masterSTextField) {
        JOptionPane.showMessageDialog(masterSTextField, ExceptionUtil.determineMessage(throwable), "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
