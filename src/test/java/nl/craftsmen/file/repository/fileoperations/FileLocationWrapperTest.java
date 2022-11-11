package nl.craftsmen.file.repository.fileoperations;

import static org.assertj.core.api.Assertions.assertThat;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import java.io.File;
import java.nio.file.Path;

import javax.inject.Inject;
import nl.craftsmen.file.repository.testprofiles.RepositoryTestProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(RepositoryTestProfile.class)
class FileLocationWrapperTest {

	public static final String FILE_LOCATION = File.separator + "files";

	@Inject
	FileLocationWrapper cut;

	@Test
	@DisplayName("When calling getFileBaseDirAsFile, I expect base dir file location to be returned as a File")
	void expect_file_location_to_be_returned_as_File_when_calling_getFileBaseDirAsFile() {
		// GIVEN / WHEN
		final var result = cut.getFileBaseDirAsFile();

		// THEN
		assertThat(result.getPath()).isEqualTo(FILE_LOCATION);
	}

	@Test
	@DisplayName("When calling getFileBaseDirAsPath, I expect base dir location to be returned as a Path")
	void expect_file_location_to_be_returned_as_Path_when_calling_getFileBaseDirAsPath() {
		// GIVEN / WHEN
		final var result = cut.getFileBaseDirAsPath();

		// THEN
		assertThat(result).isEqualTo(Path.of(FILE_LOCATION));
	}

	@Test
	@DisplayName("When calling getFileLocationAsString, I expect base dir location to be returned as a String")
	void expect_file_location_to_be_returned_as_String_when_calling_getFileLocationAsString() {
		// GIVEN / WHEN
		final var result = cut.getFileLocationAsString();

		// THEN
		assertThat(result).isEqualTo(FILE_LOCATION);
	}

	@Test
	@DisplayName("When calling getFile, I expect a file to be returned")
	void expect_a_file_to_be_returned_when_calling_getFile() {
		// GIVEN
		final var filename = "testfile";

		// WHEN
		final var result = cut.getFile(filename);

		// THEN
		assertThat(result.getPath()).isEqualTo(FILE_LOCATION + File.separator + filename);
	}
}
