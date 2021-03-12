package net.ssehub.jacat.addon.svndatacollector;

import net.ssehub.jacat.api.addon.Addon;

public class Main extends Addon {

    @Override
    public void onEnable() {
        this.getJacatPlatform()
                .registerDataCollector(this, new SVNDataCollector(this.getLogger()));
    }

}
