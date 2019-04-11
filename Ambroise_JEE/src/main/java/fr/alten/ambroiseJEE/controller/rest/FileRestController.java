/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.FileBusinessController;
import fr.alten.ambroiseJEE.controller.business.FileStorageBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
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
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * upload a file. This delegate the stockage of file and his subscription in the
	 * document's database collection HTTP Method : POST
	 * 
	 * @param file the file to store
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link UnprocessableEntityException} if the ressource is not found,
	 *         ({@link OkException} if there is a conflict in the database (that
	 *         mean file already exist and then it's an upload. But no change to
	 *         make in base and {@link CreatedException} if the file is stored and
	 *         created
	 * @author Andy Chabalier
	 */
	@PostMapping("/file")
	public HttpException uploadFile(@RequestParam("isForForum") String isForForum, @RequestParam("file") MultipartFile file, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) {
		return file != null
				? fileBusinessController.createDocument(ServletUriComponentsBuilder.fromCurrentContextPath()
						.path(fileStorageBusinessController.storeFile(file)).toUriString(), isForForum, role)
				: new UnprocessableEntityException();
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
	public List<HttpException> uploadMultipleFiles(@RequestParam("isForForum") String isForForum, @RequestParam("files") MultipartFile[] files,
			@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return Arrays.asList(files).stream().map(file -> uploadFile(isForForum, file, mail, role)).collect(Collectors.toList());
	}

	/**
	 * Send the requested ressources. HTTP Method : GET
	 * 
	 * @param fileName the file's name to fetch
	 * @param mail     the current logged user's mail
	 * @param role     the current logged user's role
	 * @param request  Request object with informations
	 * @return the asked ressource wrapped in an ResponseEntity
	 * @author Andy Chabalier
	 */
	@GetMapping("/file/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request,
			@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		Resource resource = fileStorageBusinessController.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			if(contentType==null) {
				throw new NullPointerException();
			}
		} catch (Exception ex) {
			// Set the default content type
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	/**
	 *  Fetch the list of documents
	 * @param mail     the current logged user's mail
	 * @param role     the current logged user's role
	 * @return the list of documents
	 * @author Andy Chabalier
	 */
	@GetMapping("/files")
	public String getFiles(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(fileBusinessController.getFiles(role));
	}
	
	/**
	 *  Fetch the list of forum documents
	 * @param mail     the current logged user's mail
	 * @param role     the current logged user's role
	 * @return the list of forum documents
	 * @author Andy Chabalier
	 */
	@GetMapping("/files/forum")
	public String getFilesForum(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(fileBusinessController.getFilesForum(role));
	}
}
