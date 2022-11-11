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
 * Inspired by <a href="https://www.baeldung.com/java-quarkus-testing">Java Quarkus Testing</a>.
 */
@QuarkusIntegrationTest
@TestHTTPEndpoint(RepositoryQueryResource.class)
@TestProfile(RepositoryFileLocationNotFoundITProfile.class)
class RepositoryQueryResourceReadByKeyLocationNotFoundIT {

	@Test
	@DisplayName("When a GET request is made with a key as parameter, but when looking in a non-existing location, I expect a NotFoundException with a "
			+ "description")
	void expect_response_with_status_404_when_calling_get_with_key_as_parameter_becauase_file_location_does_not_exist() {
		// GIVEN / WHEN / THEN
		given()
				.pathParam("key", "120160212016-09-19          2016092999991231EUR")
				.when()
				.get("/key/{key}")
				.then()
				.statusCode(404)
				.body(emptyString());
	}
}
