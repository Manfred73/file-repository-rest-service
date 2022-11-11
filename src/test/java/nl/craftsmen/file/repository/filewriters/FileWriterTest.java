package nl.craftsmen.file.repository.filewriters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.util.ResourceReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

@QuarkusTest
class FileWriterTest {

    @InjectMock
    FileLocationWrapper fileLocationWrapper;

    @Inject
    FileWriter cut;

    @Test
    @DisplayName("When calling writeFile, I expect that a file has been written")
    void expect_file_to_be_written_when_calling_writeFile(@TempDir Path tempDir) throws IOException {
        // GIVEN
        final var testParams = mockForWriteFileSuccess(tempDir);

        // WHEN
        cut.writeFile(testParams.inputFile, testParams.outputFilename);

        // THEN
        expectFileToBeWrittenAndOutputIsSameAsInput(testParams);
    }

    @Test
    @DisplayName("When calling writeFile with a non-existing file, I expect an InternalServerErrorException and that file has not been written")
    void expect_InternalServerErrorException_when_calling_writeFile(@TempDir Path tempDir) {
        // GIVEN
        final var testParams = mockForWriteFileWithInputThatDoesNotExist(tempDir);

        // WHEN
        final var thrown = catchThrowable(() -> cut.writeFile(testParams.inputFile, testParams.outputFilename));

        // THEN
        expectAnInternalServerErrorExceptionAndThatFileHasNotBeenWritten(testParams, thrown);
    }

    @Test
    @DisplayName("When calling writeFile with a non-existing file, I expect an InternalServerErrorException because the faulty file cannot be removed")
    @Disabled("Mocking Files.readAllBytes throws a java.lang.VerifyError. Is this an issue with mockito/bytebuddy? How can we test this differently?")
    void expect_InternalServerErrorException_when_calling_writeFile_because_faulty_file_cannot_be_removed(@TempDir Path tempDir) {
        // GIVEN
        final var testParams = mockForWriteFileWithInputThatDoesNotExist(tempDir);

        // WHEN
        try (var files = Mockito.mockStatic(Files.class)) {
            files.when(() -> Files.readAllBytes(testParams.inputFile.toPath())).thenThrow(new IOException());
            files.when(() -> Files.deleteIfExists(testParams.outputFilePath)).thenThrow(new IOException());

            final var thrown = catchThrowable(() -> cut.writeFile(testParams.inputFile, testParams.outputFilename));

            // THEN
            expectInternalServerErrorExceptionAndThatFaultyFileHasBeenCreatedAndCannotBeRemovedWhenExceptionOccurs(testParams, thrown);
        }
    }

    private TestParams mockForWriteFileSuccess(Path tempDir) {
        final var inputFilename = "files/FILE.20160919.152122.149067";
        final var inputFile = ResourceReader.readResourceFromClassLoaderResourceAsFile(inputFilename);
        final var inputFilePath = inputFile.toPath();
        final var outputFilename = "ICTOBGCL.20190920.172172.179078";
        final var outputFilePath = tempDir.resolve(outputFilename);

        BDDMockito.given(fileLocationWrapper.getFile(outputFilename)).willReturn(outputFilePath.toFile());

        return TestParams.builder()
                .inputFilename(inputFilename)
                .inputFile(inputFile)
                .inputFilePath(inputFilePath)
                .outputFilename(outputFilename)
                .outputFilePath(outputFilePath)
                .build();
    }

    private TestParams mockForWriteFileWithInputThatDoesNotExist(Path tempDir) {
        final var inputFile = new File("onzin");
        final var outputFilename = "ICTOBGCL.20190920.172172.179078";
        final var outputFilePath = tempDir.resolve(outputFilename);

        BDDMockito.given(fileLocationWrapper.getFile(outputFilename)).willReturn(outputFilePath.toFile());

        return TestParams.builder()
                .inputFile(inputFile)
                .outputFilename(outputFilename)
                .outputFilePath(outputFilePath)
                .build();
    }

    private static void expectFileToBeWrittenAndOutputIsSameAsInput(TestParams testParams) throws IOException {
        assertThat(testParams.outputFilePath).exists();
        assertThat(doesOutputMatchWithInput(testParams)).isTrue();
    }

    private static boolean doesOutputMatchWithInput(TestParams testParams) throws IOException {
        return Files.mismatch(testParams.inputFilePath, testParams.outputFilePath) == -1;
    }

    private static void expectAnInternalServerErrorExceptionAndThatFileHasNotBeenWritten(TestParams testParams, Throwable thrown) {
        assertThat(testParams.outputFilePath).doesNotExist();
        assertThat(thrown)
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error saving file " + testParams.outputFilename + ": ")
                .hasCauseInstanceOf(IOException.class);
    }

    private void expectInternalServerErrorExceptionAndThatFaultyFileHasBeenCreatedAndCannotBeRemovedWhenExceptionOccurs(TestParams testParams,
			Throwable thrown) {
        assertThat(new File(testParams.outputFilePath.toString())).exists();
        assertThat(thrown)
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error removing faulty file " + testParams.outputFilename + ": ")
                .hasCauseInstanceOf(IOException.class);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class TestParams {
        private String inputFilename;
        private File inputFile;
        private Path inputFilePath;
        private String outputFilename;
        private Path outputFilePath;
    }
}
