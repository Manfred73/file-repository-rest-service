package nl.craftsmen.file.repository.validation;

import io.quarkus.test.Mock;
import javax.enterprise.context.ApplicationScoped;
import nl.craftsmen.file.repository.validation.read.RepositoryQueryOperationValidation;

@Mock
@ApplicationScoped
public class ValidationMock2 implements RepositoryQueryOperationValidation {

	private static final int ORDER = 2;

	@Override
	public void validate(FileValidationParams params) {
		// validationMmock2
	}

	@Override
	public String getValidationCodeWithDescription(FileValidationParams params) {
		return "ValidationMock 2";
	}

	@Override
	public int getOrder() {
		return ORDER;
	}
}
