package nl.craftsmen.file.repository.validation.read;

import io.quarkus.logging.Log;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class ValidationVR002DoesFileWithFilenameExist implements RepositoryQueryOperationValidation {

	private static final int ORDER = 2;

	@Inject
    FileLocationWrapper fileLocationWrapper;

	@Override
	public void validate(FileValidationParams params) {
		if (shouldValidate(params) && doesFileWithFilenameNotExist(params.getFilename())) {
			Log.error(getValidationCodeWithDescription(params));
			throw new NotFoundException(getValidationCodeWithDescription(params));
		}
	}

	@Override
	public String getValidationCodeWithDescription(FileValidationParams params) {
		return String.format(QueryValidationCode.VR002.getValidationCodeWithDescription(QueryValidationCode.VR002), params.getFilename());
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	private boolean shouldValidate(FileValidationParams params) {
		return StringUtils.isNotEmpty(params.getFilename());
	}

	private boolean doesFileWithFilenameNotExist(String filename) {
		return !fileLocationWrapper.getFile(filename).exists();
	}
}
