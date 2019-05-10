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
	 * @param role      the current logged user's role
	 * @author Andy Chabalier
	 * @return an {@link HttpException} corresponding to the statut
	 *         ({@link ForbiddenException} if user don't have the right role,
	 *         {@link OkException} if the file is deleted,
	 *         {@link ResourceNotFoundException} if no such file,
	 *         {@link InternalServerErrorException} if IO or security problems occur
	 */
	public HttpException deleteFile(final String _id, final String path, final String extension, final UserRole role) {
		if (!isAdmin(role)) {
			return new ForbiddenException();
		}
		try {
			deleteFileInStorage(_id, path, extension);
		} catch (final NoSuchFileException nsfe) {
			return new ResourceNotFoundException();
		} catch (SecurityException | IOException e) {
			return new InternalServerErrorException();
		}
		return new OkException();
	}

	/**
	 * @param _id
	 * @param path
	 * @param extension
	 * @throws IOException
	 * @author Andy Chabalier
	 */
	public void deleteFileInStorage(final String _id, final String path, final String extension) throws IOException {
		final Path dirPath = Paths.get(getFileStorageLocationAbsolutePath() + path);
		final Path targetLocation = dirPath.resolve(_id + "." + extension);
		Files.delete(targetLocation);
	}

	/**
	 * @param role
	 * @return
	 * @author Andy Chabalier
	 */
	public boolean isAdmin(final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role;
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
			final Resource resource = loadResource(fileName);
			if (resource.exists()) {
				return resource;
			} else {
				throw new ResourceNotFoundException();
			}
		} catch (final MalformedURLException ex) {
			throw new UnprocessableEntityException();
		}
	}

	/**
	 * @param fileName
	 * @return
	 * @throws MalformedURLException
	 * @author Andy Chabalier
	 */
	public Resource loadResource(final String fileName) throws MalformedURLException {
		final Path filePath = Paths.get(getFileStorageLocationAbsolutePath() + fileName).normalize();
		final Resource resource = new UrlResource(filePath.toUri());
		return resource;
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
	 * @return ResourceNotFoundException - if an I/O error occurs
	 * @return InternalServerErrorException - in the case of the default provider,
	 *         and a security manager is installed, the checkWrite method is invoked
	 *         prior to attempting to create a directory and its checkRead is
	 *         invoked for each parent directory that is checked. If dir is not an
	 *         absolute path then its toAbsolutePath may need to be invoked to get
	 *         its absolute path. This may invoke the security manager's
	 *         checkPropertyAccess method to check access to the system property
	 *         user.dir. - In the case of the default provider, and a security
	 *         manager is installed, the checkWrite method is invoked to check write
	 *         access to both the source and target file. - if the array contains an
	 *         attribute that cannot be set atomically when creating the directory
	 *         AtomicMoveNotSupportedException
	 * @author Andy Chabalier
	 */
	public HttpException moveFile(final String fileName, final String oldPath, final String newPath,
			final UserRole role) {
		if (!isAdmin(role)) {
			return new ForbiddenException();
		}

		// Check if the file's name contains invalid characters or path doesn't end with
		// "/"
		if (fileNameAndPathIntegrity(fileName, newPath)) {
			return new UnprocessableEntityException();
		}

		final Path oldDirPath = Paths.get(getFileStorageLocationAbsolutePath() + oldPath);
		final Path oldLocation = oldDirPath.resolve(fileName);

		final Path newDirPath = Paths.get(getFileStorageLocationAbsolutePath() + newPath);
		try {
			moveFileFromTo(fileName, oldLocation, newDirPath);
		} catch (SecurityException | UnsupportedOperationException | AtomicMoveNotSupportedException
				| DirectoryNotEmptyException e) {
			return new InternalServerErrorException();
		} catch (final IOException e) {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * @return
	 * @author Andy Chabalier
	 */
	public Path getFileStorageLocationAbsolutePath() {
		return this.fileStorageLocation.toAbsolutePath();
	}

	/**
	 * @param fileName
	 * @param oldLocation
	 * @param newDirPath
	 * @throws IOException
	 * @author Andy Chabalier
	 */
	public void moveFileFromTo(final String fileName, final Path oldLocation, final Path newDirPath)
			throws IOException {
		Files.createDirectories(newDirPath);
		final Path targetLocation = newDirPath.resolve(fileName);

		Files.move(oldLocation, targetLocation, StandardCopyOption.ATOMIC_MOVE);
	}

	/**
	 * @param fileName
	 * @param newPath
	 * @author Andy Chabalier
	 */
	public boolean fileNameAndPathIntegrity(final String fileName, final String newPath) {
		return (fileName.contains("['{}[\\]\\\\;':\",./?!@#$%&*()_+=-]") || !newPath.endsWith("/"));
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
		if (!(isAdmin(role) || UserRole.MANAGER == role)) {
			return new ForbiddenException();
		}

		try {
			if (fileNameAndPathIntegrity(fileName, path)) {
				return new UnprocessableEntityException();
			}

			final Path dirPath = Paths.get(getFileStorageLocationAbsolutePath() + path);
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