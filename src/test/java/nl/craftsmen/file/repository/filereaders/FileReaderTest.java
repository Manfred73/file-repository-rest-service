package nl.craftsmen.file.repository.filereaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import java.io.File;
import java.util.HashMap;
import javax.inject.Inject;

import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.model.FileMetadata;
import nl.craftsmen.file.repository.testprofiles.RepositoryTestProfile;
import nl.craftsmen.file.repository.exceptionhandling.UnexpectedTestException;
import nl.craftsmen.file.repository.model.FileStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

@QuarkusTest
@TestProfile(RepositoryTestProfile.class)
class FileReaderTest {

	private static final String FILE_META_1 = "FILE.20160919.152122.149067.meta";
	private static final String FILE_1 = "FILE.20160919.152122.149067";
	private static final String FILE_META_2 = "FILE.20170121.172172.179077.meta";
	private static final String FILE_2 = "FILE.20170121.172172.179077";
	private static final String KEY1 = "12010212016-09-19          2016092999991231EUR";
	private static final String KEY2 = "12010212017-01-21          2017012999991231EUR";

	@InjectMock
	FileMetadataReader fileMetadataReader;

	@InjectMock
    FileLocationWrapper fileLocationWrapper;

	@Inject
	FileReader cut;

	@Test
	@DisplayName("When readFileByKey is called with an existing key, I expect a file to be returned")
	void expect_a_file_to_be_returned_when_calling_readFileByKey() {
		// GIVEN
		final var fileMock = mockReadFileByKey();

		// WHEN
		final var actualFile = cut.readFileByKey(KEY2);

		// THEN
		actualFile.map(file -> assertThat(file).isEqualTo(fileMock))
				.orElseThrow(() -> new UnexpectedTestException("Expected file, but was not returned."));
	}

	@Test
	@DisplayName("When calling readFileByKey with a non-existing key, I expect no file to be returned")
	void expect_no_file_to_be_returned_when_calling_readFileByKey_with_invalid_key() {
		// GIVEN
		mockReadFileByKey();

		// WHEN
		final var actualFile = cut.readFileByKey("trash");

		// THEN
		assertThat(actualFile).isEmpty();
	}

	private File mockReadFileByKey() {
		final var metadataFiles = new HashMap<String, FileMetadata>();
		metadataFiles.put(FILE_META_1, FileMetadata.builder()
				.key(KEY1)
				.filename(FILE_1)
				.status(FileStatus.RECEIVED)
				.build());
		metadataFiles.put(FILE_META_2,
				FileMetadata.builder()
						.key(KEY2)
						.filename(FILE_2)
						.status(FileStatus.RECEIVED)
						.build());

		final var fileMock = mock(File.class);
		BDDMockito.given(fileMetadataReader.readAllFileMetadata()).willReturn(metadataFiles);
		BDDMockito.given(fileLocationWrapper.getFile(FILE_2)).willReturn(fileMock);
		return fileMock;
	}
}
