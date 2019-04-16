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
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
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
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * 
	 * @param params JsonNode containing post parameters from http request
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return {@link HttpException} corresponding to the statut of the
	 *         request ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link CreatedException} if the user is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/city")
	@ResponseBody
	public HttpException createCity(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("name") != null ? cityBusinessController.createCity(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * 
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return the list of all cities
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/cities")
	@ResponseBody
	public String getCities(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(cityBusinessController.getCities(role));
	}

	/**
	 * 
	 * @param params JsonNode containing post parameters from http request
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return {@link HttpException} corresponding to the statut of the
	 *         request ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link CreatedException} if the user is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PutMapping(value = "/city")
	@ResponseBody
	public HttpException updateCity(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("name") != null ? cityBusinessController.updateCity(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * 
	 * @param params JsonNode containing post parameters from http request
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return {@link HttpException} corresponding to the statut of the
	 *         request ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link CreatedException} if the user is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@DeleteMapping(value = "/city")
	@ResponseBody
	public HttpException deleteCity(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("name") != null ? cityBusinessController.deleteCity(params, role)
				: new UnprocessableEntityException();
	}
}

