package nl.craftsmen.file.repository.integrationtests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;
import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nl.craftsmen.file.repository.fileoperations.read.RepositoryQueryResource;
import nl.craftsmen.file.repository.testprofiles.RepositoryFileQueryITProfile;
import nl.craftsmen.file.repository.util.ResourceReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Inspired by <a href="https://www.baeldung.com/java-quarkus-testing">Java Quarkus Testing</a>.
 */
@QuarkusIntegrationTest
@TestHTTPEndpoint(RepositoryQueryResource.class)
@TestProfile(RepositoryFileQueryITProfile.class)
class RepositoryQueryResourceReadByKeySuccessIT {

	@Test
	@DisplayName("When a GET request is made with a key as parameter, I expect the content of the file as a response")
	void expect_response_with_status_200_and_body_with_file_content_when_calling_get_with_key_as_parameter() {
		// GIVEN
		final var testParams = mockForGetWithKeyAsParameterSuccess();

		// WHEN / THEN
		given()
				.pathParam("key", testParams.key)
				.when()
				.get("/key/{key}")
				.then()
				.statusCode(200)
				.body(is(testParams.fileContent));
	}

	private TestParams mockForGetWithKeyAsParameterSuccess() {
		final var fileLocation = "files_query_integration_test";
		final var filename = "FILE.20160919.152122.149067";
		final var fileContent = ResourceReader.readFileContentFromClassLoaderResourceAsString(fileLocation + File.separator + filename);
		final var key = "120160212016-09-19          2016092999991231EUR";

		return TestParams.builder()
				.fileLocation(fileLocation)
				.filename(filename)
				.fileContent(fileContent)
				.key(key)
				.build();
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	private static class TestParams {
		private String fileLocation;
		private String filename;
		private String fileContent;
		private String key;
	}
}
