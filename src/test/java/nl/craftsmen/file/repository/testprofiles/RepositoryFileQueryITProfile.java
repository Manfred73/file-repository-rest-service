package nl.craftsmen.file.repository.testprofiles;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Collections;
import java.util.Map;
import nl.craftsmen.file.repository.util.ResourceReader;

/**
 * For details about test profiles, see:
 * <ul>
 *     <li><a href="https://quarkus.io/guides/getting-started-testing">Getting started testing</a></li>
 * 	   <li><a href="https://quarkus.io/guides/getting-started-testing#writing-a-profile">Writing a profile</a></li>
 * </ul>
 * <p>
 * Mocking configurations like classes with @ConfigMapping cannot be easily mocked (see
 * <a href="https://github.com/quarkusio/quarkus/issues/15235">Mock config classes</a>).
 * The proposed way is to use test profiles for this.
 * <p>
 * Also note the order in which properties are handled: <a href="https://quarkus.io/guides/config-reference">config reference</a>.
 * If you have .env file for local development which contains a certain property, but also an application-test.yaml with the same property, the one from .env
 * will take precedence.
 * <p>
 * BEWARE!!! when running tests with @QuarkusIntegrationTest, overriding the getConfigProfile() method has no effect because @QuarkusIntegrationTest
 * always results in the production {@code application.yaml} being used, unless you have a {@code application-test.yaml}. In that case properties will be
 * read from {@code application-test.yaml} instead of {@code application.yaml}.
 * Having a {@code application-integration.yaml} and trying to override the default {@code application.yaml} in the getConfigProfile() has no effect.
 * See Quarkus GitHub:
 * <ul>
 *     <li>
 *         <a href="https://github.com/quarkusio/quarkus/issues/28962#issuecomment-1299970125">Debugging underlying service code when using
 *             QuarkusIntegrationTest?
 *         </a>
 *     </li>
 *     <li>
 *         <a href="https://github.com/quarkusio/quarkus/issues/24581">Make Quarkus*IntegrationTest runs use a dedicated "integration" profile for runtime
 *     			config
 *     	   </a>
 *     </li>
 * </ul>
 */
public class RepositoryFileQueryITProfile implements QuarkusTestProfile {

	/**
	 * Here we override the files.location property. The files for testing are bundled as test resources. Overriding this property is
	 * necessary so that the integration tests know where to find the files on the classpath. This makes it OS and environment independent.
	 */
	@Override
	public Map<String, String> getConfigOverrides() {
		final var filesLocation = ResourceReader.getAbsolutePathFromClassLoaderResourceAsString("files_query_integration_test");
		return Collections.singletonMap("files.location", filesLocation);
	}
}
