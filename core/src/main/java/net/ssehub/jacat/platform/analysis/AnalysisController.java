package net.ssehub.jacat.platform.analysis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.task.TaskMode;
import net.ssehub.jacat.platform.analysis.api.CreateAnalysisDto;
import net.ssehub.jacat.platform.analysis.api.ListAnalysisResultDto;
import net.ssehub.jacat.platform.analysis.exception.AnalysisTaskNotFoundException;
import net.ssehub.jacat.platform.analysis.exception.CourseConfigurationNotFoundException;
import net.ssehub.jacat.platform.course.config.CourseConfig;
import net.ssehub.jacat.platform.course.config.CoursesConfig;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v0/analysis")
public class AnalysisController {
    private final AnalysisService analysisService;
    private final AnalysisTaskRepository repository;
    private final CoursesConfig coursesConfig;

    public AnalysisController(AnalysisService analysisService,
                              AnalysisTaskRepository repository,
                              CoursesConfig coursesConfig) {
        this.analysisService = analysisService;
        this.repository = repository;
        this.coursesConfig = coursesConfig;
    }

    @Operation(summary = "Get an analysis task by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the task",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = ListAnalysisResultDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid id supplied",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found",
            content = @Content) })
    @GetMapping("/{taskId}")
    public ListAnalysisResultDto getAnalysis(@PathVariable("taskId") String taskId) {
        Optional<AnalysisTask> analysisTask = this.repository.findById(taskId);
        return new ListAnalysisResultDto(analysisTask.orElseThrow(AnalysisTaskNotFoundException::new));
    }

    @Operation(summary = "Create a new analysis task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analysis task created",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = ListAnalysisResultDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid course provided",
            content = @Content),
        @ApiResponse(responseCode = "501", description = "Analysis capability not implemented yet",
            content = @Content) })
    @PostMapping
    public ListAnalysisResultDto startAnalysis(@RequestParam("slug") String slug,
                                               @RequestParam("mode") Optional<TaskMode> modeOptional,
                                               @RequestBody CreateAnalysisDto createAnalysisDto) {
        createAnalysisDto.setMode(modeOptional.orElse(TaskMode.SYNC));
        CreateAnalysisDto.Data data = createAnalysisDto.getData();
        if (data == null) {
            throw new CourseConfigurationNotFoundException();
        }

        Optional<CourseConfig> courseConfiguration =
            coursesConfig.getCourse(data.getCourse());

        courseConfiguration.orElseThrow(CourseConfigurationNotFoundException::new);

        CourseConfig foundCourseConfiguration = courseConfiguration.get();

        DataProcessingRequest dataProcessingRequest = new DataProcessingRequest(
            foundCourseConfiguration.getProtocol(),
            slug,
            foundCourseConfiguration.getLanguage(),
            data.getCourse(),
            data.getHomework(),
            data.getSubmission()
        );

        AnalysisTask analysisTask = this.analysisService.tryProcess(createAnalysisDto, dataProcessingRequest);

        return new ListAnalysisResultDto(analysisTask);
    }
}
