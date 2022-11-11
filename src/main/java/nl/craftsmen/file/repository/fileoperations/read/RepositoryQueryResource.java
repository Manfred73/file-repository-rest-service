package nl.craftsmen.file.repository.fileoperations.read;

import io.smallrye.mutiny.Uni;
import javax.inject.Inject;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import nl.craftsmen.file.repository.validation.read.RepositoryQueryValidationService;

/**
 * This is inspired by the following sources:
 * <ul>
 *     <li><a href="https://quarkus.io/guides/rest-client-multipart">Quarkus Rest Client Multipart</a></li>
 *     <li><a href="https://quarkus.io/guides/rest-client-reactive#multipart">Quarkus Rest Client Reactive</a></li>
 *     <li><a href="https://quarkus.io/guides/resteasy-reactive#handling-multipart-form-data">Quarkus Resteasy: Handling multipart form data</a></li>
 *     <li>
 *        <a href="https://github.com/ozkanpakdil/quarkus-examples/blob/master/reactive-file-download/src/main/java/org/acme/quickstart/ReactiveGreetingResource.java">
 *  *     Quarkus Reactive File Download</a>
 *     </li>
 * </ul>
 */
@Path("/v1/files")
public class RepositoryQueryResource {

	@Inject
	RepositoryQueryValidationService validationService;

	@Inject
	RepositoryQueryService queryService;

	@Inject
    RepositoryQueryOperationResultCreator resultCreator;

	@GET
	@Path("/filename/{filename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Uni<Response> readFileByFilename(@PathParam("filename") String filename) {
		validationService.validate(FileValidationParams.builder().filename(filename).build());
		return resultCreator.create(queryService.getFileByFilename(filename));
	}

	@GET
	@Encoded
	@Path("/key/{key}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Uni<Response> readFileByKey(@PathParam("key") String key) {
		 validationService.validate(FileValidationParams.builder().key(key).build());
		 return resultCreator.create(queryService.getFileByKey(key));
	}
}
