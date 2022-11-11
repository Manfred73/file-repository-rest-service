package nl.craftsmen.file.repository.exceptionhandling;

import java.io.Serial;
import lombok.experimental.StandardException;

@StandardException
public class FileAlreadyExistsException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -4008420560191191195L;

	public FileAlreadyExistsException(String message) {
		super(message);
	}
}
