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
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import nl.craftsmen.file.repository.filereaders.FileReader;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

@QuarkusTest
class ValidationVR003DoesFileForKeyExistTest {

	private static final String KEY = "iAmAKey";
	public static final String VR003_DESCRIPTION = "VR003: File for key " + KEY + " not found!";

	@InjectMock
	FileReader fileReader;

	@Inject
	ValidationVR003DoesFileForKeyExist cut;

	@Test
	@DisplayName("Given a key, I expect that the validation is executed, that a file is found based on the key and that no exception occurs")
	void expect_validation_is_executed_and_file_is_found_and_no_exception_occurs() {
		// GIVEN
		final var params = mockExpectValidationExecutedAndFileExistsAndNoExceptionOccurs();

		// WHEN / THEN
		assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("Given a key, I expect that the validation is executed, that the file based on the key is not found and that a NotFoundException occurs")
	void expect_validation_is_executed_and_file_is_not_found_and_NotFoundException_occurs() {
		// GIVEN
		final var params = mockExpectValidationExecutedAndFileDoesNotExistAndNotFoundExceptionOccurs();

		// WHEN / THEN
		final var thrown = catchThrowable(() -> cut.validate(params));

		// THEN
		assertThat(thrown)
				.isInstanceOf(NotFoundException.class)
				.hasMessage(VR003_DESCRIPTION);
	}

	@Test
	@DisplayName("Expect no exception and that validation on key is not executed if no key has been provided")
	void expect_no_exception_and_validation_on_key_not_executed_if_no_key_has_been_provided() {
		// GIVEN
		final var params = FileValidationParams.builder().build();

		// WHEN / THEN
		assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
		verify(fileReader, never()).readFileByKey(any());
	}

	@Test
	@DisplayName("Given a filename, when getValidationCodeWithDescription is called, I expect a validation code with a description to be returned")
	void expect_validation_code_with_description_to_be_returned_when_calling_getValidationCodeWithDescription() {
		// GIVEN
		final var params = FileValidationParams.builder().key(KEY).build();

		// WHEN
		final var omschrijving = cut.getValidationCodeWithDescription(params);

		// THEN
		assertThat(omschrijving).isEqualTo(VR003_DESCRIPTION);
	}

	@Test
	@DisplayName("Expect value 3 to be returned when calling getOrder")
	void expect_value_3_to_be_returned_when_calling_getOrder() {
		// GIVEN / WHEN
		final var order = cut.getOrder();

		// THEN
		assertThat(order).isEqualTo(3);
	}

	private FileValidationParams mockExpectValidationExecutedAndFileExistsAndNoExceptionOccurs() {
		final var params = FileValidationParams.builder().key(KEY).build();
		final var fileMock = mock(File.class);
		BDDMockito.given(fileReader.readFileByKey(KEY)).willReturn(Optional.of(fileMock));
		return params;
	}

	private FileValidationParams mockExpectValidationExecutedAndFileDoesNotExistAndNotFoundExceptionOccurs() {
		final var params = FileValidationParams.builder().key(KEY).build();
		BDDMockito.given(fileReader.readFileByKey(KEY)).willReturn(Optional.empty());
		return params;
	}
}