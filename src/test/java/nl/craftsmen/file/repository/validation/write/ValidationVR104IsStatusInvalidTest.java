package nl.craftsmen.file.repository.validation.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import nl.craftsmen.file.repository.model.FileStatus;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ValidationVR104IsStatusInvalidTest {

	private static final String INVALID_STATUS = "DUH";
	public static final String VR104_DESCRIPTION = "VR104: Unknown status: " + INVALID_STATUS;

	@Inject
	ValidationVR104IsStatusInvalid cut;

	@Test
	@DisplayName("Given a valid status, I expect that the validation is being executed and that no exception occurs")
	void expect_validation_to_be_executed_and_no_exception_occurs() {
		// GIVEN
		final var params = FileValidationParams.builder().status(FileStatus.RECEIVED.name()).build();

		// WHEN / THEN
		assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("Given an invalid status, I expect that the validation is executed and that a BadRequestException occurs")
	void expect_validation_to_be_executed_and_BadRequestException_occurs() {
		// GIVEN
		final var params = FileValidationParams.builder().status(INVALID_STATUS).build();

		// WHEN / THEN
		final var thrown = catchThrowable(() -> cut.validate(params));

		// THEN
		assertThat(thrown)
				.isInstanceOf(BadRequestException.class)
				.hasMessage(VR104_DESCRIPTION);
	}

	@Test
	@DisplayName("Expect no exception and validation not being executed when no status has been provided")
	void expect_no_exception_and_validation_not_being_executed_when_status_has_not_been_provided() {
		// GIVEN
		final var params = FileValidationParams.builder().build();

		// WHEN / THEN
		assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("Given a status, when getValidationCodeWithDescription is called, I expect a validation code with description to be returned")
	void expect_validation_code_with_description_to_be_returned_when_calling_getValidationCodeWithDescription() {
		// GIVEN
		final var params = FileValidationParams.builder().status(INVALID_STATUS).build();

		// WHEN
		final var omschrijving = cut.getValidationCodeWithDescription(params);

		// THEN
		assertThat(omschrijving).isEqualTo(VR104_DESCRIPTION);
	}

	@Test
	@DisplayName("Expect value 4 to be returned when getOrder is called")
	void expect_value_4_to_be_returned_when_calling_getOrder() {
		// GIVEN / WHEN
		final var order = cut.getOrder();

		// THEN
		assertThat(order).isEqualTo(4);
	}
}