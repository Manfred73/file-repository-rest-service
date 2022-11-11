package nl.craftsmen.file.repository.exceptionhandling;

import java.io.Serial;
import lombok.experimental.StandardException;

@StandardException
// TODO: remove the constructors once the new Intellij Lombok plugin recognizes this
// See: https://github.com/mplushnikov/lombok-intellij-plugin/issues/1076
public class ResourceException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -1498789968926282996L;

	private static final String MESSAGE = "Resource cannot be found or unexpected error finding resource";

	public ResourceException() {
		super(MESSAGE);
	}

	public ResourceException(Exception exception) {
		super(MESSAGE, exception);
	}
}
