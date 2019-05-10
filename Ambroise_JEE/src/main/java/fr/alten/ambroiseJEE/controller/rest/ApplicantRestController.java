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
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest Controller for Applicant
 *
 * @author Lucas Royackkers
 *
 */
@Controller
public class ApplicantRestController {

	public boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}
	
	@Autowired
	private ApplicantBusinessController applicantBusinessController;

	private final Gson gson;

	public ApplicantRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Method to create an Applicant (Person)
 	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ConflictException} if there is a conflict in the database and
	 *         {@link CreatedException} if the person(applicant) is created
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	@PostMapping(value = "/applicant")
	@ResponseBody
	public HttpException createApplicant(@RequestBody final JsonNode params,@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws ParseException {
		return checkJsonIntegrity(params, "mail")
				? this.applicantBusinessController.createApplicant(params, role, mail)
				: new UnprocessableEntityException();
	}

	/**
	 * Method to delete an Applicant
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the person(applicant) is deleted
	 * @author Lucas Royackkers
	 */
	@DeleteMapping(value = "/applicant")
	@ResponseBody
	public HttpException deleteApplicant(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "mail")
				? this.applicantBusinessController.deleteApplicant(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Method to get a specific Applicant given its name
	 *
	 * @param applicantName the applicant's name
	 * @param mail          the current logged user's mail
	 * @param role          the user's role
	 * @return an applicant, given its name (can be empty)
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/applicant/{mail}")
	@ResponseBody
	public String getApplicant(@PathVariable("mail") final String applicantMail,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.applicantBusinessController.getApplicant(applicantMail, role));
	}

	/**
	 * Method to get all applicants
	 *
	 * @param mail the current logged user's mail
	 * @param role the user's role
	 * @return the list of all applicants
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/applicants")
	@ResponseBody
	public String getApplicants(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.applicantBusinessController.getApplicants(role));
	}

	/**
	 * Method to update an Applicant given its mail
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the person(applicant) is updated
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	@PutMapping(value = "/applicant")
	@ResponseBody
	public HttpException updateApplicant(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role)
			throws ParseException {
		return checkJsonIntegrity(params, "mail")
				? this.applicantBusinessController.updateApplicant(params, role, mail)
				: new UnprocessableEntityException();
	}
}
