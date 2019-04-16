package fr.alten.ambroiseJEE.controller.rest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * 
 * Rest controller for MobileDoc administration
 * 
 * @author Kylian Gehier, Lucas Royackkers
 *
 */
@Controller
public class MobileDocRestController {

	@Autowired
	private FileRestController fileRestController;

	/**
	 * 
	 * @param mail the current logged user's mail
	 * @param role	the user's role
	 * @return the list of all Mobile Documents
	 * @author Kylian Gehier, Lucas Royackkers
	 */
	@GetMapping(value = "/mobileDocs")
	@ResponseBody
	public String getMobileDocs(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return fileRestController.getFilesForum(mail, role);
	}
	

	/**
	 * Upload a file that refers to a mobile doc. This delegate the stocking of file and his subscription in the
	 * document's database collection HTTP Method : POST
	 * 
	 * @param file the file to store
	 * @param mail the current logged user's mail
	 * @param role the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found,
	 *         ({@link OkException} if there is a conflict in the database (that
	 *         means the file already exists and then it's an upload, But no change to
	 *         make in base) and {@link CreatedException} if the file is stored and
	 *         created
	 * @author Lucas Royackkers
	 */
	@PostMapping(value = "/uploadMobileDoc")
	@ResponseBody
	public HttpException uploadMobileDoc(@RequestParam("file") MultipartFile file, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) {
		return file != null ?  fileRestController.uploadFile("true", file, mail, role) :
		new UnprocessableEntityException();
	}
	
	/**
	 * Upload several files. Calls the uploadMobileDoc function each time it encounters a file in its List
	 * 
	 * @param files the files to store (as a List)
	 * @param mail the current logged user's mail
	 * @param role the user's role
	 * @return an array of {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resources aren't found,
	 *         ({@link OkException} if there is/are a conflict in the database (that
	 *         means the file already exists and then it's an upload, But no change to
	 *         make in base) and {@link CreatedException} if the file is/are stored and
	 *         created
	 * @author Lucas Royackkers
	 */
	@PostMapping(value = "/uploadMobileDocs")
	public List<HttpException> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
			@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return Arrays.asList(files).stream().map(file -> uploadMobileDoc(file, mail, role)).collect(Collectors.toList());
	}
	
}
