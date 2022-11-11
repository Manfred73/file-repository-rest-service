package nl.craftsmen.file.repository.filereaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;

import nl.craftsmen.file.repository.model.FileMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.model.FileStatus;
import nl.craftsmen.file.repository.util.ResourceReader;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
class FileMetadataReaderTest {

	@InjectMock
    FileLocationWrapper fileLocationWrapper;

	@Inject
	@SuppressWarnings("unused" /* should be injected to prevent NullPointerException in tests */)
	ObjectMapper mapper;

	@Inject
	FileMetadataReader cut;

	@Test
	@DisplayName("When calling readAllFileMetadata, I expect to get back all metadata")
	void expect_to_get_back_all_metadata_when_calling_readAllFileMetadata() {
		// GIVEN
		final var fileLocation = ResourceReader.readResourceFromClassLoaderResourceAsFile("files").toPath();
		BDDMockito.given(fileLocationWrapper.getFileBaseDirAsPath()).willReturn(fileLocation);

		// WHEN
		final var actualMetadataMap = cut.readAllFileMetadata();

		// THEN
		assertThat(actualMetadataMap).hasSize(2).containsAllEntriesOf(getExpectedFileMetadata());
	}

	@Test
	@DisplayName("When calling readAllFileMetadata with non-existing directory, I expect an IOException")
	void expect_IOException_when_calling_readAllFileMetadata_with_non_existing_directory() {
		// GIVEN
		final var fileLocation = new File("nonsense").toPath();
		BDDMockito.given(fileLocationWrapper.getFileBaseDirAsPath()).willReturn(fileLocation);

		// WHEN
		final var thrown = catchThrowable(() -> cut.readAllFileMetadata());

		// THEN
		assertThat(thrown)
				.isInstanceOf(InternalServerErrorException.class)
				.hasMessage("Error retrieving metadata files: ")
				.hasCauseInstanceOf(IOException.class);
	}

	@Test
	@DisplayName("When calling readAllFileMetadata with empty directory, I expect no metadata")
	void expect_empty_map_when_calling_readAllFileMetadata_with_empty_directory() {
		// GIVEN
		final var fileLocation = ResourceReader.readResourceFromClassLoaderResourceAsFile("files_directory_empty").toPath();
		BDDMockito.given(fileLocationWrapper.getFileBaseDirAsPath()).willReturn(fileLocation);

		// WHEN
		final var actualMetadataMap = cut.readAllFileMetadata();

		// THEN
		assertThat(actualMetadataMap).isEmpty();
	}

	@Test
	@DisplayName("When calling readAllFileMetadata with .meta file with invalid content, I expect an InternalServerErrorException")
	void expect_InternalServerErrorException_when_calling_readAllFileMetadata_with_incorrect_meta_file() {
		// GIVEN
		final var fileLocation = ResourceReader.readResourceFromClassLoaderResourceAsFile("files_metadata_incorrect").toPath();
		BDDMockito.given(fileLocationWrapper.getFileBaseDirAsPath()).willReturn(fileLocation);

		// WHEN
		final var thrown = catchThrowable(() -> cut.readAllFileMetadata());

		// THEN
		assertThat(thrown)
				.isInstanceOf(InternalServerErrorException.class)
				.hasMessage("Error converting file FILE.20160919.152122.149067.meta to metadata: ")
				.hasCauseInstanceOf(IOException.class);
	}

	@Test
	@DisplayName("When calling readFileMetadataByKey with existing key, I expect file associated with that key to be returned")
	void expect_metadata_file_to_be_returned_when_calling_readFileMetadataByKey_with_valid_key() {
		// GIVEN
		final var key = "120160212016-09-19          2016092999991231EUR";
		final var fileLocation = ResourceReader.readResourceFromClassLoaderResourceAsFile("files").toPath();
		BDDMockito.given(fileLocationWrapper.getFileBaseDirAsPath()).willReturn(fileLocation);

		// WHEN
		final var actualFile = cut.readFileMetadataByKey(key);

		// THEN
		assertThat(actualFile).map(FileMetadata::getKey).hasValue(key);
	}

	@Test
	@DisplayName("When calling readFileMetadataByKey with non-existing key, I expect no result")
	void expect_no_result_when_calling_readFileMetadataByKey_with_invalid_key() {
		// GIVEN
		final var key = "nonsense";
		final var fileLocation = ResourceReader.readResourceFromClassLoaderResourceAsFile("files").toPath();
		BDDMockito.given(fileLocationWrapper.getFileBaseDirAsPath()).willReturn(fileLocation);

		// WHEN
		final var actualMetadata = cut.readFileMetadataByKey(key);

		// THEN
		assertThat(actualMetadata).isEmpty();
	}


	private static Map<String, FileMetadata> getExpectedFileMetadata() {
		final var expectedMetadata1 = FileMetadata
				.builder()
				.key("120160212016-09-19          2016092999991231EUR")
				.status(FileStatus.PROCESSED)
				.filename("FILE.20160919.152122.149067")
				.build();
		final var expectedMetadata2 = FileMetadata
				.builder()
				.key("120160212019-09-19          2019092999991231EUR")
				.status(FileStatus.RECEIVED)
				.filename("FILE.20190920.172172.179077")
				.build();
		return Map.of("FILE.20160919.152122.149067.meta", expectedMetadata1, "FILE.20190920.172172.179077.meta", expectedMetadata2);
	}
}
