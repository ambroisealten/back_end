package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Person;
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
	 * Check if a Person already has a Skills sheets with its mail in it
	 *
	 * @param mailPerson the mail of the person
	 * @param role       the current logged user's role
	 * @return a boolean whether the mail has been used or not
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public boolean checkIfSkillsWithMailExists(final String mailPerson, final UserRole role) {
		if (UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role) {
			return this.skillsSheetEntityController.checkIfSkillsWithMailExists(mailPerson);
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
	 * Get a specific version of a skills sheet
	 *
	 * @param name          the searched skills sheet's name
	 * @param versionNumber the searched skills sheet's number
	 * @param role          the current logged user's role
	 * @return a skills sheet given a name and a version number (Optional, might be
	 *         empty if the resource isn't found or if the user hasn't the rights to
	 *         performs this action)
	 * @author Lucas Royackkers
	 */
	public SkillsSheet getSkillsSheet(final String name, final long versionNumber, final UserRole role) {
		if (UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return this.skillsSheetEntityController.getSkillsSheetByNameAndVersionNumber(name, versionNumber);
		} else {
			throw new ForbiddenException();
		}
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
	public List<SkillsSheet> getSkillsSheetsByIdentityAndSkills(final String identity, final String skills,
			final UserRole role) {
		if (UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role) {
			return this.skillsSheetEntityController.getSkillsSheetsByIdentityAndSkills(identity, skills);
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
	
	public Map<JsonNode, SkillsSheet> getSkillsSheetsByIdentityAndSkills(String identity, String skills, UserRole role) {
		if(UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role) {
			return skillsSheetEntityController.getSkillsSheetsByIdentityAndSkills(identity,skills);
		}
		throw new ForbiddenException();
	}

}
