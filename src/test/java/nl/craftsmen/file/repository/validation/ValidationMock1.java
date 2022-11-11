package nl.craftsmen.file.repository.validation;

import io.quarkus.test.Mock;
import javax.enterprise.context.ApplicationScoped;
import nl.craftsmen.file.repository.validation.read.RepositoryQueryOperationValidation;
import nl.craftsmen.file.repository.validation.write.RepositoryCommandOperationValidation;

@Mock
@ApplicationScoped
public class ValidationMock1 implements RepositoryQueryOperationValidation, RepositoryCommandOperationValidation {

	private static final int ORDER = 1;

	@Override
	public void validate(FileValidationParams params) {
		// validationMock1
	}

	@Override
	public String getValidationCodeWithDescription(FileValidationParams params) {
		return "ValidationMock 1";
	}

	@Override
	public int getOrder() {
		return ORDER;
	}
}
