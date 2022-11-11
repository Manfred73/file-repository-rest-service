package nl.craftsmen.file.repository.fileoperations.write;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

/**
 * This is the form data that is received in a Multipart request.
 * <ul>
 *     <li>name: this is the filename of the file as it will be written to the file repository</li>
 *     <li>file: this is the original file</li>
 * </ul>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RepositoryCreateRequest {

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	@NotNull(message = "filename is mandatory")
	private String filename;

	@RestForm
	@NotNull(message = "file is mandatory")
	private FileUpload file;
}
