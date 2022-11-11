package nl.craftsmen.file.repository.fileoperations.write;

import static org.assertj.core.api.Assertions.assertThat;
import io.quarkus.test.junit.QuarkusTest;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RepositoryCommandOperationResultCreatorTest {

	private final RepositoryCommandOperationResultCreator cut = new RepositoryCommandOperationResultCreator();

	@Test
	@DisplayName("When calling the create method, I expect a response OK (200) and and empty body")
	void expect_a_response_code_200_and_an_empty_body_when_calling_create_method() {
		// GIVEN / WHEN
		final var actualResponse = cut.create();

		// THEN
		expectAResponseOKAndEmptyBody(actualResponse);
	}

	private void expectAResponseOKAndEmptyBody(Response actualResponse) {
		assertThat(actualResponse)
				.extracting(
						Response::getStatus,
						Response::getEntity)
				.containsExactly(
						200,
						null);
	}
}