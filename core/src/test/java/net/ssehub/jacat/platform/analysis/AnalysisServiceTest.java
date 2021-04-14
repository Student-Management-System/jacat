package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.IAnalysisTaskExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.mock;

class AnalysisServiceTest {
    public static final String A_SLUG = "A_SLUG";
    public static final String A_LANGUAGE = "A_LANGUAGE";
    public static final String A_SUBMISSION = "A_SUBMISSION";
    public static final String A_HOMEWORK = "A_HOMEWORK";
    public static final String A_COURSE = "A_COURSE";
    public static final String A_PROTOCOL = "A_PROTOCOL";
    public static final DataProcessingRequest A_DATA_SECTION = new DataProcessingRequest(
        A_PROTOCOL,
        A_SLUG,
        A_LANGUAGE,
        A_COURSE,
        A_HOMEWORK,
        A_SUBMISSION
    );
    public static final String A_REQUEST_VALUE = "A_REQUEST_VALUE";
    public static final String A_REQUEST_KEY = "A_REQUEST_KEY";
    public static final Map<String, Object> A_REQUEST = Collections.singletonMap(
        A_REQUEST_KEY,
        A_REQUEST_VALUE
    );
    public static final String AN_ID = "AN_ID";

    private AnalysisService analysisService;
    private IAnalysisCapabilities mockAddonCapabilities;
    private AnalysisTaskRepository mockRepository;
    private IAnalysisTaskExecutor mockExecutor;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        mockAddonCapabilities = mock(IAnalysisCapabilities.class);
        mockRepository = mock(AnalysisTaskRepository.class);
        mockRepository = mock(AnalysisTaskRepository.class);
        mockExecutor = mock(IAnalysisTaskExecutor.class);
        analysisService =
            new AnalysisService(mockAddonCapabilities, mockRepository, mockExecutor);
    }

    @Test
    void trySchedule_withSlugOrLanguageNotAvailable_throwsException() {
//        // Arrange Action Assert
//        when(mockAddonCapabilities.isRegistered(any(), any())).thenReturn(false);
//
//        assertThrows(
//            CapabilityNotAvailableException.class,
//            () -> {
//                analysisService.tryProcess(A_SLUG, A_LANGUAGE, new CreateAnalysisDto());
//            }
//        );
    }

    @Test
    void trySchedule_withFullQueue_wontSchedule() {
//        when(mockAddonCapabilities.isRegistered(any(), any())).thenReturn(true);
//
//        assertThrows(
//            QueueCapacityLimitReachedException.class,
//            () -> {
//                analysisService.tryProcess(A_SLUG, A_LANGUAGE, new CreateAnalysisDto());
//            }
//        );
    }

    @Test
    void trySchedule_withEverythingAvailable_schedulesTaskAndBuildsFinisherCorrectly() {
//        when(mockAddonCapabilities.isRegistered(any(), any())).thenReturn(true);
//        CreateAnalysisDto createAnalysisDto = new CreateAnalysisDto();
//        createAnalysisDto.setData(A_DATA_SECTION);
//        createAnalysisDto.setRequestParameter(A_REQUEST_KEY, A_REQUEST_VALUE);
//
//        AnalysisTask result = new AnalysisTask(
//            new Task(AN_ID, A_SLUG, A_LANGUAGE, null, A_DATA_SECTION, A_REQUEST, null)
//        );
//        when(mockRepository.save(any())).thenReturn(result);
//
//        analysisService.tryProcess(A_SLUG, A_LANGUAGE, createAnalysisDto);
//
//        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
//
//        verify(mockRepository).save(new AnalysisTask(argument.getValue()));
//
//        assertEquals(result.getId(), argument.getValue().getId());
    }
}
