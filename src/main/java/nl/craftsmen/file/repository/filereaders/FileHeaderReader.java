package nl.craftsmen.file.repository.filereaders;

import io.quarkus.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.InternalServerErrorException;

@ApplicationScoped
public class FileHeaderReader {

    private static final int KEY_LENGTH = 47;
    private static final String ERROR_READING_HEADER = "Error reading header (key) from file: ";
    private static final String ERROR_READING_HEADER_HEADER_TOO_SHORT = "Error reading header (key) from file - "
            + "header is shorter than " + KEY_LENGTH + " characters: ";

    /**
     * Reads the first line (header) from the file and returns the first 47 characters as key.
     *
     * @param file the file as {@link File}
     * @return the header (key) as {@link String}
     */
    public String readFileHeader(File file) {
        try (var reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine().substring(0, KEY_LENGTH);
        } catch (IOException ioe) {
            Log.error(ERROR_READING_HEADER, ioe);
            throw new InternalServerErrorException(ERROR_READING_HEADER, ioe);
        } catch (StringIndexOutOfBoundsException sioobe) {
            Log.error(ERROR_READING_HEADER_HEADER_TOO_SHORT, sioobe);
            throw new InternalServerErrorException(ERROR_READING_HEADER_HEADER_TOO_SHORT, sioobe);
        }
    }
}
