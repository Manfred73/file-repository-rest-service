package nl.craftsmen.file.repository.fileoperations.read;

import io.smallrye.mutiny.Uni;
import java.io.File;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class RepositoryQueryOperationResultCreator {

	public Uni<Response> create(File file) {
		final var response = Response.ok(file);
		response.header("Content-Disposition", "attachment;filename=" + file);
		return Uni.createFrom().item(response.build());
	}

	public Uni<Response> create(Set<String> files) {
		final var response = Response.ok(files);
		return Uni.createFrom().item(response.build());
	}
}
