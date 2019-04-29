package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

import fr.alten.ambroiseJEE.controller.business.SkillsSheetBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest Controller for Skills Sheet
 *
 * @author Lucas Royackkers
 *
 */
@Controller
public class SkillsSheetRestController {

	@Autowired
	private SkillsSheetBusinessController skillsSheetBusinessController;

	private final Gson gson;

	public SkillsSheetRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Get all Skills Sheets given a person (with its mail)
	 * 
	 * @param mailPerson the mail of the person
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return a String containing a JsonNode with a List of Skills Sheets given the person attached to
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/skillsheetMail/{mail}")
	@ResponseBody
	public String getSkillsSheetByMail(@PathVariable("mail") final String mailPerson,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillsSheetBusinessController.getSkillsSheetByMail(mailPerson, role));
	}
	
	/**
	 * Get all versions of a Skills Sheet given their common name and person attached to
	 * 
	 * @param mailPerson the mail of the person attached to the different versions
	 * @param role the current logged user's role
	 * @param name the name of the Skills Sheet
	 * @param mail the current logged user's mail
	 * @return a String containing a JsonNode that contains a List of Skills Sheet
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/skillsheetVersion/{name}/{mail}")
	@ResponseBody
	public String getSkillsSheetVersions(@PathVariable("mail") final String mailPerson, @RequestAttribute("role") UserRole role,
			@PathVariable("name") final String name, @RequestAttribute("mail") final String mail) {
		return this.gson.toJson(this.skillsSheetBusinessController.getSkillsSheetVersion(name,mailPerson,role));
	}

	/**
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link UnprocessableEntityException} if we can't create the resource,
	 *         {@link ConflictException} if there is a conflict in the database,
	 *         {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action and {@link CreatedException} if the
	 *         skills sheet is created
	 * @author Lucas Royackkers
	 */
	@PostMapping(value = "/skillsheet")
	@ResponseBody
	public HttpException createSkillsSheet(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role)
			throws Exception {
		return params.get("name") != null && params.get("mailPersonAttachedTo") != null
				&& params.get("mailVersionAuthor") != null
						? this.skillsSheetBusinessController.createSkillsSheet(params, role)
						: new UnprocessableEntityException();
	}

	/**
	 * Get a JsonNode of Skills Sheets matching the specified queries in Identity
	 * and Skills fields
	 *
	 * @param identity a String containing all filters about the identity of a
	 *                 person
	 * @param skills   a String containing all filters about the skills contained in
	 *                 a Skills Sheet
	 * @param role     the current logged user's role
	 * @return the list of all skills sheets given the filters submitted
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/skillsheetSearch/{identity}/{skills}")
	@ResponseBody
	public String getSkillsSheetByIdentityAndSkills(@PathVariable("identity") final String identity,
			@PathVariable("skills") final String skills, @RequestAttribute("role") final UserRole role) {
		return this.skillsSheetBusinessController.getSkillsSheetsByIdentityAndSkills(identity, skills, role);
	}

	/**
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @param name the name of the skills sheet
	 * @return the list of all skills sheets given a name (empty if there is no
	 *         match)
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/skillsheet/{name}")
	@ResponseBody
	public String getSkillsSheetByName(@PathVariable("name") final String sheetName,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillsSheetBusinessController.getSkillsSheets(sheetName, role));
	}

	/**
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return the list of all skills sheets (empty if there is no match)
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/skillsheets")
	@ResponseBody
	public String getSkillsSheets(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillsSheetBusinessController.getAllSkillsSheets(role));
	}

	/**
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link UnprocessableEntityException} if we can't update the resource,
	 *         {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action and {@link CreatedException} if the
	 *         skills sheet is updated
	 * @author Lucas Royackkers
	 */
	@PutMapping(value = "/skillsheet")
	@ResponseBody
	public HttpException updateSkillsSheet(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return params.get("versionNumber") != null && params.get("name") != null
				&& params.get("mailPersonAttachedTo") != null && params.get("mailVersionAuthor") != null
						? this.skillsSheetBusinessController.updateSkillsSheet(params, role)
						: new UnprocessableEntityException();
	}

}
