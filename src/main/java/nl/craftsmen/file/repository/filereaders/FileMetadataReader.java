package nl.craftsmen.file.repository.filereaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;

import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.model.FileMetadata;

@ApplicationScoped
public class FileMetadataReader {

    private static final String ERROR_RETRIEVING_METADATA_FILES = "Error retrieving metadata files: ";
    private static final String ERROR_CONVERTING_FILE_TO_METADATA = "Error converting file %s to metadata: ";

    @Inject
    FileLocationWrapper fileLocationWrapper;

    @Inject
    ObjectMapper mapper;

    /**
     * Reads all metadata (*.meta) files from the file repository and creates a Map with filename and {@link FileMetadata}.
     *
     * @return a map of {@link FileMetadata}
     */
    public Map<String, FileMetadata> readAllFileMetadata() {
        final var fileMetadata = new HashMap<String, FileMetadata>();
        try (Stream<Path> metadataFiles = Files.list(fileLocationWrapper.getFileBaseDirAsPath())) {
            metadataFiles
                    .filter(path -> path.toString().endsWith(".meta"))
                    .forEach(path -> fileMetadata.put(path.getFileName()
                            .toString(), convertToFileMetadata(path)));
        } catch (IOException ioe) {
            Log.error(ERROR_RETRIEVING_METADATA_FILES, ioe);
            throw new InternalServerErrorException(ERROR_RETRIEVING_METADATA_FILES, ioe);
        }
        return Collections.unmodifiableMap(fileMetadata);
    }

    public Optional<FileMetadata> readFileMetadataByKey(String key) {
        for (Map.Entry<String, FileMetadata> metadataFile : readAllFileMetadata().entrySet()) {
            if (metadataFile.getValue().getKey().equals(key)) {
                return Optional.of(metadataFile.getValue());
            }
        }
        return Optional.empty();
    }

    private FileMetadata convertToFileMetadata(Path path) {
        try {
            return mapper.readValue(path.toFile(), FileMetadata.class);
        } catch (Exception e) {
            final var formattedMessage = String.format(ERROR_CONVERTING_FILE_TO_METADATA, path.getFileName());
            Log.error(formattedMessage, e);
            throw new InternalServerErrorException(formattedMessage, e);
        }
    }
}
