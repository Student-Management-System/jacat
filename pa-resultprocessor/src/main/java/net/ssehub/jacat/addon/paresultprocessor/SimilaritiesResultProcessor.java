package net.ssehub.jacat.addon.paresultprocessor;

import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.result.AbstractResultProcessor;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.studmgmt.IStudMgmtFacade;
import net.ssehub.jacat.api.studmgmt.PAUpdateStrategy;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;

import java.util.*;
import java.util.stream.Collectors;

public class SimilaritiesResultProcessor extends AbstractResultProcessor {

    public static final String PARTIAL_ASSESSMENT_TITLE = "PlagiarismCheck";

    private final IStudMgmtFacade studMgmtFacade;

    public SimilaritiesResultProcessor(IStudMgmtFacade studMgmtFacade) {
        this.studMgmtFacade = studMgmtFacade;
    }

    @Override
    public void process(Task task) {
        Map<String, Object> result = task.getResult();
        DataProcessingRequest taskConfig = task.getDataProcessingRequest();

        if (result == null
            || !result.containsKey("similarities")
            || !(result.get("similarities") instanceof List)) {
            return;
        }

        PAUpdateStrategy updateStrategy = PAUpdateStrategy.KEEP;

        try {
            updateStrategy = PAUpdateStrategy.valueOf((String) task.getRequest().get("paUpdateStrategy"));
        } catch (Exception e) {
            // Ignore, just keep the default: PAUpdateStrategy.KEEP
        }

        List<Similarity> similarities = getSimilaritiesFromResults(result);

        Map<String, Similarity> processedSims = new HashMap<>();
        for (Similarity similarity : similarities) {
            if (!processedSims.containsKey(similarity.getFrom())) {
                processedSims.put(similarity.getFrom(), similarity);
            } else {
                processedSims.get(similarity.getFrom()).addAll(similarity.getTo());
            }

            for (Similarity.To to : similarity.getTo()) {
                if (!processedSims.containsKey(to.getSubmission())) {
                    processedSims.put(to.getSubmission(), new Similarity(to.getSubmission()));
                }
                processedSims.get(to.getSubmission()).add(similarity.getFrom(), to.getSimilarity());
            }
        }

        Map<String, PartialAssessmentDto> partialAssessments = processedSims.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                s -> new PartialAssessmentDto()
                    .title(PARTIAL_ASSESSMENT_TITLE)
                    .severity(PartialAssessmentDto.SeverityEnum.WARNING)
                    .comment(getCommentFromSimilarity(s.getValue())))
            );

        this.studMgmtFacade.addPartialAssessments(
            taskConfig.getCourse(),
            taskConfig.getHomework(),
            partialAssessments,
            updateStrategy
        );

    }

    private String getCommentFromSimilarity(Similarity sim) {
        String from = sim.getFrom();
        String sims = sim.getTo().stream()
            .map(to -> to.getSubmission() + " (" + (int) Math.round(to.getSimilarity()) + "%)")
            .collect(Collectors.joining(", "));

        int lastComma = sims.lastIndexOf(",");
        if (lastComma >= 0) {
            sims = sims.substring(0, lastComma) + " and" + sims.substring(lastComma + 1);
        }
        return "Submission " + from + " might be similar to " + sims;
    }

    private List<Similarity> getSimilaritiesFromResults(Map<String, Object> result) {
        return ((List<Object>) result.get("similarities")).stream()
            .map(Similarity::fromResult)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
