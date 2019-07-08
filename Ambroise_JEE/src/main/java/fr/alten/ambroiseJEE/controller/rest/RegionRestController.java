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

import fr.alten.ambroiseJEE.controller.business.geographic.RegionBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the Region web service
 *
 * @author Andy Chabalier
 *
 */
@Controller
public class RegionRestController {

	@Autowired
	private RegionBusinessController regionBusinessController;

	private final Gson gson;

	public RegionRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	public boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}

	/**
	 * Rest controller for region creation. HTTP Method : POST
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the the JsonNode don't have
	 *         the parameters name and then we can't process the entity and
	 *         {@link CreatedException} if the region is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/region")
	@ResponseBody
	public HttpException createRegion(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "nom", "code") ? this.regionBusinessController.createRegion(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller for region deletion. HTTP Method : DELETE
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link OkException} if the region is deleted
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@DeleteMapping(value = "/region")
	@ResponseBody
	public HttpException deleteRegion(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "code") ? this.regionBusinessController.deleteRegion(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to fetch all regions. HTTP Method : GET
	 *
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return the list of all regions
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/regions")
	@ResponseBody
	public String getRegions(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.regionBusinessController.getRegions(role));
	}

	/**
	 * Rest controller to update region. HTTP Method : PUT
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user mail
	 * @param role   the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link OkException} if the region is updated
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PutMapping(value = "/region")
	@ResponseBody
	public HttpException updateRegion(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "nom", "code") ? this.regionBusinessController.updateRegion(params, role)
				: new UnprocessableEntityException();
	}
}
