package nl.craftsmen.file.repository.filewriters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import java.io.File;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.filereaders.FileHeaderReader;
import nl.craftsmen.file.repository.model.FileMetadata;
import nl.craftsmen.file.repository.model.FileStatus;

@ApplicationScoped
public class FileMetadataWriter {

	private static final String FILE_METADATA_SUCCESSFULLY_SAVED = "File metadata %s successfully saved.";
	private static final String ERROR_SAVING_FILE_METADATA = "Error saving file metadata %s: ";

	@Inject
	FileHeaderReader fileHeaderReader;

	@Inject
    FileLocationWrapper fileLocationWrapper;

	@Inject
	ObjectMapper mapper;

	/**
	 * Writes a metadata file to the file repository associated with the file. The file contains json data with a
	 * key (the header of the file), a status and a filename.
	 *
	 * @param file
	 * 		file as {@link File}
	 * @param filename
	 * 		the name of the file
	 */
	public void createMetadataFile(File file, String filename) {
		final var fileHeader = fileHeaderReader.readFileHeader(file);
		final var fileMetadata = FileMetadata.builder()
				.key(fileHeader)
				.status(FileStatus.RECEIVED)
				.filename(filename)
				.build();
		writeMetadataFile(fileMetadata);
	}

	public void updateMetadataFile(FileMetadata metadata) {
		writeMetadataFile(metadata);
	}

	private void writeMetadataFile(FileMetadata metadata) {
		final var filename = metadata.getFilename();
		try {
			mapper.writeValue(fileLocationWrapper.getFile(filename + ".meta"), metadata);
			Log.infof(FILE_METADATA_SUCCESSFULLY_SAVED, filename);
		} catch (Exception e) {
			final var formattedMessage = String.format(ERROR_SAVING_FILE_METADATA, filename);
			Log.error(formattedMessage, e);
			throw new InternalServerErrorException(formattedMessage, e);
		}
	}
}
