package net.ssehub.jacat.addon.paresultprocessor;

import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.result.AbstractResultProcessor;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.studmgmt.IStudMgmtFacade;
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

        double similarityThreshold = getSimilarityThreshold(task.getRequest());
        int classDeviation = getClassDeviation(task.getRequest());

        double threshold = calculateThreshold(similarityThreshold, classDeviation, similarities);


        Map<String, PartialAssessmentDto> partialAssessments = processedSims.entrySet().stream()
            .filter(entry -> entry.getValue().hasOneWithMoreThan(threshold))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                s -> new PartialAssessmentDto()
                    .key(PARTIAL_ASSESSMENT_TITLE)
                    .title(PARTIAL_ASSESSMENT_TITLE)
                    .draftOnly(true)
                    .comment(getCommentFromSimilarity(s.getValue(), threshold)))
            );

        this.studMgmtFacade.addOrUpdatePartialAssessments(
            taskConfig.getCourse(),
            taskConfig.getHomework(),
            partialAssessments
        );

    }

    private double getSimilarityThreshold(Map<String, Object> request) {
        double similarityThreshold = -1.0;

        try {
            similarityThreshold = (double) request.get("similarityThreshold");
        } catch (Exception e) {

        }
        return similarityThreshold;
    }

    private int getClassDeviation(Map<String, Object> request) {
        int classDeviation = -1;
        try {
            classDeviation = (int) request.get("classDeviation");
        } catch (Exception e) {

        }
        return classDeviation;
    }

    private double calculateThreshold(double similarityThreshold,
                                      int classDeviation,
                                      Collection<Similarity> values) {
        double deviationThreshold = 100.0;
        if (classDeviation >= 0) {
            double total = 0;
            int count = 0;
            for (Similarity similarity : values) {
                total += similarity.getTo().stream().mapToDouble(Similarity.To::getSimilarity).sum();
                count += similarity.getTo().size();
            }
            double mean = total/count;
            int clazz =  (((int) mean)/10) + classDeviation;
            deviationThreshold = clazz * 10;
        }

        if (similarityThreshold < 0) {
            similarityThreshold = 101.00;
        }
        return Math.min(deviationThreshold, similarityThreshold);
    }

    private String getCommentFromSimilarity(Similarity sim, double threshold) {
        String from = sim.getFrom();
        String sims = sim.getTo().stream()
            .filter(to -> to.getSimilarity() > threshold)
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
