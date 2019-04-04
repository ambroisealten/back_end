/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest Controller for User Administration
 * 
 * @author MAQUINGHEN MAXIME
 *
 */
@Controller
public class UserAdminRestController {

	@Autowired
	private UserBusinessController userBusinessController;

	private final Gson gson;

	public UserAdminRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Create a new User. HTTP method POST
	 * 
	 * @param params JsonNode containing post parameters from http request : mail,
	 *               password, name, forname, agency,
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link CreatedException} if the user is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author MAQUINGHEN MAXIME
	 */
	@PostMapping(value = "/admin/user")
	@ResponseBody
	public HttpException createUser(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
<<<<<<< HEAD:Ambroise_JEE/src/main/java/fr/alten/ambroiseJEE/controller/rest/AdminRestController.java
			@RequestAttribute("role") int role) throws Exception {
		return params.get("name") != null ? userBusinessController.createUser(params, role)
=======
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? userBusinessController.createUser(params, role)
>>>>>>> dev:Ambroise_JEE/src/main/java/fr/alten/ambroiseJEE/controller/rest/UserAdminRestController.java
				: new UnprocessableEntityException();
	}

	/**
	 * 
	 * List all User. HTTP method GET
	 * 
	 * @param mail the user mail
	 * @param role the user role
	 * @return the list of all users
	 * @author MAQUINGHEN MAXIME
	 */
	@GetMapping(value = "/admin/users")
	@ResponseBody
	public String getUsers(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(userBusinessController.getUsers(role));
	}

	/**
	 * Update a User. HTTP method PUT
	 *
	 * @param params JsonNode containing put parameters from http request : mail,
	 *               password, name, forname, agency,
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link OkException} if the user is updated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@PutMapping(value = "/admin/user")
	@ResponseBody
	public HttpException updateUser(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? userBusinessController.updateUser(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Desactivate a User. HTTP method DELETE
	 * 
	 * @param params contains the mail of the user to desactivated
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link OkException} if the user is updated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@DeleteMapping(value = "/admin/user")
	@ResponseBody
	public HttpException deleteUser(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? userBusinessController.deleteUser(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Change a User Password (Admin side). HTTP method PUT
	 * 
	 * @param resetPassMail the mail that needs to be changed
	 * @param params
	 * @param mail          the user mail
	 * @param role          the user role
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link OkException} if the user is updated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@PutMapping(value = "/admin/user/{mail}")
	@ResponseBody
	public HttpException resetUserPassword(@PathVariable("mail") String resetPassMail, @RequestBody JsonNode params,
			@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? userBusinessController.resetUserPassword(resetPassMail, params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Change User Password (User Side). HTTP method PUT The User receive a mail
	 * with a unique link (token)
	 * 
	 * @param token  Unique token to request the new password
	 * @param params TODO "pas de paramètre ?"
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link OkException} if the user is updated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@PutMapping(value = "/admin/user/{token}")
	@ResponseBody
	public HttpException newPasswordUser(@PathVariable("token") String token, @RequestBody JsonNode params,
			@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? userBusinessController.newPasswordUser(token, params, role)
				: new UnprocessableEntityException();
	}
	
	
	@GetMapping(value = "/admin/user/{mail}")
	@ResponseBody
	public String getUser(@PathVariable("mail") String usermail,@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(userBusinessController.getUser(usermail, null, role));
	}
}
