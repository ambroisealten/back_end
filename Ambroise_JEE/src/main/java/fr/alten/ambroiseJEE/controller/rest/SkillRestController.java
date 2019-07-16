/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.util.ArrayList;

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

	public boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}

	/**
	 * Method to create a Skill
	 *
	 * @param params the JsonNode containing all parameters to create a Skill (name,
	 *               eventually a String if the Skill is a Soft Skill)
	 * @param mail   the current logged user's mail
	 * @param role   the current logged user's role
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
	public HttpException createSkill(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role)
			throws Exception {
		return checkJsonIntegrity(params, "name") ? this.skillBusinessController.createSkill(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Method to checks if a Soft Skill exists
	 * @param name the name of the searched Soft Skill
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return true if the specific Soft Skill exists, otherwise false
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/softSkillExists/{name}")
	@ResponseBody
	public boolean checkIfSoftSkillExists(@PathVariable("name") final String name,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.skillBusinessController.checkIfSoftSkillExists(name, role);
	}

	/**
	 * Method to delete a Skill
	 *
	 * @param params the JsonNode containing post parameters from HTTP
	 * @param mail   the current logged user's mail
	 * @param role   the current logged user's role
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
	public HttpException deleteSkill(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role)
			throws Exception {
		return checkJsonIntegrity(params, "name") ? this.skillBusinessController.deleteSkill(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Fetch a skill given a name
	 *
	 * @param params the JsonNode containing all parameters (name of the searched
	 *               skill)
	 * @param mail   the current logged user's mail
	 * @param role   the current logged user's role
	 * @return a JsonNode containing the searched skill (can be empty)
	 * @author Thomas Decamp
	 */
	@GetMapping(value = "/skill")
	@ResponseBody
	public String getSkill(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role)
			throws Exception {
		return this.gson.toJson(this.skillBusinessController.getSkill(params, role));
	}

	/**
	 * Fetch all skills
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return a JsonNode containing all the skills within the database (can be
	 *         empty)
	 * @author Thomas Decamp
	 */
	@GetMapping(value = "/skills")
	@ResponseBody
	public String getSkills(@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillBusinessController.getSkills(role));
	}

	/**
	 * Fetch all soft skills
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return a JsonNode containing all the soft skills within the database (can be
	 *         empty)
	 * @author Andy Chabalier
	 */
	@GetMapping(value = "/softskills")
	@ResponseBody
	public String getSoftSkills(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillBusinessController.getSoftSkills(role));
	}

	/**
	 * Fetch all tech skills
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return a JsonNode containing all the tech skills within the database (can be
	 *         empty)
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/techskills")
	@ResponseBody
	public String getTechSkills(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillBusinessController.getTechSkills(role));
	}

	/**
	 * Method to update a Skill
	 *
	 * @param params the JsonNode containing all the parameters (old name and the
	 *               new name of the skill)
	 * @param mail   the current logged user's mail
	 * @param role   the current logged user's role
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
	public HttpException updateSkill(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role)
			throws Exception {
		return checkJsonIntegrity(params, "name", "oldName") ? this.skillBusinessController.updateSkill(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Method to update the order of all SoftSkills
	 * 
	 * @param params the JsonNode containing all the Soft Skills (with name and order)
	 * @param role the current logged user's role
	 * @return a list of {@link HttpException}
	 * @throws Exception
	 * @author Lucas Royackkers
	 */
	@PutMapping(value = "/softSkillsOrder")
	@ResponseBody
	public ArrayList<HttpException> updateSoftSkillsOrder(@RequestBody final JsonNode params,
			@RequestAttribute("role") final UserRole role) throws Exception {
		if (params.get("softSkillsList").isArray()) {
			return this.skillBusinessController.updateSoftSkillsOrder(params, role);
		}
		ArrayList<HttpException> result = new ArrayList<HttpException>();
		result.add(new UnprocessableEntityException());
		return result;
	}

	/**
	 * 
	 *
	 * @param params
	 * @param role
	 * @return
	 * @author Thomas Decamp
	 */
	@GetMapping(value = "/skillsSynonymous")
	@ResponseBody
	public String getSkillsSynonymous(@RequestAttribute("mail") final String mail,
	@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillBusinessController.getSynonymousList(role));
	}

	/**
	 * 
	 *
	 * @param params
	 * @param role
	 * @return
	 * @author Thomas Decamp
	 */
	@PutMapping(value = "/skillsSynonymous")
	@ResponseBody
	public HttpException updateSynonymousList(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role) throws Exception {
		return checkJsonIntegrity(params, "name") ? this.skillBusinessController.updateSynonymousList(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * 
	 *
	 * @param params
	 * @param role
	 * @return
	 * @author Thomas Decamp
	 */
	@DeleteMapping(value = "/skillsSynonymous")
	@ResponseBody
	public HttpException deleteSynonymous(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role) throws Exception {
		return checkJsonIntegrity(params, "name") ? this.skillBusinessController.deleteSynonymous(params, role)
				: new UnprocessableEntityException();
	}
}
