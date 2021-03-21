package net.ssehub.jacat.api.addon.data;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;

/**
 * This class provides utility-methods used for folder operations.
 */
public class FolderUtils {

    /**
     * This functions copies a folder recursively.
     *
     * @param source     the source path
     * @param target     the target path
     * @param folderName the name of the folder where the newly created files should be put
     * @param options    see {@link CopyOption}
     * @throws IOException if something goes wrong when accessing the file system
     */
    public static void copyFolder(Path source, Path target, String folderName, CopyOption... options)
        throws IOException {
        Files.walkFileTree(
            source,
            new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                    Path directory = getWorkspace(target, folderName)
                        .resolve(source.relativize(dir));
                    createDirectories(directory);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                    Path relativizedFile = source.relativize(file);
                    copy(
                        file,
                        getWorkspace(target, folderName).resolve(relativizedFile),
                        options
                    );
                    return FileVisitResult.CONTINUE;
                }
            }
        );
    }

    /**
     * Combines the workspace with the foldername if folderName is not null.
     *
     * @param workspace  the path for the workspace
     * @param folderName the folderName
     * @return
     */
    private static Path getWorkspace(Path workspace, String folderName) {
        Path directory = workspace;
        if (folderName != null) {
            directory = workspace.resolve(folderName);
        }
        return directory;
    }
}
