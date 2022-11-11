package nl.craftsmen.file.repository.integrationtests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;
import nl.craftsmen.file.repository.fileoperations.read.RepositoryQueryResource;
import nl.craftsmen.file.repository.testprofiles.RepositoryFileLocationNotFoundITProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Inspired by:
 * <ul>
 *     <li><a href="https://www.baeldung.com/java-quarkus-testing">Java Quarkus Testing</a>.</li>
 *     <li><a href="https://groups.google.com/g/quarkus-dev/c/zoXRyHFDAYI">JDesign Proposal: @QuarkusIntegrationTest</a>.</li>
 * </ul>
 */
@QuarkusIntegrationTest
@TestHTTPEndpoint(RepositoryQueryResource.class)
@TestProfile(RepositoryFileLocationNotFoundITProfile.class)
class RepositoryQueryResourceReadByFilenameLocationNotFoundIT {

	@Test
	@DisplayName("When a GET request is made with a filename as parameter, but the file location does not exist, I expect a NotFoundException with a "
			+ "description")
	void expect_response_with_status_404_when_calling_get_with_filename_as_parameter_because_file_location_does_not_exist() {
		// GIVEN /  WHEN / THEN
		given()
				.pathParam("filename", "FILE.20160919.152122.149067")
				.when()
				.get("/filename/{filename}")
				.then()
				.statusCode(404)
				.body(emptyString());
	}
}
