package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.ConsultantBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest Controller for Consultant
 * 
 * @author Lucas Royackkers
 *
 */
@Controller
public class ConsultantRestController {
	private ConsultantBusinessController consultantBusinessController;
	
	private final Gson gson;
	
	public ConsultantRestController() {
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
	 *         and {@link CreatedException} if the person(consultant) is created
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Lucas Royackkers
	 */
	@PostMapping(value = "/consultant")
	@ResponseBody
	public HttpException createConsultant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? consultantBusinessController.createConsultant(params, role)
				: new UnprocessableEntityException();
	}

	
	/**
	 * 
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return the list of all consultants
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/consultants")
	@ResponseBody
	public String getConsultants(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(consultantBusinessController.getConsultants(role));
	}
}
