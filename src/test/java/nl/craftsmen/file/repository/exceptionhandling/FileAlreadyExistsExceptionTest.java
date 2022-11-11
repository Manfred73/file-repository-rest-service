package nl.craftsmen.file.repository.exceptionhandling;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FileAlreadyExistsExceptionTest {

	@Test
	@DisplayName("When creating a FileAlreadyExistsException with a given file, I expect that this exception is "
			+ "constructed with a message and filename and without a cause")
	void expect_a_FileAlreadyExistsException_with_message_containing_file_and_without_cause() {
		// GIVEN
		final var filename = "filename.txt";
		final var message = "File with filename %s already exists and cannot be overridden!";
		final var expectedMessage = "File with filename " + filename + " already exists and cannot be overridden!";

		// WHEN
		final var exception = new FileAlreadyExistsException(String.format(message, filename));

		// THEN
		assertThat(exception)
				.extracting(
						Throwable::getMessage,
						Throwable::getCause)
				.containsExactly(
						expectedMessage,
						null);
	}
}