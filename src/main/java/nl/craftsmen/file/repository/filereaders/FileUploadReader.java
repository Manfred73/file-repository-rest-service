package nl.craftsmen.file.repository.filereaders;

import io.quarkus.logging.Log;
import java.io.FileInputStream;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.InternalServerErrorException;
import org.jboss.resteasy.reactive.multipart.FileUpload;

/**
 * What's the best way to determine if a file is empty? See:
 * <p>
 * <ul>
 *     <li>
 *         <a href="https://stackoverflow.com/questions/7190618/most-efficient-way-to-check-if-a-file-is-empty-in-java-on-windows">Most efficient way to check
 *         if a file is empty</a>
 *     </li>
 *     <li><a href="https://coderanch.com/t/279224/java/Checking-empty-file">Checking empty file</a></li>
 * </ul>
 */
@ApplicationScoped
public class FileUploadReader {

	private static final String ERROR_DETERMINING_FILE_UPLOAD_EMPTY = "Error determining if file is empty: ";

	public boolean isFileUploadEmpty(FileUpload fileUpload) {
		try (FileInputStream fis = new FileInputStream(fileUpload.uploadedFile().toFile())) {
			return fis.read() == -1;
		} catch (IOException ioe) {
			Log.error(ERROR_DETERMINING_FILE_UPLOAD_EMPTY, ioe);
			throw new InternalServerErrorException(ERROR_DETERMINING_FILE_UPLOAD_EMPTY, ioe);
		}
	}
}
