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
		return consultantBusinessController.createConsultant(params, role);
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
	
	/**
	 * @param consultantName the consultant's name
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return a consultant, given its name
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/consultant/{name}")
	@ResponseBody
	public String getConsultant(@PathVariable("name") String consultantName, @RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(consultantBusinessController.getConsultant(consultantName,role));
	}
	
	/**
	 * 
	 * @param params the JsonNode containing post parameters from http request
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link CreatedException} if the person(consultant) is updated
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	@PutMapping(value = "/consultant")
	@ResponseBody
	public HttpException updateConsultant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws ParseException {
		return consultantBusinessController.updateConsultant(params, role);
	}
	
	/**
	 * 
	 * @param params the JsonNode containing post parameters from http request
	 * @param mail the user's mail
	 * @param role the user's role
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link CreatedException} if the person(consultant) is deleted
	 * @author Lucas Royackkers
	 */
	@DeleteMapping(value = "/consultant")
	@ResponseBody
	public HttpException deleteConsultant(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) {
		return consultantBusinessController.deleteConsultant(params, role);
	}
	
}
