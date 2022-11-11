package nl.craftsmen.file.repository.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.craftsmen.file.repository.fileoperations.write.RepositoryCreateRequest;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileValidationParams {
	private String key;
	private String filename;
	private String status;
	private RepositoryCreateRequest request;
}
