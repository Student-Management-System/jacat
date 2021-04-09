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
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void setUp() throws IOException, URISyntaxException {
        temp = Files.createTempDirectory("addonLoaderTest").toAbsolutePath();
        this.prepareTestFolder(temp);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(temp)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void newAddonLoader_withPath_loadsAddons() {
        AddonLoader loader = new AddonLoader(temp, null);
        assertTrue(loader.isLoaded("net.ssehub.test.addon.Functional"));
    }

    @Test
    void loadAddon_withValidPath_loadsOneAddon() throws URISyntaxException {
        AddonLoader loader = new AddonLoader(null, null);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.Functional"));

        loader.loadAddon(getTestAddonJar(), A_FUNCTIONAL_DESCRIPTION);

        assertTrue(loader.isLoaded("net.ssehub.test.addon.Functional"));
    }

    @Test
    void loadAddon_withNonFunctionalAddon_doesntLoadAddon() throws URISyntaxException {
        AddonLoader loader = new AddonLoader(null, null);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.NonFunctional"));
        loader.loadAddon(getTestAddonJar(), A_NON_FUNCTIONAL_DESCRIPTION);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.NonFunctional"));
    }

    @Test
    void loadAddon_withInvalidPath_doesntLoadAddon() {
        AddonLoader loader = new AddonLoader(null, null);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.Functional"));

        loader.loadAddon(temp.resolve("A_NON_EXISTING_PATH").toFile(), A_FUNCTIONAL_DESCRIPTION);

        assertFalse(loader.isLoaded("net.ssehub.test.addon.Functional"));
    }

    @Test
    void loadAddonsFolder_withInvalidPath_doesntLoadAddon() {
        AddonLoader loader = new AddonLoader(null, null);

        assertEquals(0, loader.getLoadedAddons().size());

        loader.loadAddonFolder(temp.resolve("A_NON_EXISTING_PATH").toFile());

        assertEquals(0, loader.getLoadedAddons().size());
    }

    @Test
    void loadAddonsFolder_withNullFolder_doesNotLoadAddons() {
        AddonLoader loader = new AddonLoader(null, null);
        assertEquals(0, loader.getLoadedAddons().size());

        loader.loadAddonFolder(null);
        assertEquals(0, loader.getLoadedAddons().size());
    }

    @Test
    void loadAddonsFolder_withFileFolder_doesNotLoadAddons() throws IOException {
        AddonLoader loader = new AddonLoader(null, null);
        assertEquals(0, loader.getLoadedAddons().size());

        File file = temp.resolve("myfolderasa.file").toFile();
        file.createNewFile();

        loader.loadAddonFolder(file);
        assertEquals(0, loader.getLoadedAddons().size());
    }

    @Test
    void loadAddonsFolder_withSubfolders_doesNotLoadSubfolder() {
        temp.resolve("addons").resolve("subfolder1").toFile().mkdirs();

        AddonLoader loader = new AddonLoader(temp, null);

        assertEquals(1, loader.getLoadedAddons().size());
        System.out.println(temp.toAbsolutePath());

    }

    private void prepareTestFolder(Path workspaceFolder) throws IOException, URISyntaxException {
        File addons = workspaceFolder.resolve("addons").toFile();
        addons.mkdirs();

        Files.copy(getTestAddonJar().toPath(), workspaceFolder.resolve("addons").resolve("test-addons.jar"));
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