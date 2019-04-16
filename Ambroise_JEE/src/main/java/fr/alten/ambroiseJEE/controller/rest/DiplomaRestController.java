package fr.alten.ambroiseJEE.controller.rest;

import java.text.ParseException;

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

import fr.alten.ambroiseJEE.controller.business.DiplomaBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Rest controller for the diploma web service
 *
 * @author Lucas Royackkers
 *
 */
@Controller
public class DiplomaRestController {

	@Autowired
	private DiplomaBusinessController diplomaBusinessController;

	private final Gson gson;

	public DiplomaRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping("/diploma")
	@ResponseBody
	public HttpException createDiploma(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role)
			throws ParseException {
		return diplomaBusinessController.createDiploma(params, role);
	}

	@DeleteMapping("/diploma")
	@ResponseBody
	public HttpException deleteDiploma(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
		return diplomaBusinessController.deleteDiploma(params, role);
	}

	@GetMapping("/diplomas")
	@ResponseBody
	public String getDiplomas(@RequestAttribute("role") UserRole role) {
		return gson.toJson(diplomaBusinessController.getDiplomas(role));
	}

	@PutMapping("/diploma")
	@ResponseBody
	public HttpException updateDiploma(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role)
			throws ParseException {
		return diplomaBusinessController.updateDiploma(params, role);
	}

}
