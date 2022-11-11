package nl.craftsmen.file.repository.validation.write;

import java.util.Comparator;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import nl.craftsmen.file.repository.validation.FileValidationParams;

/**
 * This class contains a list of all validations that implement the {@link RepositoryCommandOperationValidation}. Inspired by
 * <a href="https://stackoverflow.com/questions/63080232/inject-list-of-beans-implementing-same-interface">Inject list of beans implementing some interface</a>
 * <p>
 * The order of injected validations is not guaranteed (there's no @Order annotation available like in Spring), so we first sort them based on the order set in
 * the validation.
 */
@ApplicationScoped
public class RepositoryCommandValidationService {

	@Inject
	Instance<RepositoryCommandOperationValidation> validations;

	public void validate(FileValidationParams params) {
		final var validationsSorted = validations.stream()
				.sorted(Comparator.comparingInt(RepositoryCommandOperationValidation::getOrder))
				.toList();
		validationsSorted.forEach(v -> v.validate(params));
	}
}
