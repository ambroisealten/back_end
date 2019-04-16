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

import fr.alten.ambroiseJEE.controller.business.TechSkillBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;


/**
 * Rest controller for the techSkill web service
 * 
 * @author Thomas Decamp
 *
 */
@Controller
public class TechSkillRestController {

	@Autowired
	private TechSkillBusinessController techSkillBusinessController;

	private final Gson gson;

	public TechSkillRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/techSkill")
	@ResponseBody
	public HttpException createTechSkill(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return  techSkillBusinessController.createTechSkill(params, role);				
	}

	@GetMapping(value = "/techSkills")
	@ResponseBody
	public String getTechSkills(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(techSkillBusinessController.getTechSkills(role));
	}

	@PutMapping(value = "/techSkill")
	@ResponseBody
	public HttpException updateTechSkill(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return  techSkillBusinessController.updateTechSkill(params, role);				
	}

	@DeleteMapping(value = "/techSkill")
	@ResponseBody
	public HttpException deleteTechSkill(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return  techSkillBusinessController.deleteTechSkill(params, role);				
	}
}
