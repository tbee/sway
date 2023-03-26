package org.tbee.sway;

import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.swing.FontIcon;
import org.tbee.sway.format.Format;

import javax.swing.Icon;
import java.util.List;

public class CityFormat implements Format<City> {

    final private List<City> cities;
    public CityFormat() {
        this(List.of());
    }

    public CityFormat(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public String toString(City value) {
        return value == null ? "" : value.getName();
    }

    @Override
    public Icon toIcon(City value) {
        if (cities.isEmpty()) {
            return null;
        }
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIkon(MaterialDesignC.values()[cities.indexOf(value)]);
        fontIcon.setIconSize(16);
        return fontIcon;
    }

    @Override
    public City toValue(String string) {
        return string.isBlank() ? null : cities.stream().filter(c -> c.getName().equals(string)).findFirst().orElseThrow(() -> new IllegalArgumentException("No city found with that name: " + string));
    }
}
