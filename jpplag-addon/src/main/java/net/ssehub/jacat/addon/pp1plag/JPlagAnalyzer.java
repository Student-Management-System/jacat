package net.ssehub.jacat.addon.pp1plag;

import net.ssehub.jacat.api.addon.data.Submission;
import net.ssehub.jacat.api.addon.data.SubmissionCollection;
import net.ssehub.jacat.api.addon.task.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.task.PreparedTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JPlagAnalyzer extends AbstractAnalysisCapability {

    public static final String REGEX =
            "Comparing ([0-9_a-zA-Z]+)-([0-9_a-zA-Z]+): ([0-9]+\\.?[0-9]*)";
    public static final String JPLAG_JAR =
            ".\\debug\\addons\\pp1plag\\jplag-2.12.1.jar";

    public JPlagAnalyzer() {
        super("pp1plag",
                Collections.singletonList("java"),
                1.0);
    }

    @Override
    public PreparedTask run(PreparedTask task) {
        Path workspace = task.getWorkspace();
        SubmissionCollection submissions = task.getSubmissions();
        Map<String, Object> request = task.getRequest();

        double minSim = 50.0;
        if (request.containsKey("similarityThreshold")) {
            minSim = (double) request.get("similarityThreshold");
        }

        try {
            File jplag = new File(JPLAG_JAR);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-jar",
                    jplag.getAbsolutePath(),
                    "l java19",
                    workspace.toFile().getAbsolutePath())
                    .directory(workspace.toFile());
            Process p = processBuilder.start();

            List<Result> similarities = new ArrayList<>();
            Pattern pattern = Pattern.compile(REGEX);

            try(BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;

                while ((line = input.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {

                        System.out.println(line);
                        Submission from = submissions.getSubmission(matcher.group(1)).get();
                        Submission to = submissions.getSubmission(matcher.group(2)).get();

                        double sim = Double.parseDouble(matcher.group(3));
                        if (sim >= minSim) {
                            Result result = new Result(from.getSubmission());
                            if (!similarities.contains(result)) {
                                similarities.add(result);
                            }

                            similarities.get(similarities.indexOf(result))
                                    .add(to.getSubmission(), sim);
                        }
                    }
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("similarities", similarities);

            task.setSuccessfulResult(result);

        } catch (Exception err) {
            task.setFailedResult(Collections.singletonMap("error", err.getMessage()));
            err.printStackTrace();
        }
        return task;
    }
}
