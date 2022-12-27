package org.tbee.sway;

import org.tbee.sway.format.Format;

import java.util.List;

public class CityFormat implements Format<City> {

    final private List<City> cities;
    public CityFormat(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public String toString(City value) {
        return value == null ? "" : value.getName();
    }

    @Override
    public City toValue(String string) {
        return string.isBlank() ? null : cities.stream().filter(c -> c.getName().equals(string)).findFirst().orElseThrow(() -> new IllegalArgumentException("No city found with that name: " + string));
    }
}
