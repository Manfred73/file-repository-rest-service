package nl.craftsmen.file.repository.fileoperations.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.mock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import java.io.File;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import nl.craftsmen.file.repository.testprofiles.RepositoryTestProfile;
import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.filereaders.FileReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

@QuarkusTest
@TestProfile(RepositoryTestProfile.class)
class RepositoryQueryServiceTest {

	private static final String FILE_LOCATION = File.separator + "files";
	private static final String FILENAME_1 = "FILE.20160919.152122.149067";
	private static final String KEY_1 = "12010212016-09-19          2016092999991231EUR";
	private static final String KEY_2 = "12010212017-01-21          2017012999991231EUR";

	@InjectMock
	FileLocationWrapper fileLocationWrapper;

	@InjectMock
	FileReader reader;

	@Inject
	RepositoryQueryService cut;

	@Test
	@DisplayName("Given a filename, I expect to get a file back when calling getFileByFilename")
	void expect_a_file_back_when_calling_getFileByFilename() {
		// GIVEN
		final var expectedFile = new File(FILE_LOCATION + File.separator + FILENAME_1);
		BDDMockito.given(fileLocationWrapper.getFile(FILENAME_1)).willReturn(expectedFile);

		// WHEN
		final var file = cut.getFileByFilename(FILENAME_1);

		// THEN
		assertThat(file.getName()).isEqualTo(FILENAME_1);
		assertThat(file.getPath()).isEqualTo(FILE_LOCATION + File.separator + FILENAME_1);
	}

	@Test
	@DisplayName("Given a key, I expect a file back when calling getFileByKey")
	void expect_a_file_back_when_calling_getFileByKey() {
		// GIVEN
		final var fileMock = mock(File.class);
		BDDMockito.given(reader.readFileByKey(KEY_1)).willReturn(Optional.of(fileMock));

		// WHEN
		final var file = cut.getFileByKey(KEY_1);

		// THEN
		assertThat(file).isEqualTo(fileMock);
	}

	@Test
	@DisplayName("Given a filename, I don't expect to get a file back but a NotFoundException when calling "
			+ "getFileByKey")
	void expect_a_NotFoundException_when_calling_getFileByKey() {
		// GIVEN
		BDDMockito.given(reader.readFileByKey(KEY_2)).willReturn(Optional.empty());

		// WHEN
		final var thrown = catchThrowable(() -> cut.getFileByKey(KEY_2));

		// THEN
		assertThat(thrown)
				.isInstanceOf(NotFoundException.class)
				.hasMessage("File with key " + KEY_2 + " not found!");
	}
}
