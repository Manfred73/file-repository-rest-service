package nl.craftsmen.file.repository.validation.shared;

import io.quarkus.logging.Log;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import nl.craftsmen.file.repository.validation.read.RepositoryQueryOperationValidation;
import nl.craftsmen.file.repository.validation.write.RepositoryCommandOperationValidation;

@ApplicationScoped
public class ValidationVR001DoesFileLocationExist implements RepositoryQueryOperationValidation, RepositoryCommandOperationValidation {

	private static final int ORDER = 1;

	@Inject
    FileLocationWrapper fileLocationWrapper;

	@Override
	public void validate(FileValidationParams params) {
		if (fileLocationDoesNotExist()) {
			Log.error(getValidationCodeWithDescription(params));
			throw new NotFoundException(getValidationCodeWithDescription(params));
		}
	}

	@Override
	public String getValidationCodeWithDescription(FileValidationParams params) {
		return String.format(SharedValidationCode.VR001.getValidationCodeWithDescription(SharedValidationCode.VR001),
				fileLocationWrapper.getFileLocationAsString());
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	private boolean fileLocationDoesNotExist() {
		return !fileLocationWrapper.getFileBaseDirAsFile().exists();
	}
}
