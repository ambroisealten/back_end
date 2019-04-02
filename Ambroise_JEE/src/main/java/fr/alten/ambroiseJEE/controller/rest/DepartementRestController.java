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

import fr.alten.ambroiseJEE.controller.business.geographic.DepartementBusinessController;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the city web service
 * 
 * @author Andy Chabalier
 *
 */
@Controller
public class DepartementRestController {

	@Autowired
	private DepartementBusinessController departementBusinessController;

	private final Gson gson;

	public DepartementRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/departement")
	@ResponseBody
	public HttpException createDepartement(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? departementBusinessController.createDepartement(params, role)
				: new UnprocessableEntityException();
	}

	@GetMapping(value = "/departements")
	@ResponseBody
	public String getDepartements(@RequestAttribute("mail") String mail, @RequestAttribute("role") int role) {
		return gson.toJson(departementBusinessController.getDepartements(role));
	}

	@PutMapping(value = "/departement")
	@ResponseBody
	public HttpException updateDepartement(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? departementBusinessController.createDepartement(params, role)
				: new UnprocessableEntityException();
	}

	@DeleteMapping(value = "/departement")
	@ResponseBody
	public HttpException deleteDepartement(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? departementBusinessController.deleteDepartement(params, role)
				: new UnprocessableEntityException();
	}
}


