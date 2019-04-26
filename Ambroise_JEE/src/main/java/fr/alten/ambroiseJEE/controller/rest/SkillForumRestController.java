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

import fr.alten.ambroiseJEE.controller.business.SkillForumBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the skill web service
 *
 * @author Thomas Decamp
 *
 */
@Controller
public class SkillForumRestController {

	@Autowired
	private SkillForumBusinessController skillForumBusinessController;

	private final Gson gson;

	public SkillForumRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/forum/skill")
	@ResponseBody
	public HttpException createSkill(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "mail")
				? this.skillForumBusinessController.createSkillForum(params, role)
				: new UnprocessableEntityException();
	}

	@DeleteMapping(value = "/forum/skill")
	@ResponseBody
	public HttpException deleteSkill(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "mail")
				? this.skillForumBusinessController.deleteSkillForum(params, role)
				: new UnprocessableEntityException();
	}

	@GetMapping(value = "/forum/skill")
	@ResponseBody
	public String getSkill(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return this.gson.toJson(this.skillForumBusinessController.getSkillForum(params, role));
	}

	@GetMapping(value = "/forum/skills")
	@ResponseBody
	public String getSkills(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillForumBusinessController.getSkillsForum(role));
	}

	@PutMapping(value = "/forum/skill")
	@ResponseBody
	public HttpException updateSkill(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "mail")
				? this.skillForumBusinessController.createSkillForum(params, role)
				: new UnprocessableEntityException();
	}
}
