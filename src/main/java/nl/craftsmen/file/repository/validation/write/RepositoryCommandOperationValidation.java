package nl.craftsmen.file.repository.validation.write;

import nl.craftsmen.file.repository.validation.FileValidationParams;

public interface RepositoryCommandOperationValidation {

	void validate(FileValidationParams params);

	String getValidationCodeWithDescription(FileValidationParams params);

	int getOrder();
}
