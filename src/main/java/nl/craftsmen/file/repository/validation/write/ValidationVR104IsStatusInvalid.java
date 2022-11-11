package nl.craftsmen.file.repository.validation.write;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.EnumUtils;

import nl.craftsmen.file.repository.model.FileStatus;
import nl.craftsmen.file.repository.validation.FileValidationParams;

import io.quarkus.logging.Log;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class ValidationVR104IsStatusInvalid implements RepositoryCommandOperationValidation {

	private static final int ORDER = 4;

	@Override
	public void validate(FileValidationParams params) {
		if (shouldValidate(params) && isValidEnum(params)) {
			Log.error(getValidationCodeWithDescription(params));
			throw new BadRequestException(getValidationCodeWithDescription(params));
		}
	}

	@Override
	public String getValidationCodeWithDescription(FileValidationParams params) {
		return String.format(CommandValidationCode.VR104.getValidationCodeWithDescription(CommandValidationCode.VR104), params.getStatus());
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	private boolean shouldValidate(FileValidationParams params) {
		return StringUtils.isNotEmpty(params.getStatus());
	}

	private static boolean isValidEnum(FileValidationParams params) {
		return !EnumUtils.isValidEnum(FileStatus.class, params.getStatus());
	}
}
