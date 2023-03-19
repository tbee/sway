# Sway
A more modern style API for the Java Swing UI framework, focused on ease of use.

Let's be honest, coding for Swing is not easy. And JTable is probably the epiphany;
you have to understand TableModel, manually add sorting, filtering, the events that trigger updates, etc. 
What if the Swing API would be revisited and made modern, so that using that JTable becomes simple. 
What if creating a table with sorting, filtering, automatic updates of cells when values change only took a few lines of code? 

``` java
var sTable = new STable<City>() //
        .columns(City.class, "name", "distance", "roundtrip") //
        .data(cities); // cities is a List<City>
```

Or creating a strongly typed text field the is bound to the property of a Java bean would be as simple as:

``` java
var sTextField = STextField.ofString();
sTextField.value$().bindTo(city.name$());

// Or a bit compact; value is STextField's default property 
var sTextField = STextField.ofString().bindTo(city.name$());

// Or even more compact
var sTextField = STextField.ofBindTo(city.name$());
```

Well, you've come to the right place. 
Take a look at the examples here, and the JavaDoc present in org.tbee.sway, and see if they are easy enough to use.
A UI library probably is never truly finished, but Sway is stable and used in production.


``` java
// A label can have its text and icon property bound
var sLabel = new Slabel();
sLabel.text$().bindTo(city.name$());

// SCheckBox can be bound to a boolean property
var sCheckBox = new SCheckBox("Growing").bindTo(city.growing$());
// SCheckBox3 can be bound to a Boolean property, supporting 3 states: TRUE, FALSE, NULL
var sCheckBox = new SCheckBox3("Cityrights").bindTo(city.cityrights$());

// Add a list
var sList = new SList<City>() //
        .render(new CityFormat(cities))
        .selectionMode(SList.SelectionMode.MULTIPLE)        
        .data(cities); // cities is a List<City>
        
// Bind the selection of the list to that of the table created above        
sList.selection$().bindTo(sTable.selection$());

// SButtonGroup revolves around the associated value, not the button
var sButtonGroup = new SButtonGroup<Integer>() //
        .add(1, new SToggleButton("winner")) //
        .add(2, new SToggleButton("2nd")) //
        .add(3, new SToggleButton("3rd")) //
        .bindTo(race.position$());
var sPanel = new SFlowPanel(sButtonGroup.getButtons());

// SButtonGroup has some practical convenience methods
var sButtonGroupCities = SButtonGroup.ofRadioButtons(amsterdam, berlin, rome);
var sPanelCities = new SFlowPanel(sButtonGroupCities.getButtons());

// Explicit panels for layouts, with corresponding methods.
var sBorderPanel = new SBorderPanel(new STable()) //
         .west(new SomeNavigationMenu()) //
         .east(new SomeContextLinks());
         
// MigLayout is used by Sway anyhow         
var migPanel = new MigPanel().fill(); //
migPanel.addLabelAndField(someLabel, someField).growX();
migPanel.wrap();         
```

## Format
In order to not have to repeat the same formatting over and over again, Sway has a FormatRegistry.

A simple example:

``` java
// Only two methods need to be implemented, the rest are optional
public class LongFormat implements Format<Long> {

    @Override
    public String toString(Long value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public Long toValue(String string) {
        return string.isBlank() ? null : Long.parseLong(string);
    }
} 

// Register the format once to use it in many components
FormatRegistry.register(Long.class, new LongFormat()); // Formats must be stateless and thread safe.   
```

The FormatRegisty is used by all components, STextField, STable, SButtonGroup, ..., so it is only necessary to register a format once.
This also is true for domain entities, like for example a "City" or "Employee".


Format also allows to define things like horizontal alignment and icon.


## Actions / context menu
Actions can be registered and will be shown in the (right mouse button) context menu.
These can be simple actions, like the default available "copy", "cut" and "paste", but also business model related actions.

