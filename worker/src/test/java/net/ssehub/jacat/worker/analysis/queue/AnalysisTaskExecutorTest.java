package net.ssehub.jacat.worker.analysis.queue;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.task.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.IAnalysisScheduler;
import net.ssehub.jacat.worker.analysis.TaskPreparer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnalysisTaskExecutorTest {

    public static final String A_SLUG = "A_SLUG";
    public static final String A_LANGUAGE = "A_LANGUAGE";
    public static final String AN_ID = "AN_ID";

    private AnalysisTaskExecutor taskExecutor;

    private IAnalysisCapabilities<Addon> mockCapabilities;
    private TaskPreparer mockTaskPreparer;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
//        mockCapabilities = (IAnalysisCapabilities<Addon>) mock(IAnalysisCapabilities.class);
//
//        AbstractAnalysisCapability mockCapability = mock(AbstractAnalysisCapability.class);
//        when(mockCapability.run(any())).then(AdditionalAnswers.returnsFirstArg());
//        when(mockCapabilities.getCapability(anyString(), anyString()))
//                .thenReturn(mockCapability);
//
//        mockTaskPreparer = mock(TaskPreparer.class);
//        taskExecutor = new AnalysisTaskExecutor(
//                mock(IAnalysisScheduler.class),
//                mockTaskPreparer);
    }

    @Test
    void process_withSimpleMockedCapability_shouldReturnSuccessfulTask() {
        Task task = mockTask();

//        this.taskExecutor.process(task, result -> {
//            assertEquals(task, result);
//            assertEquals(result.getStatus(), Task.Status.SUCCESSFUL);
//        });
    }

    @Test
    void process_withFailingCapability_shouldReturnFailedTask() {
//        Task task = mockTask();
//
//        AbstractAnalysisCapability mockFailedCapability = mock(AbstractAnalysisCapability.class);
//        when(mockFailedCapability.run(any())).then((invocation -> {
//            PreparedTask preparedTask = invocation.getArgument(1);
//            preparedTask.setFailedResult(Collections.emptyMap());
//            return preparedTask;
//        }));
//        when(mockCapabilities.getCapability(anyString(), anyString()))
//                .thenReturn(mockFailedCapability);
//
//        this.taskExecutor.process(task, result -> {
//            assertEquals(task, result);
//            assertEquals(result.getStatus(), Task.Status.FAILED);
//        });
    }

    @Test
    void process_withExceptionThrowingCapability_shouldReturnFailedTask() {
//        Task task = mockTask();
//
//        AbstractAnalysisCapability mockFailedCapability = mock(AbstractAnalysisCapability.class);
//        when(mockFailedCapability.run(any())).thenThrow(new RuntimeException());
//        when(mockCapabilities.getCapability(anyString(), anyString()))
//                .thenReturn(mockFailedCapability);
//
//        this.taskExecutor.process(task, result -> {
//            assertEquals(task, result);
//            assertEquals(result.getStatus(), Task.Status.FAILED);
//        });
    }


    private Task mockTask() {
        Task task = new Task(AN_ID, A_SLUG, A_LANGUAGE, null, Collections.emptyMap(), null);
        PreparedTask preparedTask = new PreparedTask(task);
        when(mockTaskPreparer.prepare(any())).thenReturn(preparedTask);
        return task;
    }
}