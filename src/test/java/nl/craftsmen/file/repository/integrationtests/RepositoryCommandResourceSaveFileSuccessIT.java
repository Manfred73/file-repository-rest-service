package nl.craftsmen.file.repository.integrationtests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import java.io.File;
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
@TestProfile(RepositoryCommandResourceSaveFileSuccessIT.BuildTimeValueChangeTestProfile.class)
class RepositoryCommandResourceSaveFileSuccessIT {

	private static final String TMP_FILES_LOCATION = "files_command_integration_test";

	static {
		filesPath = RepositoryFileUtil.createTemporaryDirectoryForFiles(TMP_FILES_LOCATION);
	}

	static Path filesPath;

	@Test
	@DisplayName("When a POST request is made with a filename as parameter, I expect response code 200 with an empty body")
	void expect_response_with_status_200_and_empty_body_when_calling_post_with_filenam_as_parameter() throws IOException {
		// GIVEN
		final var testParams = mockForPostRequestSuccess();

		// WHEN / THEN
		try {
			given()
					.multiPart("file", testParams.fileToPost)
					.formParam("filename", testParams.filenameOutput)
					.when()
					.post()
					.then()
					.statusCode(200)
					.body(emptyString());
		} finally {
			RepositoryFileUtil.deleteFilesAndTmpDir(TMP_FILES_LOCATION);
		}
	}

	private TestParams mockForPostRequestSuccess() throws IOException {
		final var filenameInput = "FILE.20160919.152122.149067_input";
		final var fileFromTmpDir = RepositoryFileUtil.copyFileFromResourceClasspathToTmpDirectory(filesPath, filenameInput);
		final var filenameOutput = "FILE.20160919.152122.149067_output";
		final var fileToPost = ResourceReader.readFileFromResourcePath(fileFromTmpDir);

		return TestParams.builder()
				.filenameInput(filenameInput)
				.fileFromTmpDir(fileFromTmpDir)
				.filenameOutput(filenameOutput)
				.fileToPost(fileToPost)
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
		private String filenameInput;
		private Path fileFromTmpDir;
		private String filenameOutput;
		private File fileToPost;
	}
}
