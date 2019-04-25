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

import fr.alten.ambroiseJEE.controller.business.EmployerBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the employer web service
 *
 * @author Lucas Royackkers
 *
 */
@Controller
public class EmployerRestController {

	@Autowired
	private EmployerBusinessController employerBusinessController;

	private final Gson gson;

	public EmployerRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Create an Employer
	 *
	 * @param params the JsonNode containing all parameters (only the name of the employer)
	 * @param role the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database, {@link UnprocessableEntityException} if there are not enough parameters to perform the action
	 *         and {@link CreatedException} if the employer is created
	 * @author Lucas Royackkers
	 */
	@PostMapping("/employer")
	@ResponseBody
	public HttpException createEmployer(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
		return (checkJsonIntegrity(params,"name")) ? employerBusinessController.createEmployer(params, role) : new UnprocessableEntityException();
	}

	/**
	 * Delete an Employer
	 *
	 * @param params the JsonNode containing all parameters (only the name of the employer)
	 * @param role the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ResourceNotFoundException} if the resource is not
	 *         found, {@link UnprocessableEntityException} if there are not enough parameters to perform the action
	 *         and {@link OkException} if the employer is deleted
	 * @author Lucas Royackkers
	 */
	@DeleteMapping("/employer")
	@ResponseBody
	public HttpException deleteEmployer(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
		return (checkJsonIntegrity(params,"name")) ? employerBusinessController.deleteEmployer(params, role) : new UnprocessableEntityException();
	}

	/**
	 * Get all Employers 
	 *
	 * @param role the current logged user's role
	 * @return a String representing a Json, containing all the Employers
	 * @author Lucas Royackkers
	 */
	@GetMapping("/employers")
	@ResponseBody
	public String getEmployers(@RequestAttribute("role") UserRole role) {
		return gson.toJson(employerBusinessController.getEmployers(role));
	}

	/**
	 * Update an employer
	 *
	 * @param params the JsonNode containing all parameters (the name of the employer and its oldname)
	 * @param role the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, ({@link ResourceNotFoundException} if the resource is not
	 *         found, {@link UnprocessableEntityException} if there are not enough parameters to perform the action
	 *         and {@link OkException} if the employer is updated
	 * @author Lucas Royackkers
	 */
	@PutMapping("/employer")
	@ResponseBody
	public HttpException updateEmployer(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
		return (checkJsonIntegrity(params,"name","oldName")) ? 
				employerBusinessController.updateEmployer(params, role) : new UnprocessableEntityException();
	}
	
	
	public static boolean checkJsonIntegrity(JsonNode params, String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}

}
