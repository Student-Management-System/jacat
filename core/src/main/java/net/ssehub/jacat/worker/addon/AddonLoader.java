package net.ssehub.jacat.worker.addon;

import net.ssehub.jacat.api.addon.AddonDescription;
import net.ssehub.jacat.worker.JacatWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Component
public class AddonLoader {

    private JacatWorker jacatPlatform;
    private AddonManager addonManager;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public AddonLoader(JacatWorker jacatPlatform, AddonManager addonManager) {
        this.jacatPlatform = jacatPlatform;
        this.addonManager = addonManager;
        loadAddonFolder(new File("./debug/addons"));
    }

    public void loadAddonFolder(File folder) {
        if (folder == null || !folder.isDirectory()) {
            return;
        }

        for (File addonFile : folder.listFiles()) {
            if (!addonFile.isFile()) {
                continue;
            }

            AddonDescription addonDescription = getDescription(addonFile);
            if (addonDescription != null) {
                loadAddon(addonFile, addonDescription);
            }
        }
    }

    public void loadAddon(File file, AddonDescription addonDescription) {
        if (file == null) {
            return;
        }

        try {
            AddonClassLoader classLoader = new AddonClassLoader(file, addonDescription, this.jacatPlatform);
            this.addonManager.addAddon(classLoader.getLoadedAddon());
        } catch (AddonNotLoadableException | MalformedURLException e) {
            this.logger.error("Could not load addon: " + addonDescription.getName() + " - " + file.getAbsolutePath());
            this.logger.error("Cause: " + e.getMessage());
        }
    }

    private AddonDescription getDescription(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (entry.getName().equalsIgnoreCase("addon.yml")
                        || entry.getName().equalsIgnoreCase("addon.yaml")) {
                    InputStream input = jarFile.getInputStream(entry);
                    Yaml yaml = new Yaml();
                    Map<String, Object> obj = yaml.load(input);

                    return new AddonDescription((String) obj.get("main"),
                            (String) obj.get("name"));
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

}
