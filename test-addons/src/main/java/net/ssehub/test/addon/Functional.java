package net.ssehub.test.addon;

import net.ssehub.jacat.api.addon.Addon;

public class Functional extends Addon {

    @Override
    public void onEnable() {
        this.getLogger().info("Test Addon geladen");
    }
}
