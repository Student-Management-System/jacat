package net.ssehub.jacat.worker.addon;

import net.ssehub.jacat.api.addon.AddonDescription;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddonLoaderTest {

    private Path temp;

    public static final AddonDescription A_FUNCTIONAL_DESCRIPTION = new AddonDescription(
        "net.ssehub.test.addon.Functional",
        "test");
    public static final AddonDescription A_NON_FUNCTIONAL_DESCRIPTION = new AddonDescription(
        "net.ssehub.test.addon.NonFunctional",
        "test");

    @BeforeEach
    void setUp() throws IOException {
        temp = Files.createTempDirectory("addonLoaderTest").toAbsolutePath();
    }

    @AfterEach
    void tearDown() {
        temp.toFile().delete();
    }

    @Test
    void newAddonLoader_withPath_loadsAddons() throws URISyntaxException {
        AddonLoader loader = new AddonLoader(getTestAddonsFolder(), null);
        assertTrue(loader.isLoaded("net.ssehub.test.addon.Functional"));
    }

    @Test
    void loadAddon_withValidPath_loadsOneAddon() throws URISyntaxException {
        AddonLoader loader = new AddonLoader(temp, null);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.Functional"));

        loader.loadAddon(getTestAddonJar(), A_FUNCTIONAL_DESCRIPTION);

        assertTrue(loader.isLoaded("net.ssehub.test.addon.Functional"));
    }

    @Test
    void loadAddon_withNonFunctionalAddon_doesntLoadAddon() throws URISyntaxException {
        AddonLoader loader = new AddonLoader(temp, null);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.NonFunctional"));
        loader.loadAddon(getTestAddonJar(), A_NON_FUNCTIONAL_DESCRIPTION);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.NonFunctional"));
    }

    @Test
    void loadAddon_withInvalidPath_doesntLoadAddon() {
        AddonLoader loader = new AddonLoader(temp, null);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.Functional"));

        loader.loadAddon(null, A_FUNCTIONAL_DESCRIPTION);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.Functional"));
    }

    private Path getTestAddonsFolder() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("addons/test-addons.jar");
        if (resource == null) {
            throw new IllegalArgumentException("Path not found!");
        } else {
            return Path.of(resource.toURI()).getParent().getParent();
        }
    }

    private File getTestAddonJar() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("addons/test-addons.jar");
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            return new File(resource.toURI());
        }
    }
}