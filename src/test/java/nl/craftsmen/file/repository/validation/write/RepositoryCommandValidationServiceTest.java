package nl.craftsmen.file.repository.validation.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import io.quarkus.test.junit.QuarkusTest;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Inspiration from:
 * <p>
 * <a href="https://access.redhat.com/documentation/en-us/red_hat_build_of_quarkus/1.3/html/testing_your_quarkus_applications/proc-test-mocking-objects">
 * Quarkus Mocking Objects</a>
 */
@QuarkusTest
class RepositoryCommandValidationServiceTest {

	/**
	 * Here the ValidationMock1 and ValidationMock2 are injected, so when the actual code is called, the mocks are used instead of the real validations being
	 * executed.
	 */
	@Inject
	Instance<RepositoryCommandOperationValidation> validations;

	@Inject
	RepositoryCommandValidationService cut;

	@Test
	@DisplayName("Given 2 validations, I expect these to be executed")
	void expect_that_validations_are_being_executed() {
		// WHEN
		final var params = FileValidationParams.builder().build();

		// WHEN
		cut.validate(params);

		// THEN
		// We use spy here because the ValidationMock1 and ValidationMock2 are real beans that are injected (no mocks), so we can't do verify on it.
		validations.forEach(v -> Mockito.spy(v).validate(params));
		Assertions.assertThat(validations).hasSize(1);
	}
}