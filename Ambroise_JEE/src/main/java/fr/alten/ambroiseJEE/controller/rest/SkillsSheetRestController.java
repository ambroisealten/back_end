package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.SkillsSheetBusinessController;
import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
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

	@Autowired
	private FileRestController fileRestController;

	private final Gson gson;

	public SkillsSheetRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Method to create a Skills Sheet
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link UnprocessableEntityException} if we can't create the resource,
	 *         {@link ResourceNotFoundException} if there is no such resource (such as User, PersonRole, etc.) as the one that are given,
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
		((ObjectNode) params).put("mailVersionAuthor", mail);
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
	 * @throws IOException
	 */
	@GetMapping(value = "/skillsheetSearch/{identity}/{skills}")
	@ResponseBody
	public String getSkillsSheetByIdentityAndSkills(@PathVariable("identity") final String identity,
			@PathVariable("skills") final String skills, @RequestAttribute("role") final UserRole role)
			throws IOException {
		return "{\"results\" : " + this.skillsSheetBusinessController
				.getSkillsSheetsByIdentityAndSkills(identity, skills, role).toString() + "}";
	}

	/**
	 * Get all Skills Sheets given a person (with its mail)
	 *
	 * @param mailPerson the mail of the person
	 * @param mail       the current logged user's mail
	 * @param role       the current logged user's role
	 * @return a String containing a JsonNode with a List of Skills Sheets given the
	 *         person attached to
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/skillsheetMail/{mail}")
	@ResponseBody
	public String getSkillsSheetByMail(@PathVariable("mail") final String mailPerson,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.skillsSheetBusinessController.getSkillsSheetByMail(mailPerson, role));
	}

	/**
	 * Method to get all Skills Sheets given a name
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
	 * Get all Skills Sheets
	 *
	 * @param mail the current logged user's mail
	 * @param role the current logged user's role
	 * @return the list of all skills sheets (can be empty)
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/skillsheets")
	@ResponseBody
	public String getSkillsSheets(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return "{\"results\" : " + this.skillsSheetBusinessController.getAllSkillsSheets(role).toString() + "}";
	}

	/**
	 * Get all versions of a Skills Sheet given their common name and person
	 * attached to
	 *
	 * @param mailPerson the mail of the person attached to the different versions
	 * @param role       the current logged user's role
	 * @param name       the name of the Skills Sheet
	 * @param mail       the current logged user's mail
	 * @return a String containing a JsonNode that contains a List of Skills Sheet
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/skillsheetVersion/{name}/{mail}")
	@ResponseBody
	public String getSkillsSheetVersions(@PathVariable("mail") final String mailPerson,
			@RequestAttribute("role") final UserRole role, @PathVariable("name") final String name,
			@RequestAttribute("mail") final String mail) {
		return this.gson.toJson(this.skillsSheetBusinessController.getSkillsSheetVersion(name, mailPerson, role));
	}
	
	
	/**
	 * Checks if a version of a specific Skills Sheet exists
	 * 
	 * @param mailPerson the mail of the person attached to this Skills Sheet
	 * @param role the current logged user's role
	 * @param name the name of the Skills Sheet
	 * @param mail the current logged user's mail
	 * @param versionNumber the version Number of this Skills Sheet
	 * @return true if the specific version of this Skills Sheet exists, otherwise false
	 * @author Lucas Royackkers
	 */
	@GetMapping(value = "/skillsheetVersionExists/{name}/{mail}/{versionNumber}")
	@ResponseBody
	public boolean checkIfSkillsSheetVersionExists(@PathVariable("mail") final String mailPerson,
			@RequestAttribute("role") final UserRole role, @PathVariable("name") final String name,
			@RequestAttribute("mail") final String mail, @PathVariable("versionNumber") String versionNumber) {
		return this.skillsSheetBusinessController.checkIfSkillsSheetVersionExists(name, mailPerson, Long.parseLong(versionNumber), role);
	}

	/**
	 * Method to update a Skills Sheet
	 *
	 * @param params JsonNode containing post parameters from http request
	 * @param mail   the current logged user's mail
	 * @param role   the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link UnprocessableEntityException} if we can't update the resource,
	 *         {@link ResourceNotFoundException} if there is no such resource as the one that are given,
	 *         {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action and {@link CreatedException} if the
	 *         skills sheet is updated
	 * @author Lucas Royackkers
	 */
	@PutMapping(value = "/skillsheet")
	@ResponseBody
	public HttpException updateSkillsSheet(@RequestBody final JsonNode params,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		((ObjectNode) params).put("mailVersionAuthor", mail);
		return params.get("versionNumber") != null && params.get("name") != null
				&& params.get("mailPersonAttachedTo") != null && params.get("mailVersionAuthor") != null
						? this.skillsSheetBusinessController.updateSkillsSheet(params, role)
						: new UnprocessableEntityException();
	}

	/**
	 * Method to update a CV on a existent {@link SkillsSheet}
	 * 
	 * @param file the CV as a File 
	 * @param name the name of the Skills Sheet
	 * @param mailPersonAttachedTo the mail of the person attached to this Skills Sheet
	 * @param versionNumber the versionNumber of this Skills Sheet
	 * @param mail the mail of the current logged user's 
	 * @param role the role of the current logged user's
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link UnprocessableEntityException} if we can't update the resource,
	 *         {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action and {@link OkException} if the
	 *         skills sheet is updated with the CV
	 * @author Lucas Royackkers
	 */
	@PutMapping(value = "/skillsheet/CV")
	@ResponseBody
	public HttpException updateSkillsSheetCV(@RequestParam("file") final MultipartFile file,
			@RequestParam("name") final String name,
			@RequestParam("mailPersonAttachedTo") final String mailPersonAttachedTo,
			@RequestParam("versionNumber") final long versionNumber, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		final File cv = this.fileRestController.uploadFile(file,
				"/skillsheet/cv/" + mailPersonAttachedTo + "/" + name + "/" + versionNumber + "/", mail, role);
		return this.skillsSheetBusinessController.updateSkillsSheetCV(cv, name, mailPersonAttachedTo, versionNumber,
				role);
	}

}
