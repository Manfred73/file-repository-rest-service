package nl.craftsmen.file.repository.fileoperations.read;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

import java.io.File;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class RepositoryQueryOperationResultCreatorTest {

    private final File file = Mockito.mock(File.class);

    private final RepositoryQueryOperationResultCreator cut = new RepositoryQueryOperationResultCreator();

    @Test
    @DisplayName("Given a file, when calling the create method with a file, I expect a response OK (200) with a "
            + " header and a file")
    void expect_a_response_code_200_and_that_response_contains_header_and_file() {
        // GIVEN / WHEN
        final var actualResponse = cut.create(file);

        // THEN
        expectAResponseOKAndThatResponseContainsHeaderAndFile(actualResponse);
    }

    @Test
    @DisplayName("Given a set of filenames, when calling the create method with these filenames, I expect a response "
            + "OK (200) with the filenames")
    void expect_a_response_code_200_and_that_response_contains_filenames() {
        // GIVEN
        final var files = Set.of("file001.txt", "file002.txt");

        // WHEN
        final var actualResponse = cut.create(files);

        // THEN
        expectAResponseOKAndThatResponseContainsFilenames(actualResponse, files);
    }

    private void expectAResponseOKAndThatResponseContainsHeaderAndFile(Uni<Response> actualResponse) {
        final var result = actualResponse.subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted()
                .getItem();
        assertThat(result)
                .extracting(
                        Response::getStatus,
                        Response::getEntity)
                .containsExactly(
                        200,
                        file);
        assertThat(result.getHeaders())
                .hasSize(1)
                .containsEntry("Content-Disposition", List.of("attachment;filename=" + file));
    }

    private void expectAResponseOKAndThatResponseContainsFilenames(Uni<Response> actualResponse,
            Set<String> expectedFiles) {
        final var result = actualResponse.subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted()
                .getItem();
        assertThat(result)
                .extracting(
                        Response::getStatus,
                        Response::getEntity)
                .containsExactly(
                        200,
                        expectedFiles);
    }
}