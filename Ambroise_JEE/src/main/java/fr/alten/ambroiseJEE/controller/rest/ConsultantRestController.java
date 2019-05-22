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

import fr.alten.ambroiseJEE.controller.business.ConsultantBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest Controller for Consultant
 *
 * @author Lucas Royackkers
 *
 */
@Controller
public class ConsultantRestController {
	@Autowired
	private ConsultantBusinessController consultantBusinessController;

	private final Gson gson;

	public ConsultantRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	public boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}

	/**
	 * Method to create a Consultant (Person)
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ConflictException} if the resource is not found and
	 *         {@link CreatedException} if the person(consultant) is created
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	@PostMapping(value = "/consultant")
	@ResponseBody
	public HttpException createConsultant(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role)
			throws ParseException {
		return checkJsonIntegrity(params, "mail")
				? this.consultantBusinessController.createConsultant(params, role, mail)
				: new UnprocessableEntityException();
	}

	/**
	 * Method to create an Consultant and a Skills Sheet in one way
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ConflictException} if there is a conflict in the database,
	 *         {@link UnprocessableEntityException} if the resources aren't well
	 *         constructed and {@link CreatedException} if the person(consultant) is
	 *         created
	 * @author Lucas Royackkers
	 */
	@PostMapping(value = "/consultantAndSkillsSheet")
	@ResponseBody
	public HttpException createConsultantAndSkillsSheet(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "person", "skillsSheet")
				? this.consultantBusinessController.createConsultantAndSkillsSheet(params, role, mail)
				: new UnprocessableEntityException();
	}

	/**
	 * Method to delete a Consultant given its mail
	 *
	 * @param params the JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the person(consultant) is deleted
	 * @author Lucas Royackkers
	 */
	@DeleteMapping(value = "/consultant")
	@ResponseBody
	public HttpException deleteConsultant(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(params, "mail") ? this.consultantBusinessController.deleteConsultant(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Method to get a Consultant given its mail
	 *
	 * @param consultantName the consultant's name
	 * @param mail           the current logged user's mail
	 * @param role           the user's role
	 * @return a consultant, given its name
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/consultant/{mail}")
	@ResponseBody
	public String getConsultant(@PathVariable("mail") final String consultantMail,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.consultantBusinessController.getConsultant(consultantMail, role));
	}

	/**
	 * Method to get all Consultants
	 *
	 * @param mail the current logged user's mail
	 * @param role the user's role
	 * @return the list of all consultants
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/consultants")
	@ResponseBody
	public String getConsultants(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.consultantBusinessController.getConsultants(role));
	}

	/**
	 * Method to update a Consultant
	 *
	 * @param params the JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResoucreNotFoundException} if the resource is not found and
	 *         {@link OkException} if the person(consultant) is updated
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	@PutMapping(value = "/consultant")
	@ResponseBody
	public HttpException updateConsultant(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role)
			throws ParseException {
		return checkJsonIntegrity(params, "mail")
				? this.consultantBusinessController.updateConsultant(params, role, mail)
				: new UnprocessableEntityException();
	}

}
