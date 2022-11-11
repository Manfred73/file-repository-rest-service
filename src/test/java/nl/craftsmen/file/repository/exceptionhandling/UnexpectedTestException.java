package nl.craftsmen.file.repository.exceptionhandling;

import java.io.Serial;
import lombok.experimental.StandardException;

@StandardException
// TODO: remove the constructors once the new Intellij Lombok plugin recognizes this
// See: https://github.com/mplushnikov/lombok-intellij-plugin/issues/1076
public class UnexpectedTestException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 4333735099643688342L;

	private static final String MESSAGE = "Didn't expect an exception to be thrown!";

	public UnexpectedTestException() {
		super(MESSAGE);
	}

	public UnexpectedTestException(Exception exception) {
		super(MESSAGE, exception);
	}
}
