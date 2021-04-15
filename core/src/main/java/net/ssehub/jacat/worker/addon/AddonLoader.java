package net.ssehub.jacat.worker.addon;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.AddonDescription;
import net.ssehub.jacat.worker.JacatWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AddonLoader {
    private JacatWorker jacatPlatform;

    private List<Addon> loadedAddons;

    public AddonLoader(@Qualifier("workdir") Path workdir,
                       JacatWorker jacatPlatform) {
        this.jacatPlatform = jacatPlatform;
        this.loadedAddons = new ArrayList<>();

        if (workdir != null) {
            loadAddonFolder(workdir.resolve("addons").toFile());
        }
    }

    void loadAddonFolder(File folder) {
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

    boolean isLoaded(String javaClass) {
        return loadedAddons.stream()
            .map(addon -> addon.getDescription().getMainClass())
            .collect(Collectors.toList())
            .contains(javaClass);
    }

    List<Addon> getLoadedAddons() {
        return new ArrayList<>(this.loadedAddons);
    }

    void loadAddon(File file, AddonDescription addonDescription) {
        if (file == null) {
            return;
        }

        try {
            AddonClassLoader addonClassLoader =
                new AddonClassLoader(file,
                    addonDescription,
                    this.jacatPlatform,
                    getClass().getClassLoader());
            this.loadedAddons.add(addonClassLoader.getLoadedAddon());
        } catch (AddonNotLoadableException | MalformedURLException e) {
            log.error(
                "Could not load addon: " +
                    addonDescription.getName() +
                    " - " +
                    file.getAbsolutePath()
            );
            String message = e.getCause().getMessage();
            if (message == null) {
                message = e.getMessage();
            }
            log.error("Cause: " + message);
        }
    }

    private AddonDescription getDescription(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (
                    entry.getName().equalsIgnoreCase("addon.yml") ||
                        entry.getName().equalsIgnoreCase("addon.yaml")
                ) {
                    InputStream input = jarFile.getInputStream(entry);
                    Yaml yaml = new Yaml();
                    Map<String, Object> obj = yaml.load(input);

                    return new AddonDescription(
                        (String) obj.get("main"),
                        (String) obj.get("name")
                    );
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
