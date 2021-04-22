package net.ssehub.jacat.platform.analysis;

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
    private AnalysisService analysisService;
    private AnalysisTaskRepository repository;
    private CoursesConfig coursesConfig;

    public AnalysisController(AnalysisService analysisService,
                              AnalysisTaskRepository repository,
                              CoursesConfig coursesConfig) {
        this.analysisService = analysisService;
        this.repository = repository;
        this.coursesConfig = coursesConfig;
    }

    @GetMapping("/{task}")
    public ListAnalysisResultDto getAnalysis(@PathVariable("task") String taskId) {
        Optional<AnalysisTask> analysisTask = this.repository.findById(taskId);
        return new ListAnalysisResultDto(analysisTask.orElseThrow(AnalysisTaskNotFoundException::new));
    }

    @PostMapping
    public ListAnalysisResultDto startAnalysis(@RequestParam("slug") String slug,
                                               @RequestParam("mode") Optional<TaskMode> modeOptional,
                                               @RequestBody CreateAnalysisDto createAnalysisDto) {
        DataProcessingRequest data = createAnalysisDto.getData();
        createAnalysisDto.setMode(modeOptional.orElse(TaskMode.SYNC));
        if (data == null) {
            throw new CourseConfigurationNotFoundException();
        }

        Optional<CourseConfig> courseConfiguration =
            coursesConfig.getCourse(data.getCourse());

        courseConfiguration.orElseThrow(CourseConfigurationNotFoundException::new);

        CourseConfig foundCourseConfiguration = courseConfiguration.get();

        data.setDataCollector(foundCourseConfiguration.getProtocol());
        data.setCodeLanguage(foundCourseConfiguration.getLanguage());
        data.setAnalysisSlug(slug);

        AnalysisTask analysisTask = this.analysisService.tryProcess(createAnalysisDto);

        return new ListAnalysisResultDto(analysisTask);
    }
}
