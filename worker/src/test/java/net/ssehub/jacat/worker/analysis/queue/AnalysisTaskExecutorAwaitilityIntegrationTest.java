package net.ssehub.jacat.worker.analysis.queue;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.task.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.IAnalysisScheduler;
import net.ssehub.jacat.api.analysis.IAnalysisTaskExecutor;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(ScheduledConfig.class)
public class AnalysisTaskExecutorAwaitilityIntegrationTest {

    public static final String A_SLUG = "A_SLUG";
    public static final String A_LANGUAGE = "A_LANGUAGE";
    @SpyBean
    private IAnalysisTaskExecutor taskExecutor;

    @MockBean
    private IAnalysisCapabilities<Addon> capabilities;

    @BeforeEach
    void setUp() {
        when(capabilities.getCapability(eq(A_SLUG), eq(A_LANGUAGE)))
                .thenReturn(new AbstractAnalysisCapability(A_SLUG, Collections.singletonList(A_LANGUAGE), 0) {
                    @Override
                    public PreparedTask run(PreparedTask task) {
                        return task;
                    }
                });
    }

    @Autowired
    private IAnalysisScheduler taskScheduler;

    @Test
    public void process_whenScheduled_shouldBeProcessed() {
        Task inputTask = new Task(
                "AN_ID", A_SLUG, A_LANGUAGE,
                null, Collections.emptyMap(), (task) -> {});
        taskScheduler.trySchedule(inputTask);


        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        await().atMost(Duration.FIVE_SECONDS)
                .untilAsserted(() -> {
                    verify(taskExecutor,
                            times(1))
                            .process(taskArgumentCaptor.capture(), any());
                    assertEquals(inputTask.getId(), taskArgumentCaptor.getValue().getId());
                });

    }

}
