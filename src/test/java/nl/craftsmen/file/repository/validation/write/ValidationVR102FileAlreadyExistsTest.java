package nl.craftsmen.file.repository.validation.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import java.io.File;
import javax.inject.Inject;

import nl.craftsmen.file.repository.exceptionhandling.FileAlreadyExistsException;
import nl.craftsmen.file.repository.fileoperations.FileLocationWrapper;
import nl.craftsmen.file.repository.fileoperations.write.RepositoryCreateRequest;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

@QuarkusTest
class ValidationVR102FileAlreadyExistsTest {

    private static final String FILENAME = "testfile.txt";
    public static final String VR102_DESCRIPTION = "VR102: File with filename " + FILENAME + " already exists and cannot be overridden!";

    @InjectMock
    FileLocationWrapper fileLocationWrapper;

    @Inject
    ValidationVR102FileAlreadyExists cut;

    @Test
    @DisplayName("Given a request with a filename and file, I expect that the validation is being executed, that the file does not exist and no exception "
            + "occurs")
    void expect_validation_to_be_executed_and_file_does_not_exist_and_no_exception_occurs() {
        // GIVEN
        final var params = mockExpectValidationExecutedAndFileDoesNotExistAndNoExceptionOccurs();

        // WHEN / THEN
        assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Given a request with a filename, I expect the that the validation is being executed, that the file exists and that a "
            + "FileAlreadyExistsException occurs")
    void expect_validation_to_be_executed_and_file_exists_and_FileAlreadyExistsException_occurs() {
        // GIVEN
        final var params = mockExpectValidationExecutedAndFileExistsAndFileAlreadyExistsExceptionOccurs();

        // WHEN / THEN
        final var thrown = catchThrowable(() -> cut.validate(params));

        // THEN
        assertThat(thrown)
                .isInstanceOf(FileAlreadyExistsException.class)
                .hasMessage(VR102_DESCRIPTION);
    }

    @Test
    @DisplayName("Expect no exception and that validation for request with filename is not executed when no request had been provided")
    void expect_no_exception_and_validation_not_being_executed_when_no_request_has_been_provided() {
        // GIVEN
        final var params = FileValidationParams.builder().build();

        // WHEN / THEN
        assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
        verify(fileLocationWrapper, never()).getFile(any());
    }

    @Test
    @DisplayName("Given a request with a filename, when getValidationCodeWithDescription is called, I expect a validation code with a description to be "
            + "returned")
    void expect_validation_code_with_description_to_be_returned_when_calling_getValidationCodeWithDescription() {
        // GIVEN
        final var request = RepositoryCreateRequest.builder().filename(FILENAME).build();
        final var params = FileValidationParams.builder().request(request).build();

        // WHEN
        final var description = cut.getValidationCodeWithDescription(params);

        // THEN
        assertThat(description).isEqualTo(VR102_DESCRIPTION);
    }

    @Test
    @DisplayName("Expect value 2 to be returned when calling getOrder")
    void expect_value_2_to_be_returned_when_calling_getOrder() {
        // GIVEN / WHEN
        final var order = cut.getOrder();

        // THEN
        assertThat(order).isEqualTo(2);
    }

    private FileValidationParams mockExpectValidationExecutedAndFileExistsAndFileAlreadyExistsExceptionOccurs() {
        final var fileUpload = mock(FileUpload.class);
        final var request = RepositoryCreateRequest.builder().filename(FILENAME).file(fileUpload).build();
        final var params = FileValidationParams.builder().request(request).build();
        final var fileMock = mock(File.class);
        BDDMockito.given(fileLocationWrapper.getFile(params.getRequest().getFilename())).willReturn(fileMock);
        BDDMockito.given(fileMock.exists()).willReturn(true);
        return params;
    }

    private FileValidationParams mockExpectValidationExecutedAndFileDoesNotExistAndNoExceptionOccurs() {
        final var fileUpload = mock(FileUpload.class);
        final var request = RepositoryCreateRequest.builder().filename(FILENAME).file(fileUpload).build();
        final var params = FileValidationParams.builder().request(request).build();
        final var fileMock = mock(File.class);
        BDDMockito.given(fileLocationWrapper.getFile(params.getRequest().getFilename())).willReturn(fileMock);
        BDDMockito.given(fileMock.exists()).willReturn(false);
        return params;
    }
}