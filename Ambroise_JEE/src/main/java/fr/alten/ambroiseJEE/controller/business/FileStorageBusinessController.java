/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Business file controller to file storage
 *
 * @author Andy Chabalier
 *
 */
@Service
public class FileStorageBusinessController {

	@Autowired
	private ApplicationContext ctx;

	private Path fileStorageLocation;

	/**
	 * @param _id       the id of file to delete
	 * @param path      the path of file to delete
	 * @param extension the extension of file to delete
	 * @param role      the current loggend user's role
	 * @author Andy Chabalier
	 * @throws {@link NoSuchFileException} - if the file does not exist (optional
	 *         specific exception) {@link DirectoryNotEmptyException} - if the file
	 *         is a directory and could not otherwise be deleted because the
	 *         directory is not empty (optional specific exception)
	 *         {@link IOException} - if an I/O error occurs SecurityException - In
	 *         the case of the default provider, and a security manager is
	 *         installed, the SecurityManager.checkDelete(String) method is invoked
	 *         to check delete access to the file {@link ForbiddenException} if user
	 *         don't have the right role
	 */
	public HttpException deleteFile(final String _id, final String path, final String extension, final UserRole role)
			throws NoSuchFileException, DirectoryNotEmptyException, IOException, SecurityException {
		if (!(UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)) {
			return new ForbiddenException();
		}
		final Path dirPath = Paths.get(this.fileStorageLocation.toAbsolutePath() + path);
		final Path targetLocation = dirPath.resolve(_id + "." + extension);
		Files.delete(targetLocation);
		return new OkException();
	}

	/**
	 * Initialise the controller with the application properties of file path
	 *
	 * @author Andy Chabalier
	 */
	@PostConstruct
	private void init() {
		this.fileStorageLocation = Paths.get(this.ctx.getEnvironment().getProperty("file.upload.path")).toAbsolutePath()
				.normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (final Exception ex) {
			throw new ConflictException();
		}
	}

	/**
	 * Load a file
	 *
	 * @param fileName file to load
	 * @return the resource fetched
	 * @author Andy Chabalier
	 */
	public Resource loadFileAsResource(final String fileName) {
		try {
			final Path filePath = Paths.get(this.fileStorageLocation.toAbsolutePath() + fileName).normalize();
			final Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new ResourceNotFoundException();
			}
		} catch (final MalformedURLException ex) {
			throw new ResourceNotFoundException();
		}
	}

	/**
	 * Store a file
	 *
	 * @param file     file to store
	 * @param fullPath full relative path of file
	 * @return the name of the stored file
	 * @author Andy Chabalier
	 */
	public HttpException storeFile(final MultipartFile file, final String path, final String fileName,
			final UserRole role) {
		if (!(UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)) {
			throw new ForbiddenException();
		}

		try {
			// Check if the file's name contains invalid characters or path doesn't end with
			// "/"
			if (fileName.contains("['{}[\\]\\\\;':\",./?!@#$%&*()_+=-]") || !path.endsWith("/")) {
				throw new UnprocessableEntityException();
			}

			final Path dirPath = Paths.get(this.fileStorageLocation.toAbsolutePath() + path);
			Files.createDirectories(dirPath);
			final Path targetLocation = dirPath.resolve(fileName);
			final InputStream fileInputStream = file.getInputStream();
			Files.copy(fileInputStream, targetLocation);
			fileInputStream.close();
			return new CreatedException();
		} catch (final IOException ex) {
			throw new InternalServerErrorException();
		}
	}
}