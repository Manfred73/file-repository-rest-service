package nl.craftsmen.file.repository.filereaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import io.quarkus.test.junit.QuarkusTest;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import nl.craftsmen.file.repository.util.ResourceReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FileHeaderReaderTest {

	@Inject
	FileHeaderReader cut;

	@Test
	@DisplayName("Expect a key of 47 characters from file given file")
	void expect_key_of_47_characters_from_given_file() {
		// GIVEN
		final var expectedHeader = "120160212016-09-19          2016092999991231EUR";
		final var file = ResourceReader.readResourceFromClassLoaderResourceAsFile("files/FILE.20160919.152122.149067");

		// WHEN
		final var actualHeader = cut.readFileHeader(file);

		// THEN
		assertThat(actualHeader).isEqualTo(expectedHeader);
	}

	@Test
	@DisplayName("Given a non-existing file, when readFileHeader is called with that file, I expect an InternalServerErrorException")
	void expect_InternalServerErrorException_when_calling_readFileHeader_with_non_existing_file() {
		// GIVEN
		final var nonExistingFile = new File("iDoNotExist");

		// WHEN
		final var thrown = catchThrowable(() -> cut.readFileHeader(nonExistingFile));

		// THEN
		assertThat(thrown)
				.isInstanceOf(InternalServerErrorException.class)
				.hasMessage("Error reading header (key) from file: ")
				.hasCauseInstanceOf(IOException.class);
	}

	@Test
	@DisplayName("Given a file with a header that is too short, when calling readFileHeader with that file, I expect an InternalServerErrorException")
	void expect_InternalServerErrorException_when_calling_readFileHeader_with_header_too_short() {
		// GIVEN
		final var fileWithHeaderTooShort = ResourceReader.readResourceFromClassLoaderResourceAsFile("files_file_header_too_short/FILE.20160919.152122.149067");

		// WHEN
		final var thrown = catchThrowable(() -> cut.readFileHeader(fileWithHeaderTooShort));

		// THEN
		assertThat(thrown)
				.isInstanceOf(InternalServerErrorException.class)
				.hasMessage("Error reading header (key) from file - header is shorter than 47 characters: ")
				.hasCauseInstanceOf(StringIndexOutOfBoundsException.class);
	}
}
