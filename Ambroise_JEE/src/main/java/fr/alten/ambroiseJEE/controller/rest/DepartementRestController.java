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

import fr.alten.ambroiseJEE.controller.business.geographic.DepartementBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the city web service
 *
 * @author Andy Chabalier
 *
 */
@Controller
public class DepartementRestController {

	@Autowired
	private DepartementBusinessController departementBusinessController;

	private final Gson gson;

	public DepartementRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Rest controller to create departement. HTTP Method : POST
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the name is not provided and
	 *         {@link CreatedException} if the departement is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/departement")
	@ResponseBody
	public HttpException createDepartement(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return params.get("name") != null ? this.departementBusinessController.createDepartement(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to delete departement. HTTP Method : DELETE
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the name is not provided and
	 *         {@link OkException} if the departement is deleted
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@DeleteMapping(value = "/departement")
	@ResponseBody
	public HttpException deleteDepartement(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return params.get("name") != null ? this.departementBusinessController.deleteDepartement(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to fetch all departements. HTTP Method : GET
	 *
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return the list of all departements
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/departements")
	@ResponseBody
	public String getDepartements(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.departementBusinessController.getDepartements(role));
	}

	/**
	 * Rest controller to update departement
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the name is not provided and
	 *         {@link OkException} if the departement is updated
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PutMapping(value = "/departement")
	@ResponseBody
	public HttpException updateDepartement(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return params.get("name") != null ? this.departementBusinessController.updateDepartement(params, role)
				: new UnprocessableEntityException();
	}
}
