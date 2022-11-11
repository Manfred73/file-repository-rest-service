package nl.craftsmen.file.repository.validation.write;

import static nl.craftsmen.file.repository.validation.write.CommandValidationCodeDescriptions.VR102_DESCRIPTION;
import static nl.craftsmen.file.repository.validation.write.CommandValidationCodeDescriptions.VR103_DESCRIPTION;
import static nl.craftsmen.file.repository.validation.write.CommandValidationCodeDescriptions.VR104_DESCRIPTION;
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
class CommandValidationCodeTest {

    @Test
    @DisplayName("Given a CommandValidationCode enum, I expect 3 CommandValidationCodes")
    void expect_3_CommandValidationCodes() {
        assertThat(CommandValidationCode.values()).hasSize(3);
    }

    @ParameterizedTest
    @MethodSource("commandValidationCodeGenerator")
    @DisplayName("Given a CommandValidationCode enum, I expect the associated string representation to be returned when calling getValidationCode")
    void getCommandValidatiecode(CommandValidationCode commandValidationCode, String expectedCommandValidationCode) {
        assertThat(commandValidationCode.getValidationCode()).isEqualTo(expectedCommandValidationCode);
    }

    @ParameterizedTest
    @MethodSource("descriptionGenerator")
    @DisplayName("Given a CommandValidatiecode enum, I expect the associated description to be returned when calling getDescription")
    void getOmschrijving(CommandValidationCode commandValidationCode, String expectedDescription) {
        assertThat(commandValidationCode.getDescription()).isEqualTo(expectedDescription);
    }

    @ParameterizedTest
    @MethodSource("commandValidatieCodeWithDescriptionGenerator")
    @DisplayName("Given a CommandValidationCode enum, I expect the associated CommandValidationCode with description to be returned when calling "
            + "getValidationCodeWithDescription")
    void getCommandValidatiecodeMetOmschrijving(CommandValidationCode commandValidationCode, String expectedDescription) {
        assertThat(commandValidationCode.getValidationCodeWithDescription(commandValidationCode)).isEqualTo(expectedDescription);
    }

    @ParameterizedTest
    @EnumSource(CommandValidationCode.class)
    @DisplayName("When the valueOf method is called, I expect no null value")
    void valueOf(CommandValidationCode commandValidationCode) {
        assertThat(CommandValidationCode.valueOf(commandValidationCode.name())).isNotNull();
    }

    private static Stream<Arguments> commandValidationCodeGenerator() {
        return Stream.of(
                Arguments.of(CommandValidationCode.VR102, "VR102"),
                Arguments.of(CommandValidationCode.VR103, "VR103"),
                Arguments.of(CommandValidationCode.VR104, "VR104"));
    }

    private static Stream<Arguments> descriptionGenerator() {
        return Stream.of(
                Arguments.of(CommandValidationCode.VR102, VR102_DESCRIPTION),
                Arguments.of(CommandValidationCode.VR103, VR103_DESCRIPTION),
                Arguments.of(CommandValidationCode.VR104, VR104_DESCRIPTION));
    }

    private static Stream<Arguments> commandValidatieCodeWithDescriptionGenerator() {
        return Stream.of(
                Arguments.of(CommandValidationCode.VR102, "VR102: " + VR102_DESCRIPTION),
                Arguments.of(CommandValidationCode.VR103, "VR103: " + VR103_DESCRIPTION),
                Arguments.of(CommandValidationCode.VR104, "VR104: " + VR104_DESCRIPTION));
    }
}