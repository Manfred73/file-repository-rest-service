package nl.craftsmen.file.repository.filereaders;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.model.FileMetadata;

@ApplicationScoped
public class FileReader {

	@Inject
	FileMetadataReader fileMetadataReader;

	@Inject
    FileLocationWrapper fileLocationWrapper;

	public Optional<File> readFileByKey(String key) {
		for (Map.Entry<String, FileMetadata> metadataFile : fileMetadataReader.readAllFileMetadata().entrySet()) {
			if (metadataFile.getValue().getKey().equals(key)) {
				return Optional.of(fileLocationWrapper.getFile(metadataFile.getValue().getFilename()));
			}
		}
		return Optional.empty();
	}
}
