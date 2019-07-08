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

import fr.alten.ambroiseJEE.controller.business.AgencyBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the agency web service
 *
 * @author Andy Chabalier
 *
 */
@Controller
public class AgencyRestController {

	@Autowired
	private AgencyBusinessController agencyBusinessController;

	private final Gson gson;

	public AgencyRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	public boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}

	/**
	 * Rest controller to create agency. HTTP Method : POST
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the agency name is not
	 *         provided and {@link CreatedException} if the agency is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/agency")
	@ResponseBody
	public HttpException createAgency(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "name", "place", "placeType")
				? this.agencyBusinessController.createAgency(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to delete agency. HTTP Method : DELETE
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the agency name is not
	 *         provided and {@link OkException} if the agency is deleted
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@DeleteMapping(value = "/agency")
	@ResponseBody
	public HttpException deleteAgency(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "name") ? this.agencyBusinessController.deleteAgency(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to fetch all agencies. HTTP Method : GET
	 *
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return the list of all agencies
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/agencies")
	@ResponseBody
	public String getAgencies(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.agencyBusinessController.getAgencies(role));
	}

	/**
	 * Rest controller to update an agency. HTTP Method : PUT
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the agency name is not
	 *         provided and {@link OkException} if the agency is updated
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PutMapping(value = "/agency")
	@ResponseBody
	public HttpException updateAgency(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "oldName", "name", "place", "placeType")
				? this.agencyBusinessController.updateAgency(params, role)
				: new UnprocessableEntityException();
	}
}
