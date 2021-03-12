package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.data.DataSection;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.platform.analysis.api.CreateAnalysisDto;
import net.ssehub.jacat.worker.analysis.queue.AnalysisTaskScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnalysisServiceTest {

    public static final String A_SLUG = "A_SLUG";
    public static final String A_LANGUAGE = "A_LANGUAGE";
    public static final String A_SUBMISSION = "A_SUBMISSION";
    public static final String A_HOMEWORK = "A_HOMEWORK";
    public static final String A_COURSE = "A_COURSE";
    public static final String A_PROTOCOL = "A_PROTOCOL";
    public static final DataSection A_DATA_SECTION = new DataSection(A_PROTOCOL, A_COURSE, A_HOMEWORK, A_SUBMISSION);
    public static final String A_REQUEST_VALUE = "A_REQUEST_VALUE";
    public static final String A_REQUEST_KEY = "A_REQUEST_KEY";
    public static final Map<String, Object> A_REQUEST = Collections.singletonMap(A_REQUEST_KEY, A_REQUEST_VALUE);
    public static final String AN_ID = "AN_ID";

    private AnalysisService analysisService;
    private IAnalysisCapabilities<Addon> mockAddonCapabilities;
    private AnalysisTaskRepository mockRepository;
    private AnalysisTaskScheduler mockTaskScheduler;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        mockAddonCapabilities = mock(IAnalysisCapabilities.class);
        mockRepository = mock(AnalysisTaskRepository.class);
        mockTaskScheduler = mock(AnalysisTaskScheduler.class);
        analysisService = new AnalysisService(mockAddonCapabilities, mockTaskScheduler, mockRepository);
    }

    @Test
    void trySchedule_withSlugOrLanguageNotAvailable_throwsException() {
        // Arrange Action Assert
        when(mockAddonCapabilities.isRegistered(any(), any())).thenReturn(false);

        assertThrows(CapabilityNotAvailableException.class, () -> {
            analysisService.trySchedule(A_SLUG, A_LANGUAGE, new CreateAnalysisDto());
        });
    }

    @Test
    void trySchedule_withFullQueue_wontSchedule() {
        when(mockAddonCapabilities.isRegistered(any(), any())).thenReturn(true);
        when(mockTaskScheduler.canSchedule()).thenReturn(false);

        assertThrows(QueueCapacityLimitReachedException.class, () -> {
            analysisService.trySchedule(A_SLUG, A_LANGUAGE, new CreateAnalysisDto());
        });
    }

    @Test
    void trySchedule_withEverythingAvailable_schedulesTaskAndBuildsFinisherCorrectly() {
        when(mockAddonCapabilities.isRegistered(any(), any())).thenReturn(true);
        when(mockTaskScheduler.canSchedule()).thenReturn(true);
        CreateAnalysisDto createAnalysisDto = new CreateAnalysisDto();
        createAnalysisDto.setData(A_DATA_SECTION);
        createAnalysisDto.setRequestParameter(A_REQUEST_KEY, A_REQUEST_VALUE);

        AnalysisTask result = new AnalysisTask(
                new Task(AN_ID, A_SLUG, A_LANGUAGE, null, A_DATA_SECTION, A_REQUEST, null)
        );
        when(mockRepository.save(any())).thenReturn(result);

        analysisService.trySchedule(A_SLUG, A_LANGUAGE, createAnalysisDto);

        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        verify(mockTaskScheduler).trySchedule(argument.capture());

        argument.getValue().finish();
        verify(mockRepository).save(new AnalysisTask(argument.getValue()));

        assertEquals(result.getId(), argument.getValue().getId());
    }
}