package nl.craftsmen.file.repository.model;

import static nl.craftsmen.file.repository.model.FileStatus.PROCESSED;
import static nl.craftsmen.file.repository.model.FileStatus.REJECTED;
import static nl.craftsmen.file.repository.model.FileStatus.APPROVED;
import static nl.craftsmen.file.repository.model.FileStatus.RECEIVED;
import static nl.craftsmen.file.repository.model.FileStatus.INCOMPLETELY_PROCESSED;
import static org.assertj.core.api.Assertions.assertThat;
import io.quarkus.test.junit.QuarkusTest;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
class FileStatusTest {

	@ParameterizedTest(name = "{index}: fileStatus {0}, description {1}")
	@MethodSource("descriptionByFileStatusGenerator")
	@DisplayName("Given a FileStatus enum, I expect the correct description to be returned")
	void getByType(FileStatus status, String description) {
		assertThat(FileStatus.getDescriptionByFileStatus(status)).isEqualTo(description);
	}

	@ParameterizedTest(name = "{index}: description {0}, fileStatus {1}")
	@MethodSource("fileStatusByDescriptionGenerator")
	@DisplayName("Given the description of a FileStatus enum, I expect the correct FileStatus enum to be returned")
	void getByValue(String description, FileStatus status) {
		assertThat(FileStatus.getFileStatusByDescription(description)).isEqualTo(status);
	}

	@ParameterizedTest(name = "{index}: description {0}, expected result {1}")
	@MethodSource("isValidGenerator")
	@DisplayName("Given the description of a FileStatus enum, when isValid method is called, I expect true or false")
	void isValid(String description, boolean expectedResult) {
		assertThat(FileStatus.isValid(description)).isEqualTo(expectedResult);
	}

	@ParameterizedTest
	@EnumSource(FileStatus.class)
	@DisplayName("When the valueOf method is called, I expect no null value")
	void valueOf(FileStatus status) {
		assertThat(FileStatus.valueOf(status.name())).isNotNull();
	}

	private static Stream<Arguments> descriptionByFileStatusGenerator() {
		return Stream.of(
				Arguments.of(RECEIVED, "File has been received and saved"),
				Arguments.of(APPROVED, "File has been successfully validated"),
				Arguments.of(REJECTED, "File has not been successfully validated"),
				Arguments.of(PROCESSED, "File has been successfully processed"),
				Arguments.of(INCOMPLETELY_PROCESSED, "File has not been successfully processed"));
	}

	private static Stream<Arguments> fileStatusByDescriptionGenerator() {
		return Stream.of(
				Arguments.of("File has been received and saved", RECEIVED),
				Arguments.of("File has been successfully validated", APPROVED),
				Arguments.of("File has not been successfully validated", REJECTED),
				Arguments.of("File has been successfully processed", PROCESSED),
				Arguments.of("File has not been successfully processed", INCOMPLETELY_PROCESSED),
				Arguments.of("Unknown value", null),
				Arguments.of(null, null));
	}

	private static Stream<Arguments> isValidGenerator() {
		return Stream.of(
				Arguments.of("File has been received and saved", true),
				Arguments.of("File has been successfully validated", true),
				Arguments.of("File has not been successfully validated", true),
				Arguments.of("File has been successfully processed", true),
				Arguments.of("File has not been successfully processed", true),
				Arguments.of(null, true),
				Arguments.of("Unknown value", false));
	}
}