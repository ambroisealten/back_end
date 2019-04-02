/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
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
public class AdminRestController {

	@Autowired
	private UserBusinessController userBusinessController;

	private final Gson gson;

	public AdminRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * 
	 * @param params JsonNode containing post parameters from http request : mail,
	 *               password, name, forname, agency, 
	 * @param mail the user mail
	 * @param role the user role
	 * @return {@link HttpException} corresponding to the statut of the
	 *         request ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link CreatedException} if the user is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author MAQUINGHEN MAXIME
	 */
	@PostMapping(value = "/admin/user")
	@ResponseBody
	public HttpException createUser(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? userBusinessController.createUser(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * 
	 * @param mail the user mail
	 * @param role the user role
	 * @return the list of all users
	 * @author MAQUINGHEN MAXIME
	 */
	@GetMapping(value = "/admin/users")
	@ResponseBody
	public String getUsers(@RequestAttribute("mail") String mail, @RequestAttribute("role") int role) {
		return gson.toJson(userBusinessController.getUsers(role));
	}

	/**
	 * 
	 * @param params JsonNode containing put parameters from http request : mail,
	 *               password, name, forname, agency,  
	 * @param mail the user mail
	 * @param role the user role 
	 * @return {@link HttpException} corresponding to the statut of the
	 *         request ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link OkException} if the user is updated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@PutMapping(value = "/admin/user")
	@ResponseBody
	public HttpException updateUser(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? userBusinessController.updateUser(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * 
	 * @param params contains the mail of the user to desactivated
	 * @param mail the user mail
	 * @param role the user role
	 * @return {@link HttpException} corresponding to the statut of the
	 *         request ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link OkException} if the user is updated successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@DeleteMapping(value = "/admin/user")
	@ResponseBody
	public HttpException deleteUser(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? userBusinessController.deleteUser(params, role)
				: new UnprocessableEntityException();
	}
	
	

}
