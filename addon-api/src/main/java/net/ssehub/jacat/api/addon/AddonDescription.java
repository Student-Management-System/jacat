package net.ssehub.jacat.api.addon;

import java.util.Objects;

/**
 * Datenklasse für die Beschreibung eines Addons.
 * In dieser Klasse wird die Hauptklasse und der Name gespeichert.
 * Es wird von der Plattform automatisch ein Objekt von dieser
 * Klasse angelegt, sodass ein Addon hiermit arbeiten kann.
 */
public class AddonDescription {
    private final String mainClass;
    private final String name;

    public AddonDescription(String mainClass, String name) {
        this.mainClass = mainClass;
        this.name = name;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddonDescription that = (AddonDescription) o;
        return mainClass.equals(that.mainClass) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainClass, name);
    }
}
