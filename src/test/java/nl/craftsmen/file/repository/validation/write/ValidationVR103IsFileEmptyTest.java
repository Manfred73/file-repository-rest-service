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

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import nl.craftsmen.file.repository.fileoperations.write.RepositoryCreateRequest;
import nl.craftsmen.file.repository.filereaders.FileUploadReader;
import nl.craftsmen.file.repository.validation.FileValidationParams;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

@QuarkusTest
class ValidationVR103IsFileEmptyTest {

    private static final String FILENAME = "testfile.txt";
    public static final String VR103_DESCRIPTION = "VR103: Content of file " + FILENAME + " is empty!";

    @InjectMock
    FileUploadReader fileUploadReader;

    @Inject
    ValidationVR103IsFileEmpty cut;

    @Test
    @DisplayName("Given a filename, I expect that the validation is being executed, that the file exists with content and no execption occurs")
    void expect_validation_to_be_executed_and_that_file_with_content_exists_and_no_exception_occurs() {
        // GIVEN
        final var params = mockExpectValidationExecutedAndFileWithContentExistsAndNoExceptionOccurs();

        // WHEN / THEN
        assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Given a filename, I expect that the validation is being executed, that the file exists but without content and that a BadRequestException "
            + "occurs")
    void expect_validation_being_executed_and_that_file_exists_without_content_and_BadRequestException_occurs() {
        // GIVEN
        final var params = mockExpectValidationExecutedAndFileExistsWithoutContentAndBadRequestExceptionOccurs();

        // WHEN / THEN
        final var thrown = catchThrowable(() -> cut.validate(params));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage(VR103_DESCRIPTION);
    }

    @Test
    @DisplayName("Expect no exception and that validation for file content is not executed because request has not been provided")
    void expect_no_exception_and_validation_not_being_executed_when_request_has_not_been_provided() {
        // GIVEN
        final var params = FileValidationParams.builder().build();

        // WHEN / THEN
        assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
        verify(fileUploadReader, never()).isFileUploadEmpty(any());
    }

    @Test
    @DisplayName("Expect no exception and that validation on file content is not executed because no filename has been provided")
    void expect_no_exception_and_validation_not_being_executed_when_no_filename_has_been_provided() {
        // GIVEN
        final var request = RepositoryCreateRequest.builder().build();
        final var params = FileValidationParams.builder().request(request).build();

        // WHEN / THEN
        assertThatCode(() -> cut.validate(params)).doesNotThrowAnyException();
        verify(fileUploadReader, never()).isFileUploadEmpty(any());
    }

    @Test
    @DisplayName("Given a filename, when getValidationCodeWithDescription is called, I expect a validation code to be returned when calling "
            + "getValidationCodeWithDescription")
    void expect_validation_code_to_be_returned_when_calling_getValidationCodeWithDescription() {
        // GIVEN
        final var request = RepositoryCreateRequest.builder().filename(FILENAME).build();
        final var params = FileValidationParams.builder().request(request).build();

        // WHEN
        final var omschrijving = cut.getValidationCodeWithDescription(params);

        // THEN
        assertThat(omschrijving).isEqualTo(VR103_DESCRIPTION);
    }

    @Test
    @DisplayName("Expect value 3 to be returned when calling getOrder")
    void expect_value_3_to_be_returned_when_calling_getOrder() {
        // GIVEN / WHEN
        final var order = cut.getOrder();

        // THEN
        assertThat(order).isEqualTo(3);
    }

    private FileValidationParams mockExpectValidationExecutedAndFileWithContentExistsAndNoExceptionOccurs() {
        final var fileUpload = mock(FileUpload.class);
        final var request = RepositoryCreateRequest.builder().filename(FILENAME).file(fileUpload).build();
        final var params = FileValidationParams.builder().request(request).build();
        BDDMockito.given(fileUploadReader.isFileUploadEmpty(request.getFile())).willReturn(false);
        return params;
    }

    private FileValidationParams mockExpectValidationExecutedAndFileExistsWithoutContentAndBadRequestExceptionOccurs() {
        final var fileUpload = mock(FileUpload.class);
        final var request = RepositoryCreateRequest.builder().filename(FILENAME).file(fileUpload).build();
        final var params = FileValidationParams.builder().request(request).build();
        BDDMockito.given(fileUploadReader.isFileUploadEmpty(request.getFile())).willReturn(true);
        return params;
    }
}