package net.ssehub.jacat.worker.addon;

import net.ssehub.jacat.api.addon.Addon;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddonManager {

    private List<Addon> addons;

    public AddonManager() {
        this.addons = new ArrayList<>();
    }

    public void addAddon(Addon addon) {
        if (addon == null) {
            return;
        }

        this.addons.add(addon);
    }

    public boolean isEnabled(Addon addon) {
        return this.addons.contains(addon);
    }

    @Override
    public String toString() {
        return "AddonManager{" +
                "addons=" + addons +
                '}';
    }
}
