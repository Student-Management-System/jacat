package net.ssehub.jacat.worker.analysis;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.analysis.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.task.FinishedTask;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisCapabilitiesTest {

    private static final String A_VALID_SLUG = "A_VALID_SLUG";
    private static final String A_VALID_LANGUAGE = "A_VALID_LANGUAGE";
    public static final String AN_INVALID_LANGUAGE = "AN_INVALID_LANGUAGE";
    public static final String AN_INVALID_SLUG = "AN_INVALID_SLUG";

    private AnalysisCapabilities capabilities;

    private static final AbstractAnalysisCapability A_WORKING_CAPABILITY = new AbstractAnalysisCapability(
            A_VALID_SLUG,
            Collections.singletonList(A_VALID_LANGUAGE),
            0 ) {
        @Override
        public FinishedTask run(PreparedTask task) {
            return null;
        }
    };

    private static final Addon A_WORKING_ADDON = new Addon(){
        @Override
        public void onEnable() {

        }
    };

    @BeforeEach
    void setUp() {
        this.capabilities = new AnalysisCapabilities();
    }

    @Test
    void register_withValidAddonAndCapability_registersCapability() {
        capabilities.register(A_WORKING_ADDON, A_WORKING_CAPABILITY);

        assertTrue(capabilities.isRegistered(A_VALID_SLUG, A_VALID_LANGUAGE));
    }

    @Test
    void register_withInvalidAddonAndValidCapability_doesntRegisterCapability() {

        capabilities.register(null, A_WORKING_CAPABILITY);

        assertFalse(capabilities.isRegistered(A_VALID_SLUG, A_VALID_LANGUAGE));
    }

    @Test
    void register_withValidAddonAndInvalidCapability_doesntRegisterCapability() {
        capabilities.register(A_WORKING_ADDON, null);

        assertFalse(capabilities.isRegistered(A_VALID_SLUG, A_VALID_LANGUAGE));
    }

    @Test
    void register_withInvalidAddonAndCapability_doesntRegisterCapability() {
        capabilities.register(null, null);
        assertFalse(capabilities.isRegistered(A_VALID_SLUG, A_VALID_LANGUAGE));
    }

    @Test
    void getCapability_withRegisteredCapability_shouldReturnCapability() {
        capabilities.register(A_WORKING_ADDON, A_WORKING_CAPABILITY);

        assertEquals(A_WORKING_CAPABILITY, capabilities.getCapability(A_VALID_SLUG, A_VALID_LANGUAGE));
    }

    @Test
    void getCapability_withRegisteredCapabilityButWrongLanguage_shouldThrowException() {
        capabilities.register(A_WORKING_ADDON, A_WORKING_CAPABILITY);

        assertThrows(IAnalysisCapabilities.CapabilityNotFoundException.class, () -> {
            capabilities.getCapability(A_VALID_SLUG, AN_INVALID_LANGUAGE);
        });
    }

    @Test
    void getCapability_withRegisteredCapabilityButWrongSlug_shouldThrowException() {
        capabilities.register(A_WORKING_ADDON, A_WORKING_CAPABILITY);

        assertThrows(IAnalysisCapabilities.CapabilityNotFoundException.class, () -> {
            capabilities.getCapability(AN_INVALID_SLUG, A_VALID_LANGUAGE);
        });
    }

    @Test
    void getCapabilityHolder_withRegisteredCapability_shouldReturnAddon() {
        capabilities.register(A_WORKING_ADDON, A_WORKING_CAPABILITY);
        assertEquals(A_WORKING_ADDON, capabilities.getCapabilityHolder(A_VALID_SLUG, A_VALID_LANGUAGE));
    }

    @Test
    void getCapabilityHolder_withRegisteredCapabilityButWrongSlug_shouldThrowException() {
        capabilities.register(A_WORKING_ADDON, A_WORKING_CAPABILITY);

        assertThrows(IAnalysisCapabilities.CapabilityNotFoundException.class, () -> {
            capabilities.getCapabilityHolder(AN_INVALID_SLUG, A_VALID_LANGUAGE);
        });
    }

    @Test
    void getCapabilityHolder_withRegisteredCapabilityButWrongLanguage_shouldThrowException() {
        capabilities.register(A_WORKING_ADDON, A_WORKING_CAPABILITY);

        assertThrows(IAnalysisCapabilities.CapabilityNotFoundException.class, () -> {
            capabilities.getCapabilityHolder(A_VALID_SLUG, AN_INVALID_LANGUAGE);
        });
    }
}