package net.ssehub.jacat.addon.pp1plag;

import net.ssehub.jacat.api.addon.Addon;

import java.nio.file.Path;

public class Main extends Addon {

    @Override
    public void onEnable() {
        Path workdir = this.getWorker().getWorkingDir();

        this.getWorker().registerAnalysisCapability(this, new JPlagAnalyzer(workdir));
        this.getLogger().info("JPlag Analyzer successfully loaded");
    }

}
