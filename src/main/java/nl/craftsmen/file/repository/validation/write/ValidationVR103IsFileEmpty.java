package nl.craftsmen.file.repository.validation.write;

import io.quarkus.logging.Log;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import nl.craftsmen.file.repository.filereaders.FileUploadReader;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@ApplicationScoped
public class ValidationVR103IsFileEmpty implements RepositoryCommandOperationValidation {

	private static final int ORDER = 3;

	@Inject
    FileUploadReader fileUploadReader;

	@Override
	public void validate(FileValidationParams params) {
		if (shouldValidate(params) && isFileEmpty(params.getRequest().getFile())) {
			Log.error(getValidationCodeWithDescription(params));
			throw new BadRequestException(getValidationCodeWithDescription(params));
		}
	}

	@Override
	public String getValidationCodeWithDescription(FileValidationParams params) {
		return String.format(CommandValidationCode.VR103.getValidationCodeWithDescription(CommandValidationCode.VR103),
				params.getRequest().getFilename());
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	private boolean shouldValidate(FileValidationParams params) {
		return ObjectUtils.isNotEmpty(params.getRequest()) && StringUtils.isNotEmpty(params.getRequest().getFilename());
	}

	private boolean isFileEmpty(FileUpload fileUpload) {
		return fileUploadReader.isFileUploadEmpty(fileUpload);
	}
}
