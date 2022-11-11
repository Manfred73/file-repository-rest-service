package nl.craftsmen.file.repository.integrationtests;

import static io.restassured.RestAssured.given;
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
@TestProfile(RepositoryCommandResourceSaveEmptyFileIT.BuildTimeValueChangeTestProfile.class)
class RepositoryCommandResourceSaveEmptyFileIT {

    private static final String TMP_FILES_LOCATION = "files_command_integration_test";

    static {
        filesPath = RepositoryFileUtil.createTemporaryDirectoryForFiles(TMP_FILES_LOCATION);
    }

    static Path filesPath;

    @Test
    @DisplayName("When a POST request is made with a filename as parameter and if that file is empty, I expect a response code 400 (BadRequest) with a message")
    void expect_response_with_status_400_when_calling_post_with_empty_file() throws IOException {
        // GIVEN
        final var testParams = mockForPostRequestWithEmptyFile();

        try {
            final var fileToPost = ResourceReader.readFileFromResourcePath(testParams.fileInputFromTmpDir);

            // WHEN / THEN
            given()
                    .multiPart("file", fileToPost)
                    .formParam("filename", testParams.filenameOutput)
                    .when()
                    .post()
                    .then()
                    .statusCode(400)
                    .body(emptyString());
        } finally {
            RepositoryFileUtil.deleteFilesAndTmpDir(TMP_FILES_LOCATION);
        }
    }

    private TestParams mockForPostRequestWithEmptyFile() throws IOException {
        final var filenameInput = "FILE.20221101.160000.160000_input";
        final var fileInputFromTmpDir = RepositoryFileUtil.copyFileFromResourceClasspathToTmpDirectory(filesPath, filenameInput);
        final var filenameOutput = "FILE.20221101.160000.160000_output";

        return TestParams.builder()
                .filenameInput(filenameInput)
                .fileInputFromTmpDir(fileInputFromTmpDir)
                .filenameOutput(filenameOutput)
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
        private Path fileInputFromTmpDir;
        private String filenameOutput;
    }
}
