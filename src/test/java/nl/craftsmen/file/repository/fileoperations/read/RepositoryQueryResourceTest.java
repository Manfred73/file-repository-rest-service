package nl.craftsmen.file.repository.fileoperations.read;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import java.io.File;
import javax.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nl.craftsmen.file.repository.util.ResourceReader;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import nl.craftsmen.file.repository.validation.read.RepositoryQueryValidationService;
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
class RepositoryQueryResourceTest {

	@InjectMock
	RepositoryQueryValidationService validationService;

	@InjectMock
	RepositoryQueryService queryService;

	@InjectMock
    RepositoryQueryOperationResultCreator resultCreator;

	private final ArgumentCaptor<FileValidationParams> validationParamsCaptor = ArgumentCaptor.forClass(FileValidationParams.class);

	@Test
	@DisplayName("When a GET request is made with a filename as parameter, I expect the content of the file "
			+ "to be returned as a response")
	void expect_response_with_status_200_and_body_with_file_content_when_calling_get_with_filename_as_parameter() {
		// GIVEN
		final var testParams = mockGetRequestWithFilenameAsParameter();

		// WHEN / THEN
		given()
				.pathParam("filename", testParams.filename)
				.when()
				.get("/v1/files/filename/{filename}")
				.then()
				.statusCode(200)
				.body(is(testParams.fileContent));
		assertValidationParamsForGetRequestWithFilenameAsParameter(testParams);
	}

	@Test
	@DisplayName("When a GET request is made with a key as parameter, I expect the content of the file to be "
			+ "returned as a response")
	void expect_response_with_status_200_and_body_with_file_content_when_calling_get_with_key_as_parameter() {
		// GIVEN
		final var testParams = mockGetRequestWithKeyAsParameter();

		// WHEN / THEN
		given()
				.pathParam("key", testParams.key)
				.when()
				.get("/v1/files/key/{key}")
				.then()
				.statusCode(200)
				.body(is(testParams.fileContent));
		assertValidationParamsForGetRequestWithKeyAsParameter(testParams);
	}

	private TestParams mockGetRequestWithFilenameAsParameter() {
		final var filename = "files/FILE.20160919.152122.149067";
		final var file = ResourceReader.readResourceFromClassLoaderResourceAsFile(filename);
		final var fileContent = ResourceReader.readFileContentFromClassLoaderResourceAsString(filename);
		final var expectedResponse = Uni.createFrom().item(Response.ok(file).build());

		BDDMockito.willDoNothing().given(validationService).validate(validationParamsCaptor.capture());
		BDDMockito.given(queryService.getFileByFilename(filename)).willReturn(file);
		BDDMockito.given(resultCreator.create(file)).willReturn(expectedResponse);

		return TestParams.builder()
				.filename(filename)
				.file(file)
				.fileContent(fileContent)
				.expectedResponse(expectedResponse)
				.build();
	}

	private void assertValidationParamsForGetRequestWithFilenameAsParameter(TestParams testParams) {
		final var validationParams = validationParamsCaptor.getValue();
		assertThat(validationParams)
				.extracting(
						FileValidationParams::getFilename,
						FileValidationParams::getKey,
						FileValidationParams::getRequest)
				.containsExactly(
						testParams.filename,
						null,
						null);
	}

	private TestParams mockGetRequestWithKeyAsParameter() {
		final var key = "120160212016-09-19          2016092999991231EUR";
		final var filename = "files/FILE.20160919.152122.149067";
		final var file = ResourceReader.readResourceFromClassLoaderResourceAsFile(filename);
		final var fileContent = ResourceReader.readFileContentFromClassLoaderResourceAsString(filename);
		final var expectedResponse = Uni.createFrom().item(Response.ok(file).build());

		BDDMockito.willDoNothing().given(validationService).validate(validationParamsCaptor.capture());
		BDDMockito.given(queryService.getFileByKey(key)).willReturn(file);
		BDDMockito.given(resultCreator.create(file)).willReturn(expectedResponse);

		return TestParams.builder()
				.key(key)
				.filename(filename)
				.file(file)
				.fileContent(fileContent)
				.expectedResponse(expectedResponse)
				.build();
	}

	private void assertValidationParamsForGetRequestWithKeyAsParameter(TestParams testParams) {
		final var validationParams = validationParamsCaptor.getValue();
		assertThat(validationParams)
				.extracting(
						FileValidationParams::getFilename,
						FileValidationParams::getKey,
						FileValidationParams::getRequest)
				.containsExactly(
						null,
						testParams.key,
						null);
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	private static class TestParams {
		private String filename;
		private String key;
		private File file;
		private String fileContent;
		private Uni<Response> expectedResponse;
		private String[] files;
	}
}
