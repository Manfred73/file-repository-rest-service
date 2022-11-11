package nl.craftsmen.file.repository.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;
import nl.craftsmen.file.repository.exceptionhandling.ResourceException;

@UtilityClass
public final class ResourceReader {

	/**
	 * Retrieves the absolute path as a {@link Path} for a resource on the classpath based on the given (non-absolute) resource name.
	 *
	 * @param resourceName
	 * 		the (non-absolute) name of the resource to look for on the classpath
	 * @return the absolute {@link Path} to the resource
	 */
	public static Path getAbsolutePathFromClassLoaderResourceAsPath(String resourceName) {
		try {
			return Paths.get(ClassLoader.getSystemResource(resourceName).toURI());
		} catch (URISyntaxException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * Retrieves the absolute path as a {@link String} for a resource on the classpath based on the given (non-absolute) resource name.
	 *
	 * @param resourceName
	 * 		the (non-absolute) name of the resource to look for on the classpath
	 * @return the absolute path as a {@link String} to the resource
	 */
	public static String getAbsolutePathFromClassLoaderResourceAsString(String resourceName) {
		return getAbsolutePathFromClassLoaderResourceAsPath(resourceName).toString();
	}

	/**
	 * Retrieves the {@link File} for a resource on the classpath based on the given (non-absolute) resource name.
	 *
	 * @param resourceName
	 * 		the (non-absolute) name of the resource to look for on the classpath
	 * @return the {@link File} for the given resource
	 */
	public static File readResourceFromClassLoaderResourceAsFile(String resourceName) {
		return getAbsolutePathFromClassLoaderResourceAsPath(resourceName).toFile();
	}

	/**
	 * Retrieves the content from a given {@link File} as a {@link String}.
	 *
	 * @param resourceName
	 * 		the (non-absolute) name of the resource to look for on the classpath
	 * @return the content of the {@link File} as a {@link String}
	 */
	public static String readFileContentFromClassLoaderResourceAsString(String resourceName) {
		Path filePath;
		try {
			filePath = getAbsolutePathFromClassLoaderResourceAsPath(resourceName);
			return Files.readString(filePath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * Retrieves the content from a given {@link File} as a {@link String}.
	 *
	 * @param file
	 * 		the {@link File} to read
	 * @return the content of the {@link File} as a {@link String}
	 */
	public static String readFileContentAsString(File file) {
		final var filePath = Paths.get(file.getAbsolutePath());
		try {
			return Files.readString(filePath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * Retrieves the content from a {@link File} based on the given (non-absolute) resource name.
	 *
	 * @param resourceName
	 * 		the (non-absolute) name of the resource
	 * @return the content of the given resource as a {@link String}
	 */
	public static String readFileContentFromResourceAsString(String resourceName) {
		final var filePath = Paths.get(resourceName).toAbsolutePath();
		try {
			return Files.readString(filePath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * Retrieves the content from a {@link File} based on the given (non-absolute) resource path.
	 *
	 * @param resourcePath
	 * 		the (non-absolute) path of the resource
	 * @return the content of the given resource as a {@link String}
	 */
	public static String readFileContentFromPathAsString(Path resourcePath) {
		final var filePath = resourcePath.toAbsolutePath();
		try {
			return Files.readString(filePath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * Retrieves the {@link File} based on the given resource path.
	 *
	 * @param resourcePath
	 * 		the path of the resource
	 * @return the {@link File} for the given resource path
	 */
	public static File readFileFromResourcePath(Path resourcePath) {
		return resourcePath.toFile();
	}

	/**
	 * Retrieves the {@link File} based on the given resource.
	 *
	 * @param resourceName
	 * 		the name of the resource
	 * @return the {@link File} for the given resource
	 */
	public static File readFileFromResourceName(String resourceName) {
		return Paths.get(resourceName).toFile();
	}
}
