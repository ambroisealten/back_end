package fr.alten.ambroiseJEE.controller.rest;

import java.text.ParseException;
import java.util.List;

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

import fr.alten.ambroiseJEE.controller.business.DiplomaBusinessController;
import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the diploma web service
 *
 * @author Lucas Royackkers
 *
 */
@Controller
public class DiplomaRestController {

	@Autowired
	private DiplomaBusinessController diplomaBusinessController;

	private final Gson gson;

	public DiplomaRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Create a Diploma 
	 * 
	 * @param params the JsonNode containing all parameters (name and date)
	 * @param role the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database, {@link UnprocessableEntityException} if there are not enough parameters to perform the action
	 *         and {@link CreatedException} if the diploma is created
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@PostMapping("/diploma")
	@ResponseBody
	public HttpException createDiploma(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role)
			throws ParseException {
		return (params.get("name") != null && params.get("yearOfResult") != null) ?
				diplomaBusinessController.createDiploma(params, role) : new UnprocessableEntityException();
	}

	/**
	 * Delete a Diploma
	 * 
	 * @param params the JsonNode containing all parameters (name, date)
	 * @param role the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database, {@link UnprocessableEntityException} if there are not enough parameters to perform the action
	 *         and {@link CreatedException} if the diploma is deleted
	 * @author Lucas Royackkers
	 */
	@DeleteMapping("/diploma")
	@ResponseBody
	public HttpException deleteDiploma(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
		return (params.get("name") != null && params.get("yearOfResult") != null) ?
				diplomaBusinessController.deleteDiploma(params, role) : new UnprocessableEntityException();
	}

	/**
	 * Get all Diplomas 
	 * 
	 * @param role the current logged user's role
	 * @return a String containing a representation of a JsonNode, containing a list of all diplomas (can be empty)
	 * @author Lucas Royackkers
	 */
	@GetMapping("/diplomas")
	@ResponseBody
	public String getDiplomas(@RequestAttribute("role") UserRole role) {
		return gson.toJson(diplomaBusinessController.getDiplomas(role));
	}

	/**
	 * Get all Diplomas given their name
	 * 
	 * @param role the current logged user's role
	 * @param name the name of the Diploma
	 * @return a List of all Diplomas given a specific name (the list can be empty)
	 * @author Lucas Royackkers
	 */
	@GetMapping("/diplomas/{name}")
	@ResponseBody
	public List<Diploma> getDiplomasByName(@RequestAttribute("role") UserRole role, @PathVariable("name") String name) {
		return diplomaBusinessController.getDiplomasByName(name, role);

	}

	/**
	 * Update a Diploma 
	 * 
	 * @param params the JsonNode containing all parameters (oldName, name, oldYearOfResult, yearOfResult)
	 * @param role the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database, {@link UnprocessableEntityException} if there are not enough parameters to perform the action
	 *         and {@link CreatedException} if the diploma is updated
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@PutMapping("/diploma")
	@ResponseBody
	public HttpException updateDiploma(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role)
			throws ParseException {
		return (params.get("name") != null && params.get("oldName") != null && params.get("oldYearOfResult") != null && params.get("yearOfResult") != null) ?
				diplomaBusinessController.updateDiploma(params, role) : new UnprocessableEntityException();
	}

}
