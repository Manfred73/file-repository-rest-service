package nl.craftsmen.file.repository.integrationtests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;
import nl.craftsmen.file.repository.fileoperations.read.RepositoryQueryResource;
import nl.craftsmen.file.repository.testprofiles.RepositoryFileQueryITProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Inspired by <a href="https://www.baeldung.com/java-quarkus-testing">Java Quarkus Testing</a>.
 */
@QuarkusIntegrationTest
@TestHTTPEndpoint(RepositoryQueryResource.class)
@TestProfile(RepositoryFileQueryITProfile.class)
class RepositoryQueryResourceReadByKeyNotFoundIT {

	@Test
	@DisplayName("When a GET request is made with a key as parameter for which no metadata file exists, I expect a NotFoundException with a description")
	void expect_response_with_status_404_when_calling_get_with_key_as_parameter_because_file_metadata_cannot_be_found_based_on_key() {
		// GIVEN
		final var key = "120160212020-09-19          2016092999991231EUR";

		// WHEN / THEN
		given()
				.pathParam("key", key)
				.when()
				.get("/key/{key}")
				.then()
				.statusCode(404)
				.body(emptyString());
	}
}
