package nl.craftsmen.file.repository.fileoperations;

import java.io.File;
import java.nio.file.Path;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FileLocationWrapper {

	@ConfigProperty(name = "files.location")
	String location;

	/**
	 * Returns the repository location as a {@link File} where the files and its metadata can be found and stored.
	 *
	 * @return the location of the repository
	 */
	public File getFileBaseDirAsFile() {
		return new File(location);
	}

	/**
	 * Returns the repository location as a {@link Path} where the files and its metadata can be found and stored.
	 *
	 * @return the location of the repository
	 */
	public Path getFileBaseDirAsPath() {
		return new File(location).toPath();
	}

	/**
	 * Returns the repository location as a {@link String} where the files and its metadata can be found and
	 * stored.
	 *
	 * @return the location of the repository
	 */
	public String getFileLocationAsString() {
		return new File(location).getPath();
	}

	/**
	 * Returns a {@link File} based on the given filename.
	 *
	 * @return the location of the repository
	 */
	public File getFile(String filename) {
		return new File(location + File.separator + filename);
	}
}
