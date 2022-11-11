package nl.craftsmen.file.repository.testprofiles;

import io.quarkus.test.junit.QuarkusTestProfile;

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
 */
public class RepositoryTestProfile implements QuarkusTestProfile {

	@Override
	public String getConfigProfile() {
		return "test";
	}
}
