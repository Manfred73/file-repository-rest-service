package nl.craftsmen.file.repository.filewriters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nl.craftsmen.file.repository.exceptionhandling.UnexpectedTestException;
import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.filereaders.FileHeaderReader;
import nl.craftsmen.file.repository.model.FileMetadata;
import nl.craftsmen.file.repository.model.FileStatus;
import nl.craftsmen.file.repository.util.RepositoryFileUtil;
import nl.craftsmen.file.repository.util.ResourceReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.BDDMockito;

@QuarkusTest
class FileMetadataWriterTest {

    @Inject
    ObjectMapper mapper;

    @InjectMock
    FileLocationWrapper fileLocationWrapper;

    @InjectMock
    FileHeaderReader fileHeaderReader;

    @Inject
    FileMetadataWriter cut;

    @Test
    @DisplayName("When calling createMetadataFile, I expect a MetadataFile is written based on given file")
    void expect_metadata_file_to_be_written_when_calling_createMetadataFile(@TempDir Path tempDir) throws IOException {
        // GIVEN
        final var testParams = mockForCreateMetadataFile(tempDir);

        // WHEN
        cut.createMetadataFile(testParams.file, testParams.filename);

        // THEN
        expectFileMetadataHasBeenWritten(testParams);
    }

    @Test
    @DisplayName("When calling updateMetadataFile, I expect that the content of the MetadataFile has been changed based on the given FileMetadata")
    void expect_metatdata_file_has_changed_when_calling_updateMetadataFile(@TempDir Path tempDir) throws IOException {
        // GIVEN
        final var testParams = mockForUpdateMetadataFile(tempDir);

        // WHEN
        cut.updateMetadataFile(testParams.updatedFileMetadata);

        // THEN
        expectFileMetadataHasBeenChanged(testParams);
    }

    @Test
    @DisplayName("When calling updateMetadataFile with an invalid metadat object, I expect an InternalServerErrorException")
    void expect_InternalServerErrorException_when_calling_updateMetadataFile_with_invalid_metadata_object(@TempDir Path tempDir) throws IOException {
        // GIVEN
        final var testParams = mockForUpdateMetadataFileWithInvalidMetadataObject(tempDir);

        // WHEN
        final var thrown = catchThrowable(() -> cut.updateMetadataFile(testParams.updatedFileMetadata));

        // THEN
        expectFileMetadataIncorrectAndNotSaved(testParams, thrown);
    }

    private TestParams mockForCreateMetadataFile(Path tempDir) {
        final var filename = "FILE.20160919.152122.149067";
        final var filenameMetadata = "FILE.20160919.152122.149067.meta";
        final var key = "120160212016-09-19          2016092999991231EUR";
        final var file = ResourceReader.readResourceFromClassLoaderResourceAsFile("files" + File.separator + filename);
        final var outputFileMetadata = tempDir.resolve(filenameMetadata).toFile();
        final var updatedFileMetadata = FileMetadata.builder()
                .key(key)
                .status(FileStatus.RECEIVED)
                .filename(filename)
                .build();

        BDDMockito.given(fileHeaderReader.readFileHeader(file)).willReturn(key);
        BDDMockito.given(fileLocationWrapper.getFile(filenameMetadata)).willReturn(outputFileMetadata);

        assertThat(outputFileMetadata).doesNotExist();

        return TestParams.builder()
                .tempDir(tempDir)
                .filename(filename)
                .filenameMetadata(filenameMetadata)
                .key(key)
                .file(file)
                .outputFileMetadata(outputFileMetadata)
                .updatedFileMetadata(updatedFileMetadata)
                .build();
    }

    private TestParams mockForUpdateMetadataFile(Path tempDir) throws IOException {
        final var filename = "FILE.20160919.152122.149067";
        final var filenameMetadata = "FILE.20160919.152122.149067.meta";
        final var key = "120160212016-09-19          2016092999991231EUR";

        // First we copy the test file from the classpath to the tempDir.
        final var fileLocation = ResourceReader.getAbsolutePathFromClassLoaderResourceAsPath("files");
        final var fileMetadataFromTmpDir = RepositoryFileUtil.copyFileFromResourceClasspathToTmpDirectory(
                fileLocation, filenameMetadata, tempDir);

        // Then we read it into a FileMetadata object.
        final var initialFileMetadata = mapper.readValue(fileMetadataFromTmpDir.toFile(), FileMetadata.class);

        final var outputFileMetadata = tempDir.resolve(filenameMetadata).toFile();
        final var updatedFileMetadata = FileMetadata.builder()
                .key(key)
                .status(FileStatus.RECEIVED)
                .filename(filename)
                .build();

        BDDMockito.given(fileLocationWrapper.getFile(filenameMetadata)).willReturn(outputFileMetadata);

        assertThat(outputFileMetadata).exists();

        return TestParams.builder()
                .tempDir(tempDir)
                .filename(filename)
                .filenameMetadata(filenameMetadata)
                .key(key)
                .outputFileMetadata(outputFileMetadata)
                .initialFileMetadata(initialFileMetadata)
                .updatedFileMetadata(updatedFileMetadata)
                .build();
    }

