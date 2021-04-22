package net.ssehub.jacat.worker.analysis;

import net.ssehub.jacat.api.addon.analysis.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.task.FinishedTask;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.addon.task.TaskMode;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.TaskCompletion;
import net.ssehub.jacat.worker.result.ResultProcessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnalysisTaskExecutorTest {

    public static final String A_TASK_ID = "A_TASK_ID";
    public static final String A_FAILING_SLUG = "A_FAILING_SLUG";
    private IAnalysisCapabilities mockCapabilities;
    private ITaskPreparer mockPreparer;
    private ITaskScrapper mockScrapper;
    private ResultProcessors mockResultProcessors;
    private AnalysisTaskExecutor uut;

    public static final String A_DATACOLLECTOR = "A_DATACOLLECTOR";
    public static final String A_SLUG = "A_SLUG";
    public static final String A_LANGUAGE = "A_LANGUAGE";
    public static final String A_COURSE = "A_COURSE";
    public static final String A_HOMEWORK = "A_HOMEWORK";
    public static final String A_SUBMISSION = "A_SUBMISSION";

    private static final DataProcessingRequest A_DATA_PROCESSING_REQUEST =
            new DataProcessingRequest(A_DATACOLLECTOR, A_SLUG, A_LANGUAGE,
                    A_COURSE, A_HOMEWORK, A_SUBMISSION);
    private static final DataProcessingRequest A_FAILING_DATA_PROCESSING_REQUEST =
            new DataProcessingRequest(A_DATACOLLECTOR, A_FAILING_SLUG, A_LANGUAGE,
                    A_COURSE, A_HOMEWORK, A_SUBMISSION);

    public static final String A_DEFAULT_SLUG = "A_DEFAULT_SLUG";
    private static final DataProcessingRequest A_DEFAULT_PROCESSING_REQUEST =
            new DataProcessingRequest(A_DATACOLLECTOR, A_DEFAULT_SLUG, A_LANGUAGE,
                    A_COURSE, A_HOMEWORK, A_SUBMISSION);
    @BeforeEach
    void setUp() {
        this.mockCapabilities = mock(IAnalysisCapabilities.class);
        mockCapabilities();

        this.mockPreparer = mock(ITaskPreparer.class);
        mockPreparer();

        this.mockScrapper = mock(ITaskScrapper.class);

        this.mockResultProcessors = mock(ResultProcessors.class);
        this.uut = new AnalysisTaskExecutor(mockCapabilities, mockPreparer, mockScrapper, mockResultProcessors);
    }

    @Test
    void process_withSameTask_shouldOnlyProcessOnce() throws InterruptedException {
        TaskCompletion mock = mock(TaskCompletion.class);
        Task task = new Task(A_TASK_ID, null, A_DATA_PROCESSING_REQUEST, Collections.emptyMap(), TaskMode.ASYNC);

        Thread processA = new Thread(() -> uut.process(task, mock));
        Thread processB = new Thread(() -> uut.process(task, mock));

        processA.start();
        processB.start();
        processA.join();
        processA.join();

        verify(mock, times(1)).finish(any(Task.class));
    }

    @ParameterizedTest
    @MethodSource("processParametersAndExpectations")
    void process(DataProcessingRequest dpr, Task.Status status) {
        TaskCompletion mock = mock(TaskCompletion.class);

        Task task = new Task(A_TASK_ID, null, dpr, Collections.emptyMap(), TaskMode.ASYNC);

        this.uut.process(task, mock);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(mock, times(1)).finish(taskArgumentCaptor.capture());

        Task actualTask = taskArgumentCaptor.getValue();
        assertEquals(A_TASK_ID, actualTask.getId());
        assertEquals(status, actualTask.getStatus());
    }

    private static Stream<Arguments> processParametersAndExpectations() {
        return Stream.of(
                Arguments.of(A_DATA_PROCESSING_REQUEST, Task.Status.SUCCESSFUL),
                Arguments.of(A_DEFAULT_PROCESSING_REQUEST, Task.Status.SUCCESSFUL),
                Arguments.of(A_FAILING_DATA_PROCESSING_REQUEST, Task.Status.FAILED)
        );
    }

    private void mockCapabilities() {
        when(mockCapabilities.getCapability(eq(A_SLUG), eq(A_LANGUAGE))).thenReturn(
            new AbstractAnalysisCapability(A_SLUG, Collections.singletonList(A_LANGUAGE), 0) {
                @Override
                public FinishedTask run(PreparedTask task) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) { }
                    return task.success(null);
                }
            });

        when(mockCapabilities.getCapability(eq(A_FAILING_SLUG), eq(A_LANGUAGE))).thenReturn(
                new AbstractAnalysisCapability(A_FAILING_SLUG, Collections.singletonList(A_LANGUAGE), 0) {
                    @Override
                    public FinishedTask run(PreparedTask task) {
                        throw new RuntimeException();
                    }
                });

        // Default in terms of the status remains null
        when(mockCapabilities.getCapability(eq(A_DEFAULT_SLUG), eq(A_LANGUAGE))).thenReturn(
                new AbstractAnalysisCapability(A_DEFAULT_SLUG, Collections.singletonList(A_LANGUAGE), 0) {
                    @Override
                    public FinishedTask run(PreparedTask task) {
                        return new FinishedTask(task, null, null);
                    }
                });
    }

    private void mockPreparer() {
        when(this.mockPreparer.prepare(any(Task.class))).thenAnswer(invocationOnMock -> {
            Task task = invocationOnMock.getArgument(0);
            return new PreparedTask(task, null, null);
        });
    }

}
