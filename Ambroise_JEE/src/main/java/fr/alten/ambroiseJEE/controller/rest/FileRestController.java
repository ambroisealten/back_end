/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
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

	@DeleteMapping("/file")
	public HttpException deleteFile(@RequestParam("_id") final String _id, @RequestParam("path") final String path,
			@RequestParam("extension") final String extension, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		if (ObjectId.isValid(_id)) {
			try {
				this.fileStorageBusinessController.deleteFile(_id, path, extension, role);
			} catch (final NoSuchFileException e) {
				return new ResourceNotFoundException();
			} catch (SecurityException | IOException e) {
				return new InternalServerErrorException();
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

	/**
	 * Fetch the list of specific collection's document
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return the list of the documents
	 * @author Andy Chabalier
	 */
	@GetMapping("/files/collection")
	public String getCollectionFiles(@RequestParam("path") String path, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.fileBusinessController.getCollectionFiles(path, role));
	}

	/**
	 * upload a file. This delegate the stockage of file and his subscription in the
	 * document's database collection HTTP Method : POST
	 *
	 * @param file the file to store
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found,
	 *         ({@link OkException} if there is a conflict in the database (that
	 *         mean file already exist and then it's an upload. But no change to
	 *         make in base and {@link CreatedException} if the file is stored and
	 *         created
	 * @author Andy Chabalier
	 */
	@PostMapping("/file")
	public File uploadFile(@RequestParam("file") final MultipartFile file, @RequestParam("path") final String path,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {

		if (file != null) {
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
