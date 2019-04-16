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

import fr.alten.ambroiseJEE.controller.business.SkillBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the skill web service
 *
 * @author Thomas Decamp
 *
 */
@Controller
public class SkillRestController {

	@Autowired
	private SkillBusinessController skillBusinessController;

	private final Gson gson;

	public SkillRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/skill")
	@ResponseBody
	public HttpException createSkill(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? skillBusinessController.createSkill(params, role)
				: new UnprocessableEntityException();
	}

	@DeleteMapping(value = "/skill")
	@ResponseBody
	public HttpException deleteSkill(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? skillBusinessController.deleteSkill(params, role)
				: new UnprocessableEntityException();
	}

	@GetMapping(value = "/skills")
	@ResponseBody
	public String getSkills(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(skillBusinessController.getSkills(role));
	}

	@PutMapping(value = "/skill")
	@ResponseBody
	public HttpException updateSkill(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? skillBusinessController.createSkill(params, role)
				: new UnprocessableEntityException();
	}
}
