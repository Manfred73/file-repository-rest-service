package nl.craftsmen.file.repository.validation.shared;

import static nl.craftsmen.file.repository.validation.read.QueryValidationCodeDescriptions.VR001_DESCRIPTION;
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
class SharedValidationCodeTest {

    @Test
    @DisplayName("Given SharedValidationCode enum, I expect 1 SharedValidationCode")
    void expect_1_SharedValidationCode() {
        assertThat(SharedValidationCode.values()).hasSize(1);
    }

    @ParameterizedTest
    @MethodSource("sharedValidationCodeGenerator")
    @DisplayName("Given a SharedValidationCode enum, I expect the associated string representation to be returned when calling getValidationCode")
    void getSharedValidationCode(SharedValidationCode sharedValidationCode, String expectedSharedValidationCode) {
        assertThat(sharedValidationCode.getValidationCode()).isEqualTo(expectedSharedValidationCode);
    }

    @ParameterizedTest
    @MethodSource("descriptionGenerator")
    @DisplayName("Given a SharedValidationCode enum, I expect the associated description to be returned when calling getDescription")
    void getDescription(SharedValidationCode sharedValidationCode, String expectedDescription) {
        assertThat(sharedValidationCode.getDescription()).isEqualTo(expectedDescription);
    }

    @ParameterizedTest
    @MethodSource("sharedValidationCodeWithDescriptionGenerator")
    @DisplayName("Given a SharedValidationCode enum, I expect the associated SharedValidationCode to be returned with a description when calling "
            + "getValidationCodeWithDescription")
    void getSharedValidatiecodeMetOmschrijving(SharedValidationCode sharedValidationCode, String expectedDescription) {
        assertThat(sharedValidationCode.getValidationCodeWithDescription(sharedValidationCode)).isEqualTo(expectedDescription);
    }

    @ParameterizedTest
    @EnumSource(SharedValidationCode.class)
    @DisplayName("When the valueOf method is called, I expect no null value")
    void valueOf(SharedValidationCode sharedValidationCode) {
        assertThat(SharedValidationCode.valueOf(sharedValidationCode.name())).isNotNull();
    }

    private static Stream<Arguments> sharedValidationCodeGenerator() {
        return Stream.of(
                Arguments.of(SharedValidationCode.VR001, "VR001"));
    }

    private static Stream<Arguments> descriptionGenerator() {
        return Stream.of(
                Arguments.of(SharedValidationCode.VR001, VR001_DESCRIPTION));
    }

    private static Stream<Arguments> sharedValidationCodeWithDescriptionGenerator() {
        return Stream.of(
                Arguments.of(SharedValidationCode.VR001, "VR001: " + VR001_DESCRIPTION));
    }
}