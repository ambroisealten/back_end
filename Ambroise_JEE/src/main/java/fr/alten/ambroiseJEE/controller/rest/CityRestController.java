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

import fr.alten.ambroiseJEE.controller.business.geographic.CityBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the city web service
 *
 * @author Andy Chabalier
 *
 */
@Controller
public class CityRestController {

	@Autowired
	private CityBusinessController cityBusinessController;

	private final Gson gson;

	public CityRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	public boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}

	/**
	 * Rest controller to create a city. HTTP Method : POST
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the name is not provided and
	 *         {@link CreatedException} if the city is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/city")
	@ResponseBody
	public HttpException createCity(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "nom", "code", "codesPostaux", "codeRegion", "codeDepartement") ? this.cityBusinessController.createCity(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to delete a city. HTTP Method : DELETE
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the name is not provided,
	 *         {@link OkException} if the city is deleted and
	 *         {@link ResourceNotFoundException} if the name isn't found in base
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@DeleteMapping(value = "/city")
	@ResponseBody
	public HttpException deleteCity(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "code") ? this.cityBusinessController.deleteCity(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to fetch all cities. HTTP Method : GET
	 *
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return the list of all cities
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/cities")
	@ResponseBody
	public String getCities(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.cityBusinessController.getCities(role));
	}

	/**
	 * Rest controller to update a city
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request {@link ResourceNotFoundException} if the resource is not
	 *         found, {@link ConflictException} if a duplicate unique field is
	 *         trying to be saved by updating, {@link CreatedException} if the city
	 *         is updated and {@link UnprocessableEntityException} if a required
	 *         field in missing in params
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 * @author Kylian Gehier
	 */
	@PutMapping(value = "/city")
	@ResponseBody
	public HttpException updateCity(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "nom", "code") ? this.cityBusinessController.updateCity(params, role)
				: new UnprocessableEntityException();
	}
}
