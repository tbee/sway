package org.tbee.sway;

import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.swing.FontIcon;
import org.tbee.sway.format.Format;

import javax.swing.Icon;
import java.util.List;

public class StreetFormat implements Format<Street> {

    final private List<Street> streets;
    public StreetFormat() {
        this(List.of());
    }

    public StreetFormat(List<Street> streets) {
        this.streets = streets;
    }

    @Override
    public String toString(Street value) {
        return value == null ? "" : value.getName();
    }

    @Override
    public Icon toIcon(Street value) {
        if (streets.isEmpty()) {
            return null;
        }
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIkon(MaterialDesignC.values()[value.getName().charAt(0)]);
        fontIcon.setIconSize(16);
        return fontIcon;
    }

    @Override
    public Street toValue(String string) {
        return string.isBlank() ? null : streets.stream().filter(c -> c.getName().equals(string)).findFirst().orElseThrow(() -> new IllegalArgumentException("No street found with that name: " + string));
    }
}
