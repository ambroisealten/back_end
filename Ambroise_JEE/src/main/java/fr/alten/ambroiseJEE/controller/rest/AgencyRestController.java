/**
 * 
 */
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

import fr.alten.ambroiseJEE.controller.business.AgencyBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the agency web service
 * 
 * @author Andy Chabalier
 *
 */
@Controller
public class AgencyRestController {

	@Autowired
	private AgencyBusinessController agencyBusinessController;

	private final Gson gson;

	public AgencyRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/agency")
	@ResponseBody
	public HttpException createAgency(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? agencyBusinessController.createAgency(params, role)
				: new UnprocessableEntityException();
	}

	@GetMapping(value = "/agencies")
	@ResponseBody
	public String getAgencies(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(agencyBusinessController.getAgencies(role));
	}

	@PutMapping(value = "/agency")
	@ResponseBody
	public HttpException updateAgency(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? agencyBusinessController.updateAgency(params, role)
				: new UnprocessableEntityException();
	}

	@DeleteMapping(value = "/agency")
	@ResponseBody
	public HttpException deleteAgency(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? agencyBusinessController.deleteAgency(params, role)
				: new UnprocessableEntityException();
	}
}

