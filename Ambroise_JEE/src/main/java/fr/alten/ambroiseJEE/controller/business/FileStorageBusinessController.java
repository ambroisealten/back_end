/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
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
			final Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
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
	public HttpException storeFile(final MultipartFile file, String path, String fileName, final UserRole role) {
		if (!(UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)) {
			throw new ForbiddenException();
		}

		try {
			// Check if the file's name contains invalid characters or path doesn't end with
			// "/"
			if (fileName.contains("['{}[\\]\\\\;':\",./?!@#$%&*()_+=-]") || !path.endsWith("/")) {
				throw new UnprocessableEntityException();
			}

			Path dirPath = Paths.get(this.fileStorageLocation.toAbsolutePath() + path);
			Files.createDirectories(dirPath);
			Path targetLocation = dirPath.resolve(fileName);
			InputStream fileInputStream = file.getInputStream();
			Files.copy(fileInputStream, targetLocation);
			fileInputStream.close();
			return new CreatedException();
		} catch (final IOException ex) {
			return new InternalServerErrorException();
		}
	}
}