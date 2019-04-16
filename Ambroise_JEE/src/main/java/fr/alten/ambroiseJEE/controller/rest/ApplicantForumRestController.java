/**
 * 
 */
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

import fr.alten.ambroiseJEE.controller.business.ApplicantForumBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * @author Andy Chabalier
 *
 */
@Controller
public class ApplicantForumRestController {
	
	@Autowired
	private ApplicantForumBusinessController applicantForumBusinessController;
	
	private final Gson gson;
	
	public ApplicantForumRestController() {
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
	 *         and {@link CreatedException} if the applicant is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/forum/applicant")
	@ResponseBody
	public HttpException createApplicant(@RequestBody JsonNode params,
			@RequestAttribute("role") UserRole role) throws Exception {
		return applicantForumBusinessController.createApplicant(params, role);
	}

	/**
	 * 
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return the list of all applicants
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/forum/applicants")
	@ResponseBody
	public String getApplicants(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(applicantForumBusinessController.getApplicants(role));
	}
	
	/**	
	 * 
	 * @param applicantName the applicant's name
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return an applicant, given its name
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/forum/applicant/{name}")
	@ResponseBody
	public String getApplicant(@PathVariable("name") String applicantName, @RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(applicantForumBusinessController.getApplicant(applicantName,role));
	}
	
	/**
	 * 
	 * @param params JsonNode containing post parameters from http request
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link CreatedException} if the applicant is updated
	 * @author Andy Chabalier
	 * @throws ParseException 
	 */
	@PutMapping(value = "/forum/applicant")
	@ResponseBody
	public HttpException updateApplicant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws ParseException {
		return params.get("mail") != null ? applicantForumBusinessController.updateApplicant(params, role)
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
	 * @author Andy Chabalier
	 */
	@DeleteMapping(value = "/forum/applicant")
	@ResponseBody
	public HttpException deleteApplicant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) {
		return params.get("mail") != null ? applicantForumBusinessController.deleteApplicant(params, role)
				: new UnprocessableEntityException();
	}
}
