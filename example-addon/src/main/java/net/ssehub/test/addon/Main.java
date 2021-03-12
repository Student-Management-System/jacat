package net.ssehub.test.addon;

import net.ssehub.jacat.api.addon.Addon;

public class Main extends Addon {

    @Override
    public void onEnable() {
        this.getJacatPlatform().registerAnalysisTask(this, new JavaHintingAnalysisCapability());
        this.getJacatPlatform().registerAnalysisTask(this, new CPPHintingAnalysisCapability());

        this.getJacatPlatform().registerDataCollector(this, new StaticFolderDataCollector());
        this.getLogger().info("Addon ist da: " + this.getJacatPlatform().getVersion());
    }
}
