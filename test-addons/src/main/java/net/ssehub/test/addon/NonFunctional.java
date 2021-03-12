package net.ssehub.test.addon;

import net.ssehub.jacat.api.addon.Addon;

public class NonFunctional extends Addon {

    @Override
    public void onEnable() {
        throw new RuntimeException();
    }
}
