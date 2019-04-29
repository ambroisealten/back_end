package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.entityControllers.SkillsSheetEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Skills sheet controller for business rules.
 *
 * @author Lucas Royackkers
 *
 */
@Service
public class SkillsSheetBusinessController {

	@Autowired
	private SkillsSheetEntityController skillsSheetEntityController;

	/**
	 * Get all Skills Sheets given a mail
	 * 
	 * @param mailPerson the mail of the person
	 * @param role the current logged user's role
	 * @return a List of Skills Sheet given a mail
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetByMail(final String mailPerson, final UserRole role) {
		if (UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role) {
			return this.skillsSheetEntityController.getSkillsSheetsByMail(mailPerson);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate skills sheet creation
	 *
	 * @param jSkillsSheet JsonNode with all skills sheet parameters
	 * @param role         the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database, {@link ForbiddenException} if the current logged user
	 *         hasn't the rights to perform this action and {@link CreatedException}
	 *         if the skills sheet is created
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException createSkillsSheet(final JsonNode jSkillsSheet, final UserRole role) {
		return UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				? this.skillsSheetEntityController.createSkillsSheet(jSkillsSheet)
				: new ForbiddenException();
	}

	/**
	 * Get all Skills Sheet
	 *
	 * @param role the user's role
	 * @return the list of all skills sheets
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getAllSkillsSheets(final UserRole role) {
		if (UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return this.skillsSheetEntityController.getSkillsSheets();
		}
		throw new ForbiddenException();
	}

	/**
	 * Get all versions of a skills sheet
	 *
	 * @param role the current logged user's role
	 * @param name the searched skills sheet's name
	 * @return the list of all skills sheets given a name (might be empty if there
	 *         is no match)
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheets(final String name, final UserRole role) {
		if (UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return this.skillsSheetEntityController.getSkillsSheetsByName(name);
		}
		throw new ForbiddenException();
	}

	/**
	 * Get all Skills Sheets given identity (on the Person object) and skills (on
	 * Skill objects) filters
	 *
	 * @param identity the filters about a Person (name, job, etc.)
	 * @param skills   the filters about a Skill (name)
	 * @param role     the current logged user's role
	 * @return a List of SkillsSheet that match the query (can be empty)
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public String getSkillsSheetsByIdentityAndSkills(String identity, String skills,
			UserRole role) {
		if (UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role) {
			return skillsSheetEntityController.getSkillsSheetsByIdentityAndSkills(identity, skills);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate skills sheet update
	 *
	 * @param jSkillsSheet JsonNode with all skills sheet parameters
	 * @param role         the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database, {@link ForbiddenException} if the current logged user
	 *         hasn't the rights to perform this action and {@link CreatedException}
	 *         if the skills sheet is updated
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException updateSkillsSheet(final JsonNode jSkillsSheet, final UserRole role) {
		return UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role
				? this.skillsSheetEntityController.updateSkillsSheet(jSkillsSheet)
				: new ForbiddenException();
	}

	/**
	 * Get a List of Skills Sheet (with different versions) given their common name and mail of the person attached to
	 * 
	 * @param name the name of the skills sheet
	 * @param role the logged user's role
	 * @param mail the mail of the person attached to it
	 * @return a List of Skills Sheet that matches the given parameters (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetVersion(String name, String mailPerson, UserRole role) {
		if(UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role) {
			return this.skillsSheetEntityController.getSkillsSheetVersion(name,mailPerson);
		}
		throw new ForbiddenException();
	}

}
