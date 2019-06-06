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
import fr.alten.ambroiseJEE.utils.JsonUtils;
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
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Create a new User. HTTP method POST
	 *
	 * @param params JsonNode containing post parameters from http request : mail,
	 *               password, name, forname, agency,
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link CreatedException} if the user is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author MAQUINGHEN MAXIME
	 */
	@PostMapping(value = "/admin/user")
	@ResponseBody
	public HttpException createUser(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "mail","forname","name","pswd") ? this.userBusinessController.createUser(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Deactivate a User. HTTP method DELETE
	 *
	 * @param usermail contains the mail of the user to deactivate
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link OkException} if the user is deactivated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@DeleteMapping(value = "/admin/deleteUser/{mail}")
	@ResponseBody
	public HttpException deleteUser(@PathVariable("mail") final String usermail, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return this.userBusinessController.deleteUser(usermail, role);
	}

	@GetMapping(value = "/admin/user/{mail}")
	@ResponseBody
	public String getUser(@PathVariable("mail") final String usermail, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.userBusinessController.getUserByMail(usermail, role));
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
	public String getUsers(@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.userBusinessController.getUsers(role));
	}

	/**
	 * Change User Password (User Side). HTTP method PUT The User receive a mail
	 * with a unique link (token)
	 *
	 * @param userMail the mail of the concerned user
	 * @param params   the JsonNode containing all parameters
	 * @param mail     the user mail
	 * @param role     the user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link OkException} if the user is updated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME, Lucas Royackkers
	 */
	@PutMapping(value = "/admin/user/newPwd/{mail}")
	@ResponseBody
	public HttpException newPasswordUser(@PathVariable("mail") final String userMail,
			@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "pswd")
				? this.userBusinessController.newPasswordUser(userMail, params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Change a User Password (Admin side). HTTP method PUT
	 *
	 * @param paramsthe JsonNode containing all parameters (the mail of the User
	 *                  that need to reset its password)
	 * @param mail      the user mail
	 * @param role      the user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link OkException} if the user is updated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME, Lucas Royackkers
	 */
	@PutMapping(value = "/admin/user/resetPwd")
	@ResponseBody
	public HttpException resetUserPassword(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role)
			throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "mail")
				? this.userBusinessController.resetUserPassword(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Update a User. HTTP method PUT
	 *
	 * @param params JsonNode containing put parameters from http request : mail,
	 *               password, name, forname, agency,
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link OkException} if the user is updated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@PutMapping(value = "/admin/user")
	@ResponseBody
	public HttpException updateUser(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "mail") ? this.userBusinessController.updateUser(params, role)
				: new UnprocessableEntityException();
	}
}