    private TestParams mockForUpdateMetadataFileWithInvalidMetadataObject(Path tempDir) throws IOException {
        final var filename = "FILE.20160919.152122.149067";
        final var filenameMetadata = "FILE.20160919.152122.149067.meta";
        final var key = "120160212016-09-19          2016092999991231EUR";

        // First we copy the test file from the classpath to the tempDir.
        final var fileLocation = ResourceReader.getAbsolutePathFromClassLoaderResourceAsPath("files_metadata_incorrect");
        final var fileMetadataFromTmpDir = RepositoryFileUtil.copyFileFromResourceClasspathToTmpDirectory(fileLocation,
                filenameMetadata, tempDir);

        // Then we read it into a String.
        final var initialFileMetadataAsString = ResourceReader.readFileContentAsString(fileMetadataFromTmpDir.toFile());

        final var outputFileMetadata = tempDir.resolve(filenameMetadata).toFile();
        final var updatedFileMetadata = FileMetadata.builder()
                .key(key)
                .status(FileStatus.RECEIVED)
                .filename(filename)
                .build();

        BDDMockito.given(fileLocationWrapper.getFile(filenameMetadata)).willThrow(new UnexpectedTestException());

        assertThat(outputFileMetadata).exists();

        return TestParams.builder()
                .tempDir(tempDir)
                .filename(filename)
                .filenameMetadata(filenameMetadata)
                .key(key)
                .outputFileMetadata(outputFileMetadata)
                .initialFileMetadataAsString(initialFileMetadataAsString)
                .updatedFileMetadata(updatedFileMetadata)
                .updatedFileMetadataAsString(updatedFileMetadata.toString())
                .build();
    }

    private void expectFileMetadataHasBeenWritten(TestParams testParams) throws IOException {
        final var actualFileMetadata =
                mapper.readValue(ResourceReader.readFileFromResourceName(testParams.tempDir + File.separator + testParams.filenameMetadata),
                        FileMetadata.class);
        assertThat(testParams.outputFileMetadata).exists();
        assertThat(actualFileMetadata).isEqualTo(testParams.updatedFileMetadata);
        verify(fileHeaderReader).readFileHeader(testParams.file);
        verify(fileLocationWrapper).getFile(testParams.filenameMetadata);
    }

    private void expectFileMetadataHasBeenChanged(TestParams testParams) throws IOException {
        final var updatedFileMetadata = mapper.readValue(
                ResourceReader.readFileFromResourceName(testParams.tempDir + File.separator + testParams.filenameMetadata), FileMetadata.class);
        assertThat(testParams.outputFileMetadata).exists();
        assertThat(testParams.initialFileMetadata).isNotEqualTo(updatedFileMetadata);
        assertThat(updatedFileMetadata).isEqualTo(testParams.updatedFileMetadata);
        verify(fileLocationWrapper).getFile(testParams.filenameMetadata);
    }

    private void expectFileMetadataIncorrectAndNotSaved(TestParams testParams, Throwable thrown) {
        final var updatedFileMetadataAsString = ResourceReader.readFileContentFromResourceAsString(
                testParams.tempDir + File.separator + testParams.filenameMetadata);
        assertThat(testParams.outputFileMetadata).exists();
        assertThat(testParams.initialFileMetadataAsString).isEqualTo(updatedFileMetadataAsString);
        assertThat(updatedFileMetadataAsString).isNotEqualTo(testParams.updatedFileMetadataAsString);
        verify(fileLocationWrapper).getFile(testParams.filenameMetadata);
        assertThat(thrown)
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error saving file metadata " + testParams.filename + ": ")
                .hasCauseInstanceOf(UnexpectedTestException.class);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class TestParams {
        private Path tempDir;
        private String filename;
        private String filenameMetadata;
        private String key;
        private File file;
        private File outputFile;
        private File outputFileMetadata;
        private FileMetadata initialFileMetadata;
        private String initialFileMetadataAsString;
        private FileMetadata updatedFileMetadata;
        private String updatedFileMetadataAsString;
    }
}