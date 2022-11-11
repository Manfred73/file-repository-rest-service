package nl.craftsmen.file.repository.fileoperations.write;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import nl.craftsmen.file.repository.validation.write.RepositoryCommandValidationService;
import org.jboss.resteasy.reactive.MultipartForm;

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
 * <p>
 *
 * For the time being, returning Multipart data is limited to be blocking endpoints. See:
 * <a href="https://quarkus.io/guides/resteasy-reactive">Quarkus Reasteasy Reactive Tutorial</a>
 */
@Path("/v1/files")
public class RepositoryCommandResource {

	@Inject
	RepositoryCommandValidationService validationService;

	@Inject
	RepositoryCommandService commandService;

	@Inject
    RepositoryCommandOperationResultCreator resultCreator;

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response saveFile(@Valid @MultipartForm RepositoryCreateRequest repositoryCreateRequest) {
		validationService.validate(FileValidationParams.builder()
				.request(repositoryCreateRequest)
				.build());
		commandService.saveFileAndCreateMetadata(repositoryCreateRequest);
		return resultCreator.create();
	}

	@PUT
	@Encoded
	@Path("/status/{key}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateFileStatus(String key, String status) {
		validationService.validate(FileValidationParams.builder()
				.status(status)
				.build());
		commandService.updateMetadataStatus(key, status);
		return resultCreator.create();
	}
}
