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
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
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
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Rest controller to create an forum applicant. HTTP Method : POST
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if mail is not provided and
	 *         {@link CreatedException} if the applicant is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/forum/applicant")
	@ResponseBody
	public HttpException createApplicant(@RequestBody final JsonNode params,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return params.get("mail") != null ? this.applicantForumBusinessController.createApplicant(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to delete an forum applicant. HTTP Method : DELETE
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the mail is not provided and
	 *         {@link OkException} if the person(applicant) is deleted
	 * @author Andy Chabalier
	 */
	@DeleteMapping(value = "/forum/applicant")
	@ResponseBody
	public HttpException deleteApplicant(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return params.get("mail") != null ? this.applicantForumBusinessController.deleteApplicant(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Rest controller to fetch a specific applicant. HTTP Method : GET
	 *
	 * @param applicantMail the applicant's name
	 * @param mail          the user's mail
	 * @param role          the user's role
	 * @return an applicant, given its name
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/forum/applicant/{mail}")
	@ResponseBody
	public String getApplicant(@PathVariable("mail") final String applicantMail,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.applicantForumBusinessController.getApplicant(applicantMail, role));
	}

	/**
	 * Rest controller to fetch all forum applicants. HTTP Method : GET
	 *
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return the list of all applicants
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/forum/applicants")
	@ResponseBody
	public String getApplicants(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.applicantForumBusinessController.getApplicants(role));
	}

	/**
	 * Rest controller to update a forum applicant
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the mail is not provided and
	 *         {@link OkException} if the applicant is updated
	 * @author Andy Chabalier
	 * @throws ParseException
	 */
	@PutMapping(value = "/forum/applicant")
	@ResponseBody
	public HttpException updateApplicant(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role)
			throws ParseException {
		return params.get("mail") != null ? this.applicantForumBusinessController.updateApplicant(params, role)
				: new UnprocessableEntityException();
	}
}
