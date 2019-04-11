package fr.alten.ambroiseJEE.controller.rest;

import java.text.ParseException;

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

import fr.alten.ambroiseJEE.controller.business.ApplicantBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest Controller for Applicant
 * 
 * @author Lucas Royackkers
 *
 */
@Controller
public class ApplicantRestController {
	
	@Autowired
	private ApplicantBusinessController applicantBusinessController;
	
	private final Gson gson;
	
	public ApplicantRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}
	
	/**
	 * 
	 * @param params JsonNode containing post parameters from http request 
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link CreatedException} if the person(applicant) is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Lucas Royackkers
	 */
	@PostMapping(value = "/applicant")
	@ResponseBody
	public HttpException createApplicant(@RequestBody JsonNode params,
			@RequestAttribute("role") UserRole role) throws Exception {
		return applicantBusinessController.createApplicant(params, role);
	}

	/**
	 * 
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return the list of all applicants
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/applicants")
	@ResponseBody
	public String getApplicants(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(applicantBusinessController.getApplicants(role));
	}
	
	/**	
	 * 
	 * @param applicantName the applicant's name
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return an applicant, given its name
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/applicant/{name}")
	@ResponseBody
	public String getApplicant(@PathVariable("name") String applicantName, @RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(applicantBusinessController.getApplicant(applicantName,role));
	}
	
	/**
	 * 
	 * @param params JsonNode containing post parameters from http request
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link CreatedException} if the person(applicant) is updated
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	@PutMapping(value = "/applicant")
	@ResponseBody
	public HttpException updateApplicant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws ParseException {
		return params.get("mail") != null ? applicantBusinessController.updateApplicant(params, role)
				: new UnprocessableEntityException();
	}
	
	/**
	 * 
	 * @param params JsonNode containing post parameters from http request
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link CreatedException} if the person(applicant) is deleted
	 * @author Lucas Royackkers
	 */
	@DeleteMapping(value = "/applicant")
	@ResponseBody
	public HttpException deleteApplicant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) {
		return params.get("mail") != null ? applicantBusinessController.deleteApplicant(params, role)
				: new UnprocessableEntityException();
	}
}
