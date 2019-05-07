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
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
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
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Method to create a Skill
	 * 
	 * @param params the JsonNode containing all parameters to create a Skill (name, eventually a String if the Skill is a Soft Skill)
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link UnprocessableEntityException} if we can't create the resource,
	 *         {@link ConflictException} if there is a conflict in the database,
	 *         {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action and {@link CreatedException} if the
	 *         skill is created
	 * @author Thomas Decamp
	 */
	@PostMapping(value = "/skill")
	@ResponseBody
	public HttpException createSkill(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "mail","name") ? this.skillBusinessController.createSkill(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Method to delete a Skill
	 * 
	 * @param params the JsonNode containing post parameters from HTTP 
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link UnprocessableEntityException} if we can't create the resource,
	 *         {@link ConflictException} if there is a conflict in the database,
	 *         {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action and {@link CreatedException} if the
	 *         skill is deleted
	 * @author Thomas Decamp
	 */
	@DeleteMapping(value = "/skill")
	@ResponseBody
	public HttpException deleteSkill(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "mail","name") ? this.skillBusinessController.deleteSkill(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Fetch a skill given a name
	 * 
	 * @param params the JsonNode containing all parameters (name of the searched skill)
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return a JsonNode containing the searched skill (can be empty)
	 * @author Thomas Decamp
	 */
	@GetMapping(value = "/skill")
	@ResponseBody
	public String getSkill(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return this.gson.toJson(this.skillBusinessController.getSkill(params, role));
	}

	/**
	 * Fetch all skills
	 * 
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return a JsonNode containing all the skills within the database (can be empty)
	 * @author Thomas Decamp
	 */
	@GetMapping(value = "/skills")
	@ResponseBody
	public String getSkills(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillBusinessController.getSkills(role));
	}

	/**
	 * Method to update a Skill 
	 * 
	 * @param params the JsonNode containing all the parameters (old name and the new name of the skill)
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link UnprocessableEntityException} if we can't create the resource,
	 *         {@link ConflictException} if there is a conflict in the database,
	 *         {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action and {@link CreatedException} if the
	 *         skill is updated
	 * @author Thomas Decamp
	 */
	@PutMapping(value = "/skill")
	@ResponseBody
	public HttpException updateSkill(@RequestBody final JsonNode params, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws Exception {
		return JsonUtils.checkJsonIntegrity(params, "name", "oldName", "mail")
				? this.skillBusinessController.createSkill(params, role)
				: new UnprocessableEntityException();
	}

}
