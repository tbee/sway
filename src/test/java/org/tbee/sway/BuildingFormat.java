package org.tbee.sway;

import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.swing.FontIcon;
import org.tbee.sway.format.Format;

import javax.swing.Icon;
import java.util.List;

public class BuildingFormat implements Format<Building> {

    final private List<Building> buildings;
    public BuildingFormat() {
        this(List.of());
    }

    public BuildingFormat(List<Building> buildings) {
        this.buildings = buildings;
    }

    @Override
    public String toString(Building value) {
        return value == null ? "" : ""  + value.getNumber();
    }

    @Override
    public Icon toIcon(Building value) {
        if (buildings.isEmpty()) {
            return null;
        }
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIkon(MaterialDesignC.values()[value.getNumber()]);
        fontIcon.setIconSize(16);
        return fontIcon;
    }

    @Override
    public Building toValue(String string) {
        int i = Integer.parseInt(string);
        return string.isBlank() ? null : buildings.stream().filter(c -> c.getNumber() == i).findFirst().orElseThrow(() -> new IllegalArgumentException("No building found with that number: " + string));
    }
}
