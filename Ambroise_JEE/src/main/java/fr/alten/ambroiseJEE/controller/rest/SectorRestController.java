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

import fr.alten.ambroiseJEE.controller.business.SectorBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for sector service.
 *
 * @author Andy Chabalier
 *
 */
@Controller
public class SectorRestController {

	@Autowired
	private SectorBusinessController sectorBusinessController;

	private final Gson gson;

	public SectorRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	public boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}

	/**
	 * Create a new sector. HTTP method POST
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link CreatedException} if the sector is created
	 * @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/sector")
	@ResponseBody
	public HttpException createSector(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "name") ? this.sectorBusinessController.createSector(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Delete a sector. HTTP method DELETE
	 *
	 * @param params contains the mail of the sector to delete
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link OkException} if the sector is deleted successfully
	 * @author Andy Chabalier
	 */
	@DeleteMapping(value = "/sector")
	@ResponseBody
	public HttpException deleteSector(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "name") ? this.sectorBusinessController.deleteSector(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 *
	 * List all sectors. HTTP method GET
	 *
	 * @param mail the user mail
	 * @param role the user role
	 * @return the list of all sectors
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/sector")
	@ResponseBody
	public String getSectors(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.sectorBusinessController.getSectors(role));
	}

	/**
	 * Update a sector. HTTP method PUT
	 *
	 * @param params JsonNode containing put parameters from http request
	 * @param mail   the user mail
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link OkException} if the sector is updated successfully
	 * @author Andy Chabalier
	 */
	@PutMapping(value = "/sector")
	@ResponseBody
	public HttpException updateSector(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "name", "oldName") ? this.sectorBusinessController.updateSector(params, role)
				: new UnprocessableEntityException();
	}
}
