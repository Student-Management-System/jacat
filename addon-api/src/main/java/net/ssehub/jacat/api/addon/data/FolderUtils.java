package net.ssehub.jacat.api.addon.data;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;

public class FolderUtils {

    public static void copyFolder(Path workspace,
                                  Path source,
                                  String folderName,
                                  CopyOption ...options) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Path directory = getWorkspace(workspace, folderName)
                        .resolve(source.relativize(dir));
                createDirectories(directory);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Path relativizedFile = source.relativize(file);
                copy(file, getWorkspace(workspace, folderName)
                        .resolve(relativizedFile), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static Path getWorkspace(Path workspace, String folderName) {
        Path directory = workspace;
        if (folderName != null) {
            directory = workspace.resolve(folderName);
        }
        return directory;
    }

}
