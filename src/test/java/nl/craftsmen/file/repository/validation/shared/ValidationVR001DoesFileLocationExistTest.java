package nl.craftsmen.file.repository.validation.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.mock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import java.io.File;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

@QuarkusTest
class ValidationVR001DoesFileLocationExistTest {

	private static final String FILE_LOCATION = "/home/ftd/files";
	public static final String VR001_DESCRIPTION = "VR001: Location " + FILE_LOCATION + " for files not found!";

	@InjectMock
	FileLocationWrapper fileLocationWrapper;

	@Inject
	ValidationVR001DoesFileLocationExist cut;

	@Test
	@DisplayName("Given a file location, I expect that the validation is executed, that the file location exists and no exception occurs")
	void expect_validation_to_be_executed_and_file_location_exists_an_no_exception_occurs() {
		// GIVEN
		final var params = mockExpectValidationExecutedAndFileLocationExistsAndNoExceptionOccurs();

		// WHEN / THEN
		assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("Given a file location, I expect that the validation is executed, that the location does not exist and that a NotFoundException occurs")
	void expect_validation_to_be_executed_and_file_location_does_not_exist_and_NotFoundException_occurs() {
		// GIVEN
		final var params = mockExpectValidationExecutedAndFileLocationDoesNotExistAndNotFoundExceptionOccurs();

		// WHEN / THEN
		final var thrown = catchThrowable(() -> cut.validate(params));

		// THEN
		assertThat(thrown)
				.isInstanceOf(NotFoundException.class)
				.hasMessage(VR001_DESCRIPTION);
	}

	@Test
	@DisplayName("Given a file location, when getValidationCodeWithDescription is called, I expect a validation code with description to be returned")
	void expect_validation_code_with_description_to_be_returned_when_calling_getValidationCodeWithDescription() {
		// GIVEN
		final var params = FileValidationParams.builder().build();
		BDDMockito.given(fileLocationWrapper.getFileLocationAsString()).willReturn(FILE_LOCATION);

		// WHEN
		final var description = cut.getValidationCodeWithDescription(params);

		// THEN
		assertThat(description).isEqualTo(VR001_DESCRIPTION);
	}

	@Test
	@DisplayName("Expect value 1 to be returned when calling getOrder")
	void expect_value_1_to_be_returned_when_calling_getOrder() {
		// GIVEN / WHEN
		final var order = cut.getOrder();

		// THEN
		assertThat(order).isEqualTo(1);
	}

	private FileValidationParams mockExpectValidationExecutedAndFileLocationExistsAndNoExceptionOccurs() {
		final var params = FileValidationParams.builder().build();
		final var fileLocationMock = mock(File.class);
		BDDMockito.given(fileLocationWrapper.getFileBaseDirAsFile()).willReturn(fileLocationMock);
		BDDMockito.given(fileLocationMock.exists()).willReturn(true);
		return params;
	}

	private FileValidationParams mockExpectValidationExecutedAndFileLocationDoesNotExistAndNotFoundExceptionOccurs() {
		final var params = FileValidationParams.builder().build();
		final var fileLocationMock = mock(File.class);
		BDDMockito.given(fileLocationWrapper.getFileBaseDirAsFile()).willReturn(fileLocationMock);
		BDDMockito.given(fileLocationMock.exists()).willReturn(false);
		BDDMockito.given(fileLocationWrapper.getFileLocationAsString()).willReturn(FILE_LOCATION);
		return params;
	}
}