# Sway
A more modern style API for the Java Swing UI framework, focused on ease of use.

Let's be honest, coding for Swing is not easy. And JTable is probably the epiphany;
you have to understand TableModel, manually add sorting, filtering, the events that trigger updates, etc. 
What if the Swing API would be revisited and made modern, so that using that JTable becomes simple. 
What if creating a table with sorting, filtering, automatic updates of cells when values change only took three lines of code? 

``` java
var sTable = new STable<City>() //
        .columns(City.class, "name", "distance", "roundtrip") // probably should use constants for these
        .data(cities); // cities is a List<City>
```

Or creating a strongly typed text field would be as simple as:

``` java
var sTextField = STextField.ofBind(city, "name");
```

Well, you've come to the right place. 
Take a look at the components present in org.tbee.sway and see if they are easy enough to use.

## Compatibility
The components are still the standard Swing components, only with an opinionated API, but they should blend-in nicely in existing applications.