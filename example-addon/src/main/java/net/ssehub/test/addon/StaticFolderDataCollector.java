package net.ssehub.test.addon;

import net.ssehub.jacat.api.addon.data.*;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.nio.file.Files.copy;

public class StaticFolderDataCollector extends AbstractDataCollector {

    public StaticFolderDataCollector() {
        super("local");
    }

    @Override
    public void arrange(DataRequest request) {
        // SVN Aufrufen, Dateien runterladen
        // eventuell umsortieren
    }

    @Override
    public SubmissionCollection collect(DataRequest dataRequest) {
        SubmissionCollection submissions = new SubmissionCollection();
        if (dataRequest.getCourse() != null
                && dataRequest.getHomework() == null
                && dataRequest.getSubmission() == null) { // get all submissions for all homeworks
            File baseCopyFrom = new File(".\\debug\\addons\\local\\java");
            Stream<File> stream = Arrays.stream(baseCopyFrom.listFiles());

            stream.filter(File::isDirectory).forEach(homework -> {
                File[] submissionFiles = homework.listFiles();
                for(File submissionFolder : submissionFiles) {
                    if (submissionFolder.isFile()) {
                        continue;
                    }
                    Submission java = new Submission("java",
                            homework.getName(),
                            submissionFolder.getName(),
                            submissionFolder.toPath());
                    submissions.add(java);
                }
            });
        }

        return submissions;
    }

}
