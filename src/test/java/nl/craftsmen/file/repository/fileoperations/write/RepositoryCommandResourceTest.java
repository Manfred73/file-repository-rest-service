package nl.craftsmen.file.repository.fileoperations.write;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.mockito.Mockito.verify;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import java.io.File;
import javax.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nl.craftsmen.file.repository.model.FileStatus;
import nl.craftsmen.file.repository.util.ResourceReader;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import nl.craftsmen.file.repository.validation.write.RepositoryCommandValidationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;

/**
 * Inspired by:
 * <ul>
 *     <li><a href="https://quarkus.io/guides/getting-started-testing">Getting started testing</a></li>
 *     <li><a href="https://quarkus.io/blog/mocking/">Mocking</a></li>
 * </ul>
 */
@QuarkusTest
class RepositoryCommandResourceTest {

	@InjectMock
	RepositoryCommandValidationService validationService;

	@InjectMock
	RepositoryCommandService commandService;

	@InjectMock
	RepositoryCommandOperationResultCreator resultCreator;

	private final ArgumentCaptor<FileValidationParams> validationParamsCaptor = ArgumentCaptor.forClass(FileValidationParams.class);
	private final ArgumentCaptor<RepositoryCreateRequest> requestCaptor = ArgumentCaptor.forClass(RepositoryCreateRequest.class);

	@Test
	@DisplayName("When a POST request is made with formdata filename=<String>, file=<File>, I expect an OK (200) "
			+ "response back")
	void expect_response_with_status_200_and_body_with_file_content_when_calling_get_with_filename_as_parameter() {
		// GIVEN
		final var testParams = mockPost();

		// WHEN / THEN
		given()
				.multiPart("file", testParams.file)
				.formParam("filename", testParams.filename)
				.when()
				.post("/v1/files")
				.then()
				.statusCode(200)
				.body(emptyString());
		assertValidationParamsAndRequestForPost(testParams);
	}

	@Test
	@DisplayName("When a PUT request is made with a key and status, I expect an OK (200) response back")
	void expect_response_witht_status_200_when_calling_put_with_key_and_status() {
		// GIVEN
		final var testParams = mockPut();

		// WHEN / THEN
		given()
				.contentType("text/plain")
				.body(testParams.status)
				.put("/v1/files/status/" + testParams.key)
				.then()
				.statusCode(200)
				.body(emptyString());
		assertValidationParamsForPut(testParams);
	}

	private TestParams mockPost() {
		final var filename = "FILE.20160919.152122.149067";
		final var file = ResourceReader.readResourceFromClassLoaderResourceAsFile("files/" + filename);
		final var expectedResponse = Response.ok().build();

		BDDMockito.willDoNothing().given(validationService).validate(validationParamsCaptor.capture());
		BDDMockito.willDoNothing().given(commandService).saveFileAndCreateMetadata(requestCaptor.capture());
		BDDMockito.given(resultCreator.create()).willReturn(expectedResponse);

		return TestParams.builder()
				.filename(filename)
				.file(file)
				.expectedResponse(expectedResponse)
				.build();
	}

	private TestParams mockPut() {
		final var key = "Locke";
		final var status = FileStatus.PROCESSED.name();
		final var expectedResponse = Response.ok().build();

		BDDMockito.willDoNothing().given(validationService).validate(validationParamsCaptor.capture());
		BDDMockito.willDoNothing().given(commandService).updateMetadataStatus(key, status);
		BDDMockito.given(resultCreator.create()).willReturn(expectedResponse);

		return TestParams.builder()
				.key(key)
				.status(status)
				.expectedResponse(expectedResponse)
				.build();
	}

	private void assertValidationParamsAndRequestForPost(TestParams testParams) {
		final var validationParams = validationParamsCaptor.getValue();
		final var request = requestCaptor.getValue();
		assertThat(validationParams)
				.extracting(
						FileValidationParams::getKey,
						FileValidationParams::getFilename,
						FileValidationParams::getStatus,
						FileValidationParams::getRequest)
				.containsExactly(
						null,
						null,
						null,
						request);
		verify(validationService).validate(validationParams);
		verify(commandService).saveFileAndCreateMetadata(request);
		assertThat(request.getFilename()).isEqualTo(testParams.filename);
	}

	private void assertValidationParamsForPut(TestParams testParams) {
		final var validationParams = validationParamsCaptor.getValue();
		assertThat(validationParams)
				.extracting(
						FileValidationParams::getKey,
						FileValidationParams::getFilename,
						FileValidationParams::getStatus,
						FileValidationParams::getRequest)
				.containsExactly(
						null,
						null,
						testParams.status,
						null);
		verify(validationService).validate(validationParams);
		verify(commandService).updateMetadataStatus(testParams.key, testParams.status);
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	private static class TestParams {
		private String key;
		private String status;
		private String filename;
		private File file;
		private Response expectedResponse;
	}
}
