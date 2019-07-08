/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.FileBusinessController;
import fr.alten.ambroiseJEE.controller.business.FileStorageBusinessController;
import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest Controller for file process
 *
 * @author Andy Chabalier
 *
 */
@RestController
public class FileRestController {

	@Autowired
	private FileStorageBusinessController fileStorageBusinessController;

	@Autowired
	private FileBusinessController fileBusinessController;

	private final Gson gson;

	public FileRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Delete a file from the database and the file system
	 *
	 * @param _id       the id of the file to delete
	 * @param path      the path of the file to delete
	 * @param extension the extension of the file to delete
	 * @param mail      the current logged user's mail
	 * @param role      the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the provided _id is not a
	 *         valid id, ({@link ResourceNotFoundException} if the file is not
	 *         found, {@link InternalServerErrorException} if a security or Io
	 *         problem occur and {@link OkException} if the file is deleted
	 * @author Andy Chabalier
	 */
	@DeleteMapping("/file")
	public HttpException deleteFile(@RequestParam("_id") final String _id, @RequestParam("path") final String path,
			@RequestParam("extension") final String extension, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		if (isValid(_id)) {
			final HttpException deletionResult = this.fileStorageBusinessController.deleteFile(_id, path, extension,
					role);
			if (!OkException.class.isInstance(deletionResult)) {
				return deletionResult;
			}
			return this.fileBusinessController.deleteFile(_id, role);
		}
		return new UnprocessableEntityException();
	}

	/**
	 * Send the requested resources. HTTP Method : GET
	 *
	 * @param fileName the file's name to fetch
	 * @param mail     the current logged user's mail
	 * @param role     the current logged user's role
	 * @param request  Request object with informations
	 * @return the asked resource wrapped in an ResponseEntity
	 * @author Andy Chabalier
	 */
	@GetMapping("/file/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable final String fileName,
			@RequestParam("path") final String path, final HttpServletRequest request,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		final Resource resource = this.fileStorageBusinessController.loadFileAsResource(path + fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			if (contentType == null) {
				throw new NullPointerException();
			}
		} catch (final Exception ex) {
			// Set the default content type
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	/**
	 * Check if a file is not null
	 *
	 * @param file file to check
	 * @return true if the file is not null, else return false
	 * @author Andy Chabalier
	 */
	public boolean fileNotNullCheck(final MultipartFile file) {
		return !(file.equals(null) || file.isEmpty());
	}

	/**
	 * Fetch the list of specific collection's document
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return the list of the documents
	 * @author Andy Chabalier
	 */
	@GetMapping("/files/collection")
	public String getCollectionFiles(@RequestParam("path") final String path,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.fileBusinessController.getCollectionFiles(path, role));
	}

	/**
	 * Send the requested resources. HTTP Method : GET
	 *
	 * @param fileName the file's name to fetch
	 * @param mail     the current logged user's mail
	 * @param role     the current logged user's role
	 * @param request  Request object with informations
	 * @return the asked resource wrapped in an ResponseEntity
	 * @author Andy Chabalier
	 */
	@GetMapping("/file")
	public String getFile(@RequestParam("fileName") final String fileName, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		if (isValid(fileName)) {
			return this.gson.toJson(this.fileBusinessController.getDocument(fileName, role));
		}
		throw new UnprocessableEntityException();
	}

	/**
	 * Fetch the list of documents
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return the list of documents
	 * @author Andy Chabalier
	 */
	@GetMapping("/files")
	public String getFiles(@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.fileBusinessController.getFiles(role));
	}

	/**
	 * Fetch the list of forum documents
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return the list of forum documents
	 * @author Andy Chabalier
	 */
	@GetMapping("/files/forum")
	public String getFilesForum(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.fileBusinessController.getFilesForum(role));
	}

	public boolean isValid(final String _id) {
		return ObjectId.isValid(_id);
	}

	/**
	 *
	 * @param _id         the id of the file to update
	 * @param displayName the display name of the file to update
	 * @param path        the path of the file to update
	 * @param mail        the current logged user's mail
	 * @param role        the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found,
	 *         ({@link ResourceNotFoundException} if the file is not found,
	 *         {@link InternalServerErrorException} if a security internal error
	 *         occur and {@link OkException} if the file is moved and updated
	 * @author Andy Chabalier
	 */
	@PutMapping("/file")
	public HttpException updateFile(@RequestParam("_id") final String _id,
			@RequestParam("extension") final String extension, @RequestParam("displayName") final String displayName,
			@RequestParam("newPath") final String newPath, @RequestParam("oldPath") final String oldPath,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		if (isValid(_id)) {
			final HttpException updateResult = this.fileStorageBusinessController.moveFile(_id + "." + extension,
					oldPath, newPath, role);
			if (!OkException.class.isInstance(updateResult)) {
				return updateResult;
			}
			return this.fileBusinessController.updateDocument(_id, newPath, displayName, role);
		}
		return new UnprocessableEntityException();
	}

	/**
	 * upload a file. This delegate the stockage of file and his subscription in the
	 * document's database collection HTTP Method : POST
	 *
	 * @param file the file to store
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @throws {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the provided file is null,
	 *         and {@link CreatedException} if the file is stored and created
	 * @return the uploaded file
	 * @author Andy Chabalier
	 */
	@PostMapping("/file")
	public File uploadFile(@RequestParam("file") final MultipartFile file, @RequestParam("path") final String path,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {

		if (fileNotNullCheck(file)) {
			final File newFile = this.fileBusinessController.createDocument(path, file.getOriginalFilename(), role);
			this.fileStorageBusinessController.storeFile(file, newFile.getPath(),
					newFile.get_id() + "." + newFile.getExtension(), role);
			return newFile;
		} else {
			throw new UnprocessableEntityException();
		}
	}

	/**
	 * Upload an list of files. That ask to {@link #uploadFile(MultipartFile)} to
	 * store files one by one. HTTP Method : POST
	 *
	 * @param files Array of files to store
	 * @param mail  the current logged user's mail
	 * @param role  the current logged user's role
	 * @return The list of HTTP Exception that occur @See
	 *         {@link #uploadFile(MultipartFile)}
	 * @author Andy Chabalier
	 */
	@PostMapping("/file/multiples")
	public List<File> uploadMultipleFiles(@RequestParam("files") final MultipartFile[] files,
			@RequestParam("path") final String path, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return Arrays.asList(files).stream().map(file -> uploadFile(file, path, mail, role))
				.collect(Collectors.toList());
	}

}
