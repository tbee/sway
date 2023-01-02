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
Take a look at the components amd their JavaDoc present in org.tbee.sway and see if they are easy enough to use.
Sway is not there yet, but the idea of what it is trying to do should be clear.

Some more examples:

``` java
var sLabel = new Slabel().bindText(city, "name");

var sButtonGroup = new SButtonGroup<Integer>() //
        .add(1, new SToggleButton("1")) //
        .add(2, new SToggleButton("2")) //
        .add(3, new SToggleButton("3")) //
        .bind(race, "postion");
var sPanel = new SFlowPanel(sButtonGroup.getButtons());

var sButtonGroupCities = new SButtonGroup.ofRadioButtons(amsterdam, berlin, rome);

var sBorderPanel = new SBorderPanel(new STable()) //
         .west(new SomeNavigationMenu()) //
         .east(new SomeContextLinks());
         
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