``` java
ActionRegistry.register(new ShowOnMapForCityTextFields());
SContextMenu.install();

public class ShowOnMapForCityTextFields implements Action {

    @Override
    public String label() {
        return "Show on map";
    }

    @Override
    public boolean isApplicableFor(Component component, Map<String, Object> context) {
        return component instanceof STextField sTextField
            && sTextField.getFormat() instanceof CityFormat;
    }

    @Override
    public void apply(Component component, Map<String, Object> context) {
        STextField<City> sTextField = (STextField<City>)component;
        City city = sTextField.getValue();
        ... // show city on map
    }
}
```

## Bean generator
Sway includes a bean generator.
This means you can have a class with only data (instance variables) and using some annotations all the methods are generated.

``` java
// This will generate a class "City extends CityData" 
// With a "name" property (setName, getName, name$(), etc).
@Bean(stripSuffix = "Data")
abstract public class CityData extends AbstractBean<City> {
    @Property
    String name;
}
```

The data class needs to extend AbstractBean in order to provide the expected property change methods.
Or you will need to add them manually.

Things become interesting when custom logic needs to be added. 
It is very likely the custom code needs to call a setter in order to send the appropriated events for binding.
For this the bean generator supports a dual and triple stack approach.

The triple stack is the most straight forward approach, with three classes, 
where the middle one contains the generated code. 

``` java
// Contains the data
@Bean(stripSuffix = "Data", appendSuffixToBean = "Bean")
abstract public class TripleStackData extends AbstractBean<TripleStack> {
    @Property
    String name;
}

// Contains the generated code
public class TripleStackBean extends TripleStackData {
    public String getName() {...}
    public void setName(String name) {...}
    public TripleStack name(String v) {...}
}

// Contains the custom code 
public class TripleStack extends TripleStackBean {
    public void custom() {
        setName("custom");
    }
}
```

The triple stack approach requires many classes, ideally one would want to have data and custom code in one class. 
This is where the double stack comes in. 
It uses a small trick to be able to access the generated methods from the data class, using a "self" variable.

``` java
// Contains both data and custom code
@Bean(stripSuffix = "Data")
abstract public class DoubleStackData extends AbstractBean<DoubleStack> {
    @Property
    String name;
    
    private final DoubleStack self = (DoubleStack)this;
    
    public void custom() {
        self.setName("custom");
    }
}

// Contains the generated code
public class DoubleStack extends DoubleStackData {
    public String getName() {...}
    public void setName(String name) {...}
    public DoubleStack name(String v) {...}
}
```

Using self seems like a small price to pay for not having to write all the JavaBean methods manually.
But that is a personal opinion.

The bean generator uses compiler annotations, so it will automatically be picked up by a build tool like Maven.
IDEs usually need to have compiler annotations activated.


## Compatibility
The components are still the standard Swing components, only with an opinionated API, but they should blend-in nicely in existing applications.

And as long as you stick to Sway's API upgrading should not be too much of a hassle. 
If you start poking around, you're on your own ;-)

## Support
There is no formal support for Sway: this library is an open source hobby project and no claims can be made. 
Asking for help is always an option. But so is participating, creating pull requests, and other ways of contributing.

## Usage
Just include a dependency in your project. For the latest version see [Maven central](https://central.sonatype.com/namespace/org.tbee.sway)

```xml
<dependency>
    <groupId>org.tbee.sway</groupId>
    <artifactId>sway</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Third party
* Binding uses Karsten Lentzsch's JGoodies underneath (https://www.jgoodies.com/freeware/libraries/binding/).
* STable's filter header is provided by Coderazzi (https://coderazzi.net/tablefilter/).
* STable's navigation bar is based on CoMedia's implementation (project is no longer available online).
* Mikael Grev's excelent MigLayout is used for several layouts (https://www.miglayout.com/).

And if icons are needed in an application Ikonli is highly suggested! (https://kordamp.org/ikonli/)

