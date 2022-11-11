package nl.craftsmen.file.repository.fileoperations.write;

import io.quarkus.logging.Log;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import nl.craftsmen.file.repository.filereaders.FileMetadataReader;
import nl.craftsmen.file.repository.filewriters.FileMetadataWriter;
import nl.craftsmen.file.repository.filewriters.FileWriter;
import nl.craftsmen.file.repository.model.FileStatus;

@ApplicationScoped
public class RepositoryCommandService {

	private static final String METADATA_WITH_KEY_NOT_FOUND = "Metadata file with key %s not found!";

	@Inject
	FileWriter fileWriter;

	@Inject
	FileMetadataWriter fileMetadataWriter;

	@Inject
	FileMetadataReader metadataReader;

	public void saveFileAndCreateMetadata(RepositoryCreateRequest repositoryCreateRequest) {
		final var file = repositoryCreateRequest.getFile().uploadedFile().toFile();
		final var filename = repositoryCreateRequest.getFilename();
		fileMetadataWriter.createMetadataFile(file, filename);
		fileWriter.writeFile(file, filename);
	}

	public void updateMetadataStatus(String key, String status) {
		final var fileMetadata = metadataReader.readFileMetadataByKey(key).orElseGet(() -> {
			final var formattedMessage = String.format(METADATA_WITH_KEY_NOT_FOUND, key);
			Log.error(formattedMessage);
			throw new NotFoundException(formattedMessage);
		});
		final var updatedFileMetadata = fileMetadata
				.toBuilder()
				.status(FileStatus.valueOf(status))
				.build();
		fileMetadataWriter.updateMetadataFile(updatedFileMetadata);
	}
}
