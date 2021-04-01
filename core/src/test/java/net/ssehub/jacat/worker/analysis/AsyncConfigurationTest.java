package net.ssehub.jacat.worker.analysis;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class AsyncConfigurationTest {

    @Test
    void getAsyncExecutor_buildsValidThreadPool() {
        AsyncConfiguration asyncConfiguration = new AsyncConfiguration();
        Executor asyncExecutor = asyncConfiguration.getAsyncExecutor();

        assertNotNull(asyncExecutor);
        assertEquals(ThreadPoolTaskExecutor.class, asyncExecutor.getClass());

    }
}