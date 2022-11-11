package nl.craftsmen.file.repository.validation.read;

import io.quarkus.logging.Log;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import nl.craftsmen.file.repository.filereaders.FileReader;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class ValidationVR003DoesFileForKeyExist implements RepositoryQueryOperationValidation {

	private static final int ORDER = 3;

	@Inject
    FileReader fileReader;

	@Override
	public void validate(FileValidationParams params) {
		if (shouldValidate(params) && doesFileForKeyNotExist(params.getKey())) {
			Log.error(getValidationCodeWithDescription(params));
			throw new NotFoundException(getValidationCodeWithDescription(params));
		}
	}

	@Override
	public String getValidationCodeWithDescription(FileValidationParams params) {
		return String.format(QueryValidationCode.VR003.getValidationCodeWithDescription(QueryValidationCode.VR003), params.getKey());
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	private boolean shouldValidate(FileValidationParams params) {
		return StringUtils.isNotEmpty(params.getKey());
	}

	private boolean doesFileForKeyNotExist(String key) {
		return fileReader.readFileByKey(key).isEmpty();
	}
}
