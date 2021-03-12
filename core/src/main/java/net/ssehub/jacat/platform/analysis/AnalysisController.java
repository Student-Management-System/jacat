package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.platform.analysis.api.CreateAnalysisDto;
import net.ssehub.jacat.platform.analysis.api.ListAnalysisResultDto;
import net.ssehub.jacat.platform.course.CoursesConfiguration;
import net.ssehub.jacat.platform.error.ApplicationRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v0/analysis")
public class AnalysisController {

    private AnalysisService analysisService;
    private AnalysisTaskRepository repository;
    private CoursesConfiguration coursesConfiguration;

    public AnalysisController(AnalysisService analysisService,
                              AnalysisTaskRepository repository,
                              CoursesConfiguration coursesConfiguration) {
        this.analysisService = analysisService;
        this.repository = repository;
        this.coursesConfiguration = coursesConfiguration;
    }

    @GetMapping("/{task}")
    public ListAnalysisResultDto getAnalysis(@PathVariable("task") String taskId) {
        Optional<AnalysisTask> analysisTask = this.repository.findById(taskId);
        analysisTask.orElseThrow(AnalysisTaskNotFoundException::new);

        return new ListAnalysisResultDto(analysisTask.get());
    }

    @PostMapping()
    public ListAnalysisResultDto startAnalysis(@RequestParam("slug") String slug,
                                      @RequestBody CreateAnalysisDto createAnalysisDto) {
        if (createAnalysisDto.getData() == null) {
            throw new CourseConfigurationNotFoundException();
        }

        Optional<CoursesConfiguration.Course> courseConfiguration =
                coursesConfiguration.getCourse(createAnalysisDto.getData().getCourse());

        courseConfiguration.orElseThrow(CourseConfigurationNotFoundException::new);

        CoursesConfiguration.Course foundCourseConfiguration = courseConfiguration.get();

        createAnalysisDto.getData().setProtocol(foundCourseConfiguration.getProtocol());

        AnalysisTask analysisTask = this.analysisService.trySchedule(slug,
                foundCourseConfiguration.getLanguage(),
                createAnalysisDto);

        return new ListAnalysisResultDto(analysisTask);
    }

}
