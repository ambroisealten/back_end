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

import fr.alten.ambroiseJEE.controller.business.geographic.PostalCodeBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the postal code web service
 *
 * @author Andy Chabalier
 *
 */
@Controller
public class PostalCodeRestController {

	@Autowired
	private PostalCodeBusinessController postalCodeBusinessController;

	private final Gson gson;

	public PostalCodeRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Rest controller to create postal Code. HTTP Method : POST
	 * 
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the name is not provided and
	 *         {@link CreatedException} if the postal code is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/postalCode")
	@ResponseBody
	public HttpException createPostalCode(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("name") != null ? postalCodeBusinessController.createPostalCode(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to delete postal code. HTTP Method : DELETE
	 * 
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the name is not provided and
	 *         {@link OkException} if the postal code is deleted
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@DeleteMapping(value = "/postalCode")
	@ResponseBody
	public HttpException deletePostalCode(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("name") != null ? postalCodeBusinessController.deletePostalCode(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to fetch all postal codes. HTTP Method : GET
	 * 
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return the list of all postal codes
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/postalCodes")
	@ResponseBody
	public String getPostalCodes(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(postalCodeBusinessController.getPostalCodes(role));
	}

	/**
	 * Rest controller to update postal code. HTTP Method : PUT
	 * 
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the name is not provided and
	 *         {@link OkException} if the postal code is updated
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PutMapping(value = "/postalCode")
	@ResponseBody
	public HttpException updatePostalCode(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("name") != null ? postalCodeBusinessController.updatePostalCode(params, role)
				: new UnprocessableEntityException();
	}
}
