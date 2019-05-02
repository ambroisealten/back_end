/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
	 * @return an {@link HttpException} corresponding to the statut
	 *         ({@link ForbiddenException} if user don't have the right role,
	 *         {@link OkException} if the file is deleted)
	 * @throws {@link NoSuchFileException} - if the file does not exist (optional
	 *         specific exception) {@link DirectoryNotEmptyException} - if the file
	 *         is a directory and could not otherwise be deleted because the
	 *         directory is not empty (optional specific exception)
	 *         {@link IOException} - if an I/O error occurs SecurityException - In
	 *         the case of the default provider, and a security manager is
	 *         installed, the SecurityManager.checkDelete(String) method is invoked
	 *         to check delete access to the file
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
	 * Move a file to a new path
	 *
	 * @param fileName the name of the file to move
	 * @param oldPath  full relative source path of file
	 * @param newPath  full relative target path of file
	 * @param role     the current logged user's role
	 * @return an {@link HttpException} corresponding to the statut
	 *         ({@link ForbiddenException} if user don't have the right role,
	 *         {@link OkException} if the file is moved)
	 * @throws IOException                     - if an I/O error occurs
	 * @throws SecurityException               - in the case of the default
	 *                                         provider, and a security manager is
	 *                                         installed, the checkWrite method is
	 *                                         invoked prior to attempting to create
	 *                                         a directory and its checkRead is
	 *                                         invoked for each parent directory
	 *                                         that is checked. If dir is not an
	 *                                         absolute path then its toAbsolutePath
	 *                                         may need to be invoked to get its
	 *                                         absolute path. This may invoke the
	 *                                         security manager's
	 *                                         checkPropertyAccess method to check
	 *                                         access to the system property
	 *                                         user.dir. - In the case of the
	 *                                         default provider, and a security
	 *                                         manager is installed, the checkWrite
	 *                                         method is invoked to check write
	 *                                         access to both the source and target
	 *                                         file.
	 * @throws UnsupportedOperationException   - if the array contains an attribute
	 *                                         that cannot be set atomically when
	 *                                         creating the directory
	 * @throws AtomicMoveNotSupportedException
	 * @author Andy Chabalier
	 */
	public HttpException moveFile(final String fileName, final String oldPath, final String newPath,
			final UserRole role) throws DirectoryNotEmptyException, IOException, SecurityException,
			UnsupportedOperationException, AtomicMoveNotSupportedException {
		if (!(UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)) {
			throw new ForbiddenException();
		}

		// Check if the file's name contains invalid characters or path doesn't end with
		// "/"
		if (fileName.contains("['{}[\\]\\\\;':\",./?!@#$%&*()_+=-]") || !newPath.endsWith("/")) {
			return new UnprocessableEntityException();
		}

		final Path oldDirPath = Paths.get(this.fileStorageLocation.toAbsolutePath() + oldPath);
		final Path oldLocation = oldDirPath.resolve(fileName);

		final Path newDirPath = Paths.get(this.fileStorageLocation.toAbsolutePath() + newPath);
		Files.createDirectories(newDirPath);
		final Path targetLocation = newDirPath.resolve(fileName);
		Files.move(oldLocation, targetLocation, StandardCopyOption.ATOMIC_MOVE);
		return new OkException();
	}

	/**
	 * Store a file
	 *
	 * @param file     the file to store
	 * @param path     full relative path of file
	 * @param fileName the file name
	 * @param role     the current logged user's role
	 * @return an {@link HttpException} corresponding to the status
	 *         ({@link ForbiddenException} if the user is not allowed,
	 *         {@link UnprocessableEntityException} if the filename contain
	 *         unauthorized characters,{@link InternalServerErrorException} if some
	 *         IO problems occur and {@link CreatedException} if the file is stored)
	 * @author Andy Chabalier
	 */
	public HttpException storeFile(final MultipartFile file, final String path, final String fileName,
			final UserRole role) {
		if (!(UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role)) {
			return new ForbiddenException();
		}

		try {
			// Check if the file's name contains invalid characters or path doesn't end with
			// "/"
			if (fileName.contains("['{}[\\]\\\\;':\",./?!@#$%&*()_+=-]") || !path.endsWith("/")) {
				return new UnprocessableEntityException();
			}

			final Path dirPath = Paths.get(this.fileStorageLocation.toAbsolutePath() + path);
			Files.createDirectories(dirPath);
			final Path targetLocation = dirPath.resolve(fileName);
			final InputStream fileInputStream = file.getInputStream();
			Files.copy(fileInputStream, targetLocation);
			fileInputStream.close();
			return new CreatedException();
		} catch (final IOException ex) {
			return new InternalServerErrorException();
		}
	}

}