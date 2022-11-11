package nl.craftsmen.file.repository.filewriters;

import io.quarkus.logging.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;

@ApplicationScoped
public class FileWriter {

	private static final String FILE_SUCCESSFULLY_SAVED = "File %s successfully saved.";
	private static final String ERROR_SAVING_FILE = "Error saving file %s: ";
	private static final String ERROR_REMOVING_FAULTY_FILE = "Error removing faulty file %s: ";

	@Inject
    FileLocationWrapper fileLocationWrapper;

	/**
	 * Writes the file to the file repository.
	 *
	 * @param file
	 * 		file as {@link File}
	 * @param filename
	 * 		the name of the file
	 */
	public void writeFile(File file, String filename) {
		final var outputFile = fileLocationWrapper.getFile(filename);
		try (var outputStream = new FileOutputStream(outputFile)) {
			byte[] bytes = Files.readAllBytes(file.toPath());
			outputStream.write(bytes);
			Log.infof(FILE_SUCCESSFULLY_SAVED, filename);
		} catch (IOException ioe) {
			final var formattedMessage = String.format(ERROR_SAVING_FILE, filename);
			Log.error(formattedMessage, ioe);
			deleteFileIfExists(outputFile, filename);
			throw new InternalServerErrorException(formattedMessage, ioe);
		}
	}

	private void deleteFileIfExists(File outputFile, String filename) {
		try {
			Files.deleteIfExists(outputFile.toPath());
		} catch (IOException ioe) {
			final var formattedMessage = String.format(ERROR_REMOVING_FAULTY_FILE, filename);
			Log.error(formattedMessage, ioe);
			throw new InternalServerErrorException(formattedMessage, ioe);
		}
	}
}
