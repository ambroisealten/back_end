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

import fr.alten.ambroiseJEE.controller.business.SoftSkillBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;


/**
 * Rest controller for the softSkill web service
 * 
 * @author Thomas Decamp
 *
 */
@Controller
public class SoftSkillRestController {

	@Autowired
	private SoftSkillBusinessController softSkillBusinessController;

	private final Gson gson;

	public SoftSkillRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/softSkill")
	@ResponseBody
	public HttpException createSoftSkill(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return  softSkillBusinessController.createSoftSkill(params, role);
				
	}

	@GetMapping(value = "/softSkills")
	@ResponseBody
	public String getSoftSkills(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(softSkillBusinessController.getSoftSkills(role));
	}

	@PutMapping(value = "/softSkill")
	@ResponseBody
	public HttpException updateSoftSkill(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return  softSkillBusinessController.updateSoftSkill(params, role);				
	}

	@DeleteMapping(value = "/softSkill")
	@ResponseBody
	public HttpException deleteSoftSkill(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return  softSkillBusinessController.deleteSoftSkill(params, role);
	}
}
