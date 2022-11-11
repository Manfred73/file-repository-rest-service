package nl.craftsmen.file.repository.exceptionhandling;

import javax.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * More details on <a href="https://quarkus.io/guides/resteasy-reactive#exception-mapping">exception mapping with Quarkus</a>.
 */
public class ExceptionMappers {

	@ServerExceptionMapper
	public RestResponse<String> mapException(FileAlreadyExistsException fileAlreadyExistsException) {
		return RestResponse.status(Response.Status.CONFLICT, fileAlreadyExistsException.getMessage());
	}
}
