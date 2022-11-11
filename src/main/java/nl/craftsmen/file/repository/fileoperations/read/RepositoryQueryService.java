package nl.craftsmen.file.repository.fileoperations.read;

import io.quarkus.logging.Log;
import java.io.File;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.filereaders.FileReader;

@ApplicationScoped
public class RepositoryQueryService {

	private static final String FILE_WITH_KEY_NOT_FOUND = "File with key %s not found!";

	@Inject
	FileLocationWrapper fileLocationWrapper;

	@Inject
	FileReader reader;

	public File getFileByFilename(String filename) {
		return fileLocationWrapper.getFile(filename);
	}

	public File getFileByKey(String key) {
		return reader.readFileByKey(key).orElseGet(() -> {
			final var formattedMessage = String.format(FILE_WITH_KEY_NOT_FOUND, key);
			Log.error(formattedMessage);
			throw new NotFoundException(formattedMessage);
		});
	}
}
