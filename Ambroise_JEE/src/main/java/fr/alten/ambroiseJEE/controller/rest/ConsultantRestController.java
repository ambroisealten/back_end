package fr.alten.ambroiseJEE.controller.rest;

import static fr.alten.ambroiseJEE.utils.JsonUtils.checkJsonIntegrity;

import java.text.ParseException;
import java.util.Optional;

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
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.security.UserRole;
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
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the user's mail
	 * @param role   the user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ConflictException} if the resource is not found and
	 *         {@link CreatedException} if the person(consultant) is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Lucas Royackkers
	 */
	@PostMapping(value = "/consultant")
	@ResponseBody
	public HttpException createConsultant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return (checkJsonIntegrity(params,"mail")) ? consultantBusinessController.createConsultant(params, role) : new UnprocessableEntityException();
	}

	/**
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
	public HttpException deleteConsultant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) {
		return (checkJsonIntegrity(params,"mail")) ? consultantBusinessController.deleteConsultant(params, role) : new UnprocessableEntityException();
	}

	/**
	 * @param consultantName the consultant's name
	 * @param mail           the current logged user's mail
	 * @param role           the user's role
	 * @return a consultant, given its name
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/consultant/{mail}")
	@ResponseBody
	public String getConsultant(@PathVariable("mail") String consultantMail, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) {
		Optional<Person> personOptional = consultantBusinessController.getConsultant(consultantMail, role);
		if (personOptional.isPresent()) {
			return gson.toJson(personOptional.get());
		}
		throw new ResourceNotFoundException();
	}

	/**
	 *
	 * @param mail the current logged user's mail
	 * @param role the user's role
	 * @return the list of all consultants
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/consultants")
	@ResponseBody
	public String getConsultants(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(consultantBusinessController.getConsultants(role));
	}

	/**
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
	public HttpException updateConsultant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws ParseException {
		return (checkJsonIntegrity(params,"mail","oldMail")) ?  consultantBusinessController.updateConsultant(params, role) : new UnprocessableEntityException();
	}

}
