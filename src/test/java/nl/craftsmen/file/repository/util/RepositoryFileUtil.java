package nl.craftsmen.file.repository.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;
import nl.craftsmen.file.repository.exceptionhandling.UnexpectedTestException;

/**
 * This is a class with some helper methods for creating and deleting temporary files and directory for the integration tests.
 */
@UtilityClass
public final class RepositoryFileUtil {

    public static Path createTemporaryDirectoryForFiles(String resourcePath) {
        final var tmpDir = System.getProperty("java.io.tmpdir");
        try {
            final var filesTmpDirPath = Paths.get(tmpDir + File.separator + resourcePath);
            // if cleanup failed last time, make sure we clean it here to prevent IOException when creating the temporary directory
            deleteFilesIfExist(filesTmpDirPath);
            return Files.createDirectory(filesTmpDirPath);
        } catch (IOException ioe) {
            throw new UnexpectedTestException("Error creating temporary directory for files: ", ioe);
        }
    }

    public void deleteFilesAndTmpDir(String resourcePath) throws IOException {
        final var tmpDir = System.getProperty("java.io.tmpdir");
        final var filesTmpDirPath = Paths.get(tmpDir + File.separator + resourcePath);
        deleteFilesIfExist(filesTmpDirPath);
        Files.deleteIfExists(filesTmpDirPath);
    }

    public static Path copyFileFromResourceClasspathToTmpDirectory(Path filesPath, String filename) throws IOException {
        final var fileContent = ResourceReader.readFileContentFromClassLoaderResourceAsString(filesPath.getFileName() + File.separator + filename);
        final var fileInput = filesPath.resolve(filename);
        return Files.writeString(fileInput, fileContent);
    }

    public static Path copyFileFromResourceClasspathToTmpDirectory(Path filesResourceClassPath, String filename, Path tempDir) throws IOException {
        final var fileContent = ResourceReader.readFileContentFromClassLoaderResourceAsString(filesResourceClassPath.getFileName() + File.separator + filename);
        final var fileInput = tempDir.resolve(filename);
        return Files.writeString(fileInput, fileContent);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Path copyFile(Path directory, String source, String destination) throws IOException {
        final var sourcePath = directory.resolve(source);
        final var destinationPath = directory.resolve(destination);
        return Files.copy(sourcePath, destinationPath);
    }

    private static void deleteFilesIfExist(Path filesTmpDirPath) throws IOException {
        if (Files.exists(filesTmpDirPath)) {
            try (Stream<Path> files = Files.list(filesTmpDirPath)) {
                files.forEach(RepositoryFileUtil::deleteFile);
                Files.deleteIfExists(filesTmpDirPath);
            }
        }
    }

    private static void deleteFile(Path p) {
        try {
            Files.delete(p);
        } catch (IOException ioe) {
            throw new UnexpectedTestException("Error removing file: ", ioe);
        }
    }
}
