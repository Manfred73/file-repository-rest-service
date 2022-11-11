package nl.craftsmen.file.repository.validation.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
class ValidationVR002DoesFileWithFilenameExistTest {

	private static final String FILENAME = "testfile.txt";
	public static final String VR002_DESCRIPTION = "VR002: File with name " + FILENAME + " not found!";

	@InjectMock
	FileLocationWrapper fileLocationWrapper;

	@Inject
	ValidationVR002DoesFileWithFilenameExist cut;

	@Test
	@DisplayName("Given a filename, I expect that the validation is being executed, that the file exists and that no exception occurs")
	void expect_validation_to_be_executed_that_file_exists_and_no_exception_occurs() {
		// GIVEN
		final var params = mockExpectValidationExecutedAndFileExistsAndNoExceptionOccurs();

		// WHEN / THEN
		assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("Given a filename, I expect that validation is being executed, that the file does not exist and that a NotFoundException occurs")
	void expect_validation_to_be_executed_that_file_does_not_exist_and_NotFoundException_occurs() {
		// GIVEN
		final var params = mockExpectValidationExecutedAndFileDoesNotExistAndNotFoundExceptionOccurs();

		// WHEN / THEN
		final var thrown = catchThrowable(() -> cut.validate(params));

		// THEN
		assertThat(thrown)
				.isInstanceOf(NotFoundException.class)
				.hasMessage(VR002_DESCRIPTION);
	}

	@Test
	@DisplayName("Expect no exception and that validation on filename is not being executed")
	void expect_no_exception_and_that_validation_is_not_being_executed() {
		// GIVEN
		final var params = FileValidationParams.builder().build();

		// WHEN / THEN
		assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
		verify(fileLocationWrapper, never()).getFile(any());
	}

	@Test
	@DisplayName("Given a filename, when getValidationCodeWithDescription is called, I expect a validation code with description will be returned")
	void expect_validation_code_with_description_to_be_returned_when_calling_getValidationCodeWithDescription() {
		// GIVEN
		final var params = FileValidationParams.builder().filename(FILENAME).build();

		// WHEN
		final var description = cut.getValidationCodeWithDescription(params);

		// THEN
		assertThat(description).isEqualTo(VR002_DESCRIPTION);
	}

	@Test
	@DisplayName("Expect value 2 to be returned when calling getOrder")
	void expect_value_2_when_calling_getOrder() {
		// GIVEN / WHEN
		final var order = cut.getOrder();

		// THEN
		assertThat(order).isEqualTo(2);
	}

	private FileValidationParams mockExpectValidationExecutedAndFileExistsAndNoExceptionOccurs() {
		final var params = FileValidationParams.builder().filename(FILENAME).build();
		final var fileMock = mock(File.class);
		BDDMockito.given(fileLocationWrapper.getFile(FILENAME)).willReturn(fileMock);
		BDDMockito.given(fileMock.exists()).willReturn(true);
		return params;
	}

	private FileValidationParams mockExpectValidationExecutedAndFileDoesNotExistAndNotFoundExceptionOccurs() {
		final var params = FileValidationParams.builder().filename(FILENAME).build();
		final var fileMock = mock(File.class);
		BDDMockito.given(fileLocationWrapper.getFile(FILENAME)).willReturn(fileMock);
		BDDMockito.given(fileMock.exists()).willReturn(false);
		return params;
	}
}