package nl.craftsmen.file.repository.integrationtests;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nl.craftsmen.file.repository.fileoperations.write.RepositoryCommandResource;
import nl.craftsmen.file.repository.util.RepositoryFileUtil;
import nl.craftsmen.file.repository.util.ResourceReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

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
@TestProfile(RepositoryCommandResourceSaveFileAlreadyExistsIT.BuildTimeValueChangeTestProfile.class)
class RepositoryCommandResourceSaveFileAlreadyExistsIT {

    private static final String TMP_FILES_LOCATIE = "files_command_integration_test";

    static {
        filesPath = RepositoryFileUtil.createTemporaryDirectoryForFiles(TMP_FILES_LOCATIE);
    }

    static Path filesPath;

    @Test
    @DisplayName("When a POST request is made with a filename as parameter which already exists in the file repository, I expect a response code 409 "
            + "(Conflict) with a een message")
    void expect_response_with_status_409_when_calling_post_with_file_that_already_exists() throws IOException {
        // GIVEN
        final var testParams = mockForPostRequestWithFileThatAlreadyExists();

        // WHEN / THEN
        try {
            final var fileToPost = ResourceReader.readFileFromResourcePath(testParams.fileInputFromTmpDir);

            given()
                    .multiPart("file", fileToPost)
                    .formParam("filename", testParams.filenameOutput)
                    .when()
                    .post()
                    .then()
                    .statusCode(409)
                    .body(is("VR102: File with filename FILE.20160919.152122.149067_output already exists and cannot be overridden!"));
        } finally {
            RepositoryFileUtil.deleteFilesAndTmpDir(TMP_FILES_LOCATIE);
        }
    }

    private TestParams mockForPostRequestWithFileThatAlreadyExists() throws IOException {
        final var filenameInput = "FILE.20160919.152122.149067_input";
        final var fileFromTmpDir = RepositoryFileUtil.copyFileFromResourceClasspathToTmpDirectory(filesPath, filenameInput);
        final var filenameOutput = "FILE.20160919.152122.149067_output";

        // We copy the output file to the repository directory so that it already exists.
        RepositoryFileUtil.copyFile(filesPath, filenameInput, filenameOutput);

        return TestParams.builder()
                .filenameInput(filenameInput)
                .fileInputFromTmpDir(fileFromTmpDir)
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
