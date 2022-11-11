package nl.craftsmen.file.repository.validation.read;

import static nl.craftsmen.file.repository.validation.read.QueryValidationCodeDescriptions.VR002_DESCRIPTION;
import static nl.craftsmen.file.repository.validation.read.QueryValidationCodeDescriptions.VR003_DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import io.quarkus.test.junit.QuarkusTest;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
class QueryValidationCodeTest {

	@Test
	@DisplayName("Given a QueryValidationCode enum, I expect 2 QueryValidationCodes")
	void expect_2_QueryValidationCodes() {
		assertThat(QueryValidationCode.values()).hasSize(2);
	}

	@ParameterizedTest
	@MethodSource("queryValidationCodeGenerator")
	@DisplayName("Given a QueryValidationCode enum, I expect the associated string representation to be returned when calling getValidationCode")
	void getValidationCode(QueryValidationCode queryValidationCode, String expectedQueryValidationCode) {
		assertThat(queryValidationCode.getValidationCode()).isEqualTo(expectedQueryValidationCode);
	}

	@ParameterizedTest
	@MethodSource("descriptionGenerator")
	@DisplayName("Given a QueryValidationCode enum, I expect the associated description to be returned when calling getDescription")
	void getDescription(QueryValidationCode queryValidationCode, String expectedDescription) {
		assertThat(queryValidationCode.getDescription()).isEqualTo(expectedDescription);
	}

	@ParameterizedTest
	@MethodSource("queryValidationCodeWithDescriptionGenerator")
	@DisplayName("Given a QueryValidationCode enum, I expect the associated QueryValidationCode with description to be returned when calling "
			+ "getQueryValidationCodeWithDescription")
	void getQueryValidationCodeWithDescription(QueryValidationCode queryValidationCode, String expectedDescription) {
		assertThat(queryValidationCode.getValidationCodeWithDescription(queryValidationCode)).isEqualTo(expectedDescription);
	}

	@ParameterizedTest
	@EnumSource(QueryValidationCode.class)
	@DisplayName("When the valueOf method is called, I expect no null value")
	void valueOf(QueryValidationCode queryValidationCode) {
		assertThat(QueryValidationCode.valueOf(queryValidationCode.name())).isNotNull();
	}

	private static Stream<Arguments> queryValidationCodeGenerator() {
		return Stream.of(
				Arguments.of(QueryValidationCode.VR002, "VR002"),
				Arguments.of(QueryValidationCode.VR003, "VR003"));
	}

	private static Stream<Arguments> descriptionGenerator() {
		return Stream.of(
				Arguments.of(QueryValidationCode.VR002, VR002_DESCRIPTION),
				Arguments.of(QueryValidationCode.VR003, VR003_DESCRIPTION));
	}

	private static Stream<Arguments> queryValidationCodeWithDescriptionGenerator() {
		return Stream.of(
				Arguments.of(QueryValidationCode.VR002, "VR002: " + VR002_DESCRIPTION),
				Arguments.of(QueryValidationCode.VR003, "VR003: " + VR003_DESCRIPTION));
	}
}