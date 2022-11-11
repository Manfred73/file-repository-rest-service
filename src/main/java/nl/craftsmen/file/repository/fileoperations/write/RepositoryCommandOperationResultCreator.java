package nl.craftsmen.file.repository.fileoperations.write;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class RepositoryCommandOperationResultCreator {

	public Response create() {
		return Response.ok().build();
	}
}
