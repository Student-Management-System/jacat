package net.ssehub.jacat.api.addon;

import java.util.Objects;
import net.ssehub.jacat.api.AbstractJacatWorker;
import org.slf4j.Logger;

/**
 * Diese Klasse beschreibt wie ein Addon funktioniert.
 * Ein Addon sollte diese Klasse erweitern und implementieren,
 * sodass dieses von der Plattform gestartet werden kann.
 */
public abstract class Addon {
    private AbstractJacatWorker worker;
    private AddonDescription description;
    private Logger logger;

    /**
     * Diese Methode wird zur Initialisierung des Addons genutzt.
     * Hier müssen alle möglichen Einstellungsaufgaben getätigt
     * werden. Erst dann wird das Addon als funktionsfähig angesehen.
     */
    public abstract void onEnable();

    /**
     * Gibt die JacatPlatform zurück. Hierrüber kann das Addon mit
     * der Analyseplattform interagieren.
     * @return Laufende JacatPlatform
     */
    public AbstractJacatWorker getWorker() {
        return worker;
    }

    /**
     * Die {@link AddonDescription} beinhaltet alle Informationen,
     * die in der 'addon.yml' angegeben wurden.
     * @return Die AddonDescription des aktuellen Addons
     */
    public AddonDescription getDescription() {
        return description;
    }

    /**
     * Jedes Addon besitzt einen SLF4J-Logger
     * @return Konfigurierten SLF4J-Logger
     */
    public Logger getLogger() {
        return logger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Addon addon = (Addon) o;
        return description.equals(addon.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }
}
