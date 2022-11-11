package nl.craftsmen.file.repository.fileoperations.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.verify;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nl.craftsmen.file.repository.filereaders.FileMetadataReader;
import nl.craftsmen.file.repository.filewriters.FileMetadataWriter;
import nl.craftsmen.file.repository.filewriters.FileWriter;
import nl.craftsmen.file.repository.model.FileMetadata;
import nl.craftsmen.file.repository.model.FileStatus;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

@QuarkusTest
class RepositoryCommandServiceTest {

	@InjectMock
    FileWriter fileWriter;

	@InjectMock
    FileMetadataWriter fileMetadataWriter;

	@InjectMock
    FileMetadataReader metadataReader;

	private final ArgumentCaptor<FileMetadata> metadataCaptor = ArgumentCaptor.forClass(FileMetadata.class);

	@Inject
	RepositoryCommandService cut;

	@Test
	@DisplayName("Given a repositoryCreateRequest, when saveFileAndCreateMetadata is called, I expect that a file has"
			+ " been saved in the file repository and that a metadata file has been created")
	void expect_file_and_metadata_created_when_calling_saveFileAndCreateMetadata() {
		// GIVEN
		final var testParams = mockSaveFileAndCreateMetadata();

		// WHEN
		cut.saveFileAndCreateMetadata(testParams.request);

		// THEN
		expectFileSavedAndMetadataCreated(testParams);
	}

	@Test
	@DisplayName("When updateMetadataStatus is called, I expect a valid status that the metadata has been updated")
	void expect_valid_status_that_metatdata_has_been_updated_when_calling_updateMetadataStatus() {
		// GIVEN
		final var key = "testkey";
		final var metadata = FileMetadata
				.builder()
				.key(key)
				.status(FileStatus.RECEIVED)
				.build();
		BDDMockito.given(metadataReader.readFileMetadataByKey(key)).willReturn(Optional.of(metadata));

		// WHEN
		cut.updateMetadataStatus(key, FileStatus.PROCESSED.name());

		// THEN
		verify(fileMetadataWriter).updateMetadataFile(metadataCaptor.capture());
		final var actualMetadata = metadataCaptor.getValue();
		assertThat(actualMetadata).extracting(FileMetadata::getStatus, FileMetadata::getKey)
				.containsExactly(FileStatus.PROCESSED, key);
	}

	@Test
	@DisplayName("When updateMetadataStatus is called with an invalid key, I expcect a NotFoundException")
	void expect_NotFoundException_when_calling_updateMetadataStatus_with_invalid_key() {
		// GIVEN
		final var trash = "trash";
		BDDMockito.given(metadataReader.readFileMetadataByKey(trash)).willReturn(Optional.empty());

		// WHEN
		final var thrown = catchThrowable(() -> cut.updateMetadataStatus(trash, trash));

		// THEN
		assertThat(thrown)
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Metadata file with key " + trash + " not found!");
	}

	private TestParams mockSaveFileAndCreateMetadata() {
		final var request = Mockito.mock(RepositoryCreateRequest.class);
		final var fileUpload = Mockito.mock(FileUpload.class);
		final var path = Mockito.mock(Path.class);
		final var file = Mockito.mock(File.class);
		final var filename = "testfile.txt";

		BDDMockito.given(request.getFile()).willReturn(fileUpload);
		BDDMockito.given(fileUpload.uploadedFile()).willReturn(path);
		BDDMockito.given(fileUpload.uploadedFile()).willReturn(path);
		BDDMockito.given(path.toFile()).willReturn(file);
		BDDMockito.given(request.getFilename()).willReturn(filename);

		return TestParams.builder()
				.request(request)
				.fileUpload(fileUpload)
				.path(path)
				.file(file)
				.filename(filename)
				.build();
	}

	private void expectFileSavedAndMetadataCreated(TestParams testParams) {
		verify(fileMetadataWriter).createMetadataFile(testParams.file, testParams.filename);
		verify(fileWriter).writeFile(testParams.file, testParams.filename);
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	private static class TestParams {
		private RepositoryCreateRequest request;
		private FileUpload fileUpload;
		private Path path;
		private File file;
		private String filename;
	}
}
