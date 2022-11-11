package nl.craftsmen.file.repository.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@RegisterForReflection
public class FileMetadata {

	/**
	 * The key of the file which identifies the file as unique.
	 */
	private String key;

	/**
	 * The {@link FileStatus} of the file.
	 */
	private FileStatus status;

	/**
	 * The filename of the metadata file.
	 */
	private String filename;
}
