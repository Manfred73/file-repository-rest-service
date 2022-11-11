package nl.craftsmen.file.repository.integrationtests;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.emptyString;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nl.craftsmen.file.repository.fileoperations.write.RepositoryCommandResource;
import nl.craftsmen.file.repository.util.ResourceReader;
import nl.craftsmen.file.repository.util.RepositoryFileUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * For the post / write we need to create a temporary directory, so we don't write any output files to the application's resource path. For the test we copy the
 * input file from the application's classpath to the temporary directory, and we also write the output as a result of the test calling the real code to that
 * temporary directory. After the test has completed we make sure the temporary directory is cleaned and removed. In order to do this, we override the
 * files.location property, so it points to the temporary directory.
 * <p>
 * NOTE: we create a static block to create the temporary directory and return the Path to that folder which is stored in the static filesPath
 * variable. We need to use a static block, because this is called first before calling the BuildTimeValueChangeTestProfile class where we want to override the
 * property.
 */
@QuarkusIntegrationTest
@TestHTTPEndpoint(RepositoryCommandResource.class)
@TestProfile(RepositoryCommandResourceUpdateStatusSuccessIT.BuildTimeValueChangeTestProfile.class)
class RepositoryCommandResourceUpdateStatusSuccessIT {

	private static final String TMP_FILES_LOCATION = "files_command_integration_test";

	static {
		filesPath = RepositoryFileUtil.createTemporaryDirectoryForFiles(TMP_FILES_LOCATION);
	}

	static Path filesPath;

	@Test
	@DisplayName("When a PUT request is made with a key as parameter and with a new valid status in the body, I expect a response code 200 with an empty "
			+ "body")
	void expect_response_with_status_200_and_empty_body_when_calling_put_with_key_as_parameter_and_new_valid_status_in_the_body() throws IOException {
		// GIVEN
		final var testParams = mockForPutWithKeyAsParameterSuccess();

		// WHEN / THEN
		try {
			given()
					.when()
					.body(testParams.newStatus)
					.put("/status/" + testParams.key)
					.then()
					.statusCode(200)
					.body(emptyString());

			final var result = ResourceReader.readFileContentFromPathAsString(testParams.fileMetadataFromTmpDir);
			assertThat(result).doesNotContain(testParams.oldStatus).contains(testParams.newStatus);
		} finally {
			RepositoryFileUtil.deleteFilesAndTmpDir(TMP_FILES_LOCATION);
		}
	}

	private TestParams mockForPutWithKeyAsParameterSuccess() throws IOException {
		final var fileMetadataInput = "FILE.20160919.152122.149067.meta";
		final var fileMetadataFromTmpDir = RepositoryFileUtil.copyFileFromResourceClasspathToTmpDirectory(filesPath, fileMetadataInput);
		final var filenameOutput = "FILE.20160919.152122.149067.meta";
		final var key = "120160212016-09-19          2016092999991231EUR";
		final var oldStatus = "RECEIVED";
		final var newStatus = "PROCESSED";

		// We copy the output file to the repository directory so that it already exists.
		RepositoryFileUtil.copyFile(filesPath, fileMetadataInput, filenameOutput);

		return TestParams.builder()
				.fileMetadataInput(fileMetadataInput)
				.fileMetadataFromTmpDir(fileMetadataFromTmpDir)
				.filenameOutput(filenameOutput)
				.key(key)
				.oldStatus(oldStatus)
				.newStatus(newStatus)
				.build();
	}

	public static class BuildTimeValueChangeTestProfile implements QuarkusTestProfile {
		@Override
		public Map<String, String> getConfigOverrides() {
			return Collections.singletonMap("files.location", filesPath.toString());
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	private static class TestParams {
		private String fileMetadataInput;
		private Path fileMetadataFromTmpDir;
		private String filenameOutput;
		private String key;
		private String oldStatus;
		private String newStatus;
	}
}
