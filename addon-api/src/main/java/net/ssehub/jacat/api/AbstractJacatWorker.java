package net.ssehub.jacat.api;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.analysis.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.addon.result.AbstractResultProcessor;
import net.ssehub.jacat.api.studmgmt.IStudMgmtClient;
import net.ssehub.jacat.api.studmgmt.IStudMgmtFacade;

import java.nio.file.Path;

/**
 * Diese Klasse beschreibt die Möglichkeiten auf einer
 * Analyse Plattform, die eine Erweiterung (Addon) hat.
 * <p>
 * Über diese Klasse sollen alle Steuerungen vorgenommen
 * werden, die für eine Erweiterung wichtig sein könnten.
 * Beispielsweise werden hier die Analyseaufgaben registriert
 * oder die Datacollector festgelegt.
 */
public abstract class AbstractJacatWorker {

    /**
     * Gibt die aktuelle Version des Workers zurück.
     *
     * @return Version, mit der der Worker läuft
     */
    public abstract String getVersion();

    /**
     * Diese Methode registriert ein Addon mit einer
     * Analysefähigkeit.
     *
     * @param addon      Muss das laufende Addon sein
     * @param capability Beschreibt die Fähigkeit, die eine
     *                   Analyse besitzt. Wenn eine Analyse
     *                   ansteht, wird ein entsprechendes
     *                   Addon dafür benachrichtigt.
     */
    public abstract void registerAnalysisCapability(
        Addon addon,
        AbstractAnalysisCapability capability
    );

    public abstract void registerDataCollector(
        Addon addon,
        AbstractDataCollector collector
    );

    public abstract void registerResultProcessor(
        Addon addon,
        AbstractResultProcessor processor
    );

    public abstract IStudMgmtClient getStudMgmtClient();

    public abstract IStudMgmtFacade getStudMgmtFacade();

    public abstract Path getWorkingDir();

}
