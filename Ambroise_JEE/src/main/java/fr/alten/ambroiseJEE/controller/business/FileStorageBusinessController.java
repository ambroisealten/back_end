/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;
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
		this.fileStorageLocation = Paths.get(ctx.getEnvironment().getProperty("file.upload.path")).toAbsolutePath()
				.normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new ConflictException();
		}
	}

	/**
	 * Store a file
	 * 
	 * @param file file to store
	 * @return the name of the stored file
	 * @author Andy Chabalier
	 */
	public String storeFile(MultipartFile file) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new UnprocessableEntityException();
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (IOException ex) {
			throw new UnprocessableEntityException();
		}
	}

	/**
	 * Load a file
	 * 
	 * @param fileName file to load
	 * @return the ressource fetched
	 * @author Andy Chabalier
	 */
	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new RessourceNotFoundException();
			}
		} catch (MalformedURLException ex) {
			throw new RessourceNotFoundException();
		}
	}
}