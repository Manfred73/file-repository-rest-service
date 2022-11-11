package nl.craftsmen.file.repository.validation.write;

import io.quarkus.logging.Log;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import nl.craftsmen.file.repository.exceptionhandling.FileAlreadyExistsException;
import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.apache.commons.lang3.ObjectUtils;

@ApplicationScoped
public class ValidationVR102FileAlreadyExists implements RepositoryCommandOperationValidation {

	private static final int ORDER = 2;

	@Inject
	FileLocationWrapper fileLocationWrapper;

	@Override
	public void validate(FileValidationParams params) {
		if (shouldValidate(params) && doesFileAlreadyExist(params)) {
			final var formattedMessage = getValidationCodeWithDescription(params);
			Log.error(formattedMessage);
			throw new FileAlreadyExistsException(formattedMessage);
		}
	}

	@Override
	public String getValidationCodeWithDescription(FileValidationParams params) {
		return String.format(CommandValidationCode.VR102.getValidationCodeWithDescription(CommandValidationCode.VR102),
				params.getRequest().getFilename());
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	private boolean doesFileAlreadyExist(FileValidationParams params) {
		return fileLocationWrapper.getFile(params.getRequest().getFilename()).exists();
	}

	private boolean shouldValidate(FileValidationParams params) {
		return ObjectUtils.isNotEmpty(params.getRequest());
	}
}
