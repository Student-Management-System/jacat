package net.ssehub.jacat.addon.pp1plag;

import net.ssehub.jacat.api.addon.analysis.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.data.Submission;
import net.ssehub.jacat.api.addon.data.SubmissionCollection;
import net.ssehub.jacat.api.addon.task.FinishedTask;
import net.ssehub.jacat.api.addon.task.PreparedTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JPlagAnalyzer extends AbstractAnalysisCapability {

    public static final String REGEX =
        "Comparing ([0-9_a-zA-Z]+)-([0-9_a-zA-Z]+): ([0-9]+\\.?[0-9]*)";
    public static final Path JPLAG_JAR = Path.of("addons", "pp1plag", "jplag-2.12.1.jar");

    private Path workdir;

    public JPlagAnalyzer(Path workdir) {
        super("pp1plag",
            Collections.singletonList("java"),
            1.0);
        this.workdir = workdir;
    }

    @Override
    public FinishedTask run(PreparedTask task) {
        Path workspace = task.getWorkspace();
        SubmissionCollection submissions = task.getSubmissions();

        try {
            Path jplag = this.workdir.resolve(JPLAG_JAR);

            ProcessBuilder processBuilder = new ProcessBuilder().directory(workspace.toFile());
            processBuilder.command(
                "java",
                "-jar",
                "\"" + jplag.toAbsolutePath() + "\"",
                "-l", "java19",
                "-s",
                workspace.toFile().getCanonicalPath());
            processBuilder.redirectErrorStream(true);
            Process p = processBuilder.start();

            List<Similarity> similarities = new ArrayList<>();
            Pattern pattern = Pattern.compile(REGEX);

            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;

                while ((line = input.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    // System.out.println(line);
                    if (matcher.matches()) {
                        Submission from = submissions.getSubmission(matcher.group(1)).get();
                        Submission to = submissions.getSubmission(matcher.group(2)).get();

                        double sim = Double.parseDouble(matcher.group(3));

                        Similarity result = new Similarity(from.getSubmission());
                        if (!similarities.contains(result)) {
                            similarities.add(result);
                        }

                        similarities.get(similarities.indexOf(result))
                            .add(to.getSubmission(), sim);

                    }
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("similarities", similarities);

            return task.success(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            return task.fail(ex);
        }
    }
}
