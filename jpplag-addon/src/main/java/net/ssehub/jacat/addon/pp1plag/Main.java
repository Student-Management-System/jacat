package net.ssehub.jacat.addon.pp1plag;

import net.ssehub.jacat.api.addon.Addon;

public class Main extends Addon {

    @Override
    public void onEnable() {
        this.getJacatPlatform().registerAnalysisTask(this, new JPlagAnalyzer());
        this.getLogger().info("JPlag Analyzer successfully loaded");
    }

}
