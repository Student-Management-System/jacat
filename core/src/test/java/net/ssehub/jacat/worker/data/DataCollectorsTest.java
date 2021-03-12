package net.ssehub.jacat.worker.data;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.addon.data.DataRequest;
import net.ssehub.jacat.api.addon.data.SubmissionCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataCollectorsTest {

    public static final String A_NOT_AVAILABLE_COLLECTOR_PROTOCOL = "A_NOT_AVAILABLE_COLLECTOR_PROTOCOL";
    public static final String AN_AVAILABLE_COLLECTOR_PROTOCOL = "AN_AVAILABLE_COLLECTOR_PROTOCOL";
    private DataCollectors dataCollectors;

    @BeforeEach
    void setUp() {
        dataCollectors = new DataCollectors();
    }

    @Test
    void getCollector_withNoCollector_shouldThrowException() {
        assertThrows(DataCollectors.DataCollectorNotFoundException.class, () -> {
            dataCollectors.getCollector(A_NOT_AVAILABLE_COLLECTOR_PROTOCOL);
        });
        assertEquals(0, dataCollectors.getCollectors().size());
    }

    @Test
    void isRegistered_withNoCollector_shouldReturnFalse() {
        assertFalse(dataCollectors.isRegistered(A_NOT_AVAILABLE_COLLECTOR_PROTOCOL));
        assertEquals(0, dataCollectors.getCollectors().size());
    }

    @Test
    void isRegistered_withCollector_shouldReturnTrue() {
        registerAvailableCollector();

        assertTrue(dataCollectors.isRegistered(AN_AVAILABLE_COLLECTOR_PROTOCOL));
        assertEquals(1, dataCollectors.getCollectors().size());
    }

    @Test
    void register_withAddonAndDataCollector_shouldRegister() {
        String a_protocol = "A_PROTOCOL";

        dataCollectors.register(createSimpleAddon(), createCollectorWithProtocol(a_protocol));

        assertTrue(dataCollectors.isRegistered(a_protocol));
        assertEquals(1, dataCollectors.getCollectors().size());
    }

    @Test
    void register_withNullAddon_shouldThrowException() {
        String a_protocol = "A_PROTOCOL";

        assertThrows(NullPointerException.class,
                () -> dataCollectors.register(null,
                        createCollectorWithProtocol(a_protocol))
        );

        assertFalse(dataCollectors.isRegistered(a_protocol));
        assertEquals(0, dataCollectors.getCollectors().size());
    }

    private AbstractDataCollector createCollectorWithProtocol(String a_protocol) {
        return new AbstractDataCollector(a_protocol) {
            @Override
            public void arrange(DataRequest request) {

            }

            @Override
            public SubmissionCollection collect(DataRequest request) {
                return null;
            }
        };
    }

    private void registerAvailableCollector() {
        dataCollectors.register(createSimpleAddon(), createCollectorWithProtocol(AN_AVAILABLE_COLLECTOR_PROTOCOL));
    }

    private Addon createSimpleAddon() {
        return new Addon() {
            @Override
            public void onEnable() {

            }
        };
    }
}