package net.ssehub.jacat.worker.addon;

import net.ssehub.jacat.api.addon.AddonDescription;
import net.ssehub.jacat.worker.JacatWorker;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class AddonClassLoaderTest {

    @Test
    void newAddonClassLoader_withValidSimpleAddon_loadsAddon() {
        assertDoesNotThrow(() -> {
            AddonClassLoader addonClassLoader = new AddonClassLoader(getTestAddonJar(),
                    new AddonDescription("net.ssehub.test.addon.Functional", "TestAddon"),
                    new JacatWorker(null, null));

            assertNotNull(addonClassLoader.getLoadedAddon());
        });
    }

    @Test
    void newAddonClassLoader_withInvalidSimpleAddon_doesNotLoadAddon() {
        assertThrows(AddonNotLoadableException.class, () -> {
            AddonClassLoader addonClassLoader = new AddonClassLoader(getTestAddonJar(),
                    new AddonDescription("net.ssehub.test.addon.NonFunctional", "TestAddon"),
                    new JacatWorker(null, null));
        });

    }

    private File getTestAddonJar() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("test-addons.jar");
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            return new File(resource.toURI());
        }
    }
}