package nl.craftsmen.file.repository.validation.read;

import nl.craftsmen.file.repository.validation.FileValidationParams;

public interface RepositoryQueryOperationValidation {

	void validate(FileValidationParams params);

	String getValidationCodeWithDescription(FileValidationParams params);

	int getOrder();
}
