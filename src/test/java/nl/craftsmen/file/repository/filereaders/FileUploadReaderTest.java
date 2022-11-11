package nl.craftsmen.file.repository.filereaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.mock;
import io.quarkus.test.junit.QuarkusTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import nl.craftsmen.file.repository.util.ResourceReader;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

@QuarkusTest
class FileUploadReaderTest {

	@Inject
	FileUploadReader cut;

	@Test
	@DisplayName("Given an empty file, I expect true when calling isFileUploadEmpty method")
	void expect_true_when_calling_isFileUploadEmpty_when_file_is_empty() {
		// GIVEN
		final var fileUpload = mock(FileUpload.class);
		final var path = mock(Path.class);
		final var nonEmptyFile = ResourceReader.readResourceFromClassLoaderResourceAsFile("files_file_empty_not_empty/file_empty.txt");
		BDDMockito.given(fileUpload.uploadedFile()).willReturn(path);
		BDDMockito.given(path.toFile()).willReturn(nonEmptyFile);

		// WHEN
		final var result = cut.isFileUploadEmpty(fileUpload);

		// THEN
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("Given a non-empty file, I expect false when calling isFileUploadEmpty method")
	void expect_false_when_calling_isFileUploadEmpty_with_non_empty_file() {
		// GIVEN
		final var fileUpload = mock(FileUpload.class);
		final var path = mock(Path.class);
		final var nonEmptyFile = ResourceReader.readResourceFromClassLoaderResourceAsFile("files_file_empty_not_empty/file_not_empty.txt");
		BDDMockito.given(fileUpload.uploadedFile()).willReturn(path);
		BDDMockito.given(path.toFile()).willReturn(nonEmptyFile);

		// WHEN
		final var result = cut.isFileUploadEmpty(fileUpload);

		// THEN
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("Given a file, when calling isFileUploadEmpty method, I expect an InternalServerErrorException because an IOException occurred")
	void expect_InternalServerErrorException_when_calling_isFileUploadEmpty_if_IOException_occurs() {
		// GIVEN
		final var fileUpload = mock(FileUpload.class);
		final var path = mock(Path.class);
		final var nonExistingFile = new File("NotToBe.txt");
		BDDMockito.given(fileUpload.uploadedFile()).willReturn(path);
		BDDMockito.given(path.toFile()).willReturn(nonExistingFile);

		// WHEN
		final var thrown = catchThrowable(() -> cut.isFileUploadEmpty(fileUpload));

		// THEN
		assertThat(thrown)
				.isInstanceOf(InternalServerErrorException.class)
				.hasMessage("Error determining if file is empty: ")
				.hasCauseInstanceOf(IOException.class);
	}
}
