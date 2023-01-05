# Sway
A more modern style API for the Java Swing UI framework, focused on ease of use.

Let's be honest, coding for Swing is not easy. And JTable is probably the epiphany;
you have to understand TableModel, manually add sorting, filtering, the events that trigger updates, etc. 
What if the Swing API would be revisited and made modern, so that using that JTable becomes simple. 
What if creating a table with sorting, filtering, automatic updates of cells when values change only took a few lines of code? 

``` java
var sTable = new STable<City>() //
        .columns(City.class, "name", "distance", "roundtrip") //
        .filterHeaderEnabled(true) //
        .data(cities); // cities is a List<City>
```

Or creating a strongly typed text field would be as simple as:

``` java
var sTextField = STextField.ofBind(city, "name");
```

Well, you've come to the right place. 
Take a look at the components and their JavaDoc present in org.tbee.sway and see if they are easy enough to use.
Sway is not there yet, but the idea of what it is trying to do should be clear.

Some more examples:

``` java
// A label can have its text and icon property bound
var sLabel = new Slabel().bindText(city, "name");

// ButtonGroup revolves around the associated value, not the button
var sButtonGroup = new SButtonGroup<Integer>() //
        .add(1, new SToggleButton("1")) //
        .add(2, new SToggleButton("2")) //
        .add(3, new SToggleButton("3")) //
        .bind(race, "postion");
var sPanel = new SFlowPanel(sButtonGroup.getButtons());

// ButtonGroup has some practical convenience methods
var sButtonGroupCities = SButtonGroup.ofRadioButtons(amsterdam, berlin, rome);
var sPanelCities = new SFlowPanel(sButtonGroupCities.getButtons());

// Explicit panels for layouts, with correcsponding methods.
var sBorderPanel = new SBorderPanel(new STable()) //
         .west(new SomeNavigationMenu()) //
         .east(new SomeContextLinks());
         
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
Actions can be registered and these will be shown in the (right mouse button) context menu.
These can be simple actions, like the default availabe "copy", "cut" and "paste", but also business model related actions.

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
        if (!component instanceof STextField) {
            return false;
        }
        return ((STextField)component).getFormat() instanceof CityFormat;
    }

    @Override
    public void apply(Component component, Map<String, Object> context) {
        STextField<City> sTextField = (STextField<City>)component;
        City city = sTextField.getValue();
        ... // show city on map
    }
}
```

## Compatibility
The components are still the standard Swing components, only with an opinionated API, but they should blend-in nicely in existing applications.

And as long as you stick to Sway's API upgrading should not be too much of a hassle. 
If you start poking around, you're on your own ;-)

## Support
There is no formal support for Sway: this library is an open source hobby project and no claims can be made. 
Asking for help is always an option. But so is participating, creating pull requests, and other ways of contributing.

## Third party
* Binding uses Karsten Lentzsch's JGoodies underneath (https://www.jgoodies.com/freeware/libraries/binding/).
* STable's filter header is provided by Coderazzi (https://coderazzi.net/tablefilter/).
* STable's navigation bar is based on CoMedia's implementation (project is no longer available online).
* Mikael Grev's excelent MigLayout is used for several layouts (https://www.miglayout.com/).

