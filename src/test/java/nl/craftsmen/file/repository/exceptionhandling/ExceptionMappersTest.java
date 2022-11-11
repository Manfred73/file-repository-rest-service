package nl.craftsmen.file.repository.exceptionhandling;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ExceptionMappersTest {

	@Test
	@DisplayName("When a FileAlreadyExistsException occurs, I expect a Conflict (409) response with an error message")
	void expect_a_response_code_409_and_error_message_if_FileAlreadyExistsException_occurs() {
		// GIVEN
		final var cut = new ExceptionMappers();
		final var filename = "filename.txt";
		final var message = "File with filename %s already exists and cannot be overridden!";
		final var expectedMessage = "File with filename " + filename + " already exists and cannot be overridden!";
		final var exception = new FileAlreadyExistsException(String.format(message, filename));

		// WHEN
		try (var response = cut.mapException(exception)) {

			// THEN
			assertThat(response.toResponse().getStatus()).isEqualTo(409);
			assertThat(response.toResponse().getEntity()).isEqualTo(expectedMessage);
		}
	}
}