package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.entityControllers.SkillsSheetEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Skills sheet controller for business rules.
 *
 * @author Lucas Royackkers
 *
 */
@Service
public class SkillsSheetBusinessController {

	private final UserRoleLists roles = UserRoleLists.getInstance();

	@Autowired
	private SkillsSheetEntityController skillsSheetEntityController;

	/**
	 * Method to delegate check if a version of a Skills Sheet exists
	 *
	 * @param name          the name of the Skills Sheet
	 * @param mailPerson    the mail of the person attached to the Skills Sheet
	 * @param versionNumber the version number of the Skills Sheet
	 * @param role          the current logged user's role
	 * @return true if a version of this specific Skills Sheet exists, otherwise
	 *         false
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public boolean checkIfSkillsSheetVersionExists(final String name, final String mailPerson, final long versionNumber,
			final UserRole role) {
		if (isManager(role)) {
			return this.skillsSheetEntityController.checkIfSkillsSheetVersion(name, mailPerson, versionNumber);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate skills sheet creation
	 *
	 * @param jSkillsSheet  JsonNode with all skills sheet parameters
	 * @param role          the current logged user's role
	 * @param versionAuthor the mail of the author of this version of this Skills
	 *                      Sheet
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ResourceNotFoundException} if there is no such
	 *         resource as the one that are given, {@link ConflictException} if
	 *         there is a conflict in the database, {@link ForbiddenException} if
	 *         the current logged user hasn't the rights to perform this action and
	 *         {@link CreatedException} if the skills sheet is created
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException createSkillsSheet(final JsonNode jSkillsSheet, final UserRole role, final String mailAuthor) {
		return isManager(role) ? this.skillsSheetEntityController.createSkillsSheet(jSkillsSheet, mailAuthor)
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
	public List<JsonNode> getAllSkillsSheets(final UserRole role) {
		if (isManager(role)) {
			return this.skillsSheetEntityController.getSkillsSheets();
		}
		throw new ForbiddenException();
	}

	/**
	 * Get all Skills Sheets given a mail
	 *
	 * @param mailPerson the mail of the person
	 * @param role       the current logged user's role
	 * @return a List of Skills Sheet given a mail
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetByMail(final String mailPerson, final UserRole role) {
		if (isManager(role)) {
			return this.skillsSheetEntityController.getSkillsSheetsByMail(mailPerson);
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
		if (isManager(role)) {
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
	public List<JsonNode> getSkillsSheetsByIdentityAndSkills(final String identity, final String skills,
			final String columnSorting, final UserRole role) {
		if (isManager(role)) {
			return this.skillsSheetEntityController.getSkillsSheetsByIdentityAndSkills(identity, skills, columnSorting);
		}
		throw new ForbiddenException();
	}

	/**
	 * Get a List of Skills Sheet (with different versions) given their common name
	 * and mail of the person attached to
	 *
	 * @param name the name of the skills sheet
	 * @param role the logged user's role
	 * @param mail the mail of the person attached to it
	 * @return a List of Skills Sheet that matches the given parameters (can be
	 *         empty)
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetVersion(final String name, final String mailPerson, final UserRole role) {
		if (isManager(role)) {
			return this.skillsSheetEntityController.getSkillsSheetVersion(name, mailPerson);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to test if the user is manager
	 *
	 * @param role {@link UserRole} the current logged user's role
	 * @return true if it's manager or manager admin, otherwise false
	 * @author Andy Chabalier
	 */
	public boolean isManager(final UserRole role) {
		return this.roles.isManager(role);
	}

	/**
	 * Method to delegate skills sheet update
	 *
	 * @param jSkillsSheet  JsonNode with all skills sheet parameters
	 * @param role          the current logged user's role
	 * @param versionAuthor the mail of the author of this version of this Skills
	 *                      Sheet
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database, {@link ForbiddenException} if the current logged user
	 *         hasn't the rights to perform this action,
	 *         {@link ResourceNotFoundException} if there is no such resource as the
	 *         one that are given, and {@link CreatedException}if the skills sheet
	 *         is updated
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException updateSkillsSheet(final JsonNode jSkillsSheet, final UserRole role,
			final String versionAuthor) {
		return isManager(role) ? this.skillsSheetEntityController.updateSkillsSheet(jSkillsSheet, versionAuthor)
				: new ForbiddenException();
	}
	
	/**
	 * Method to delegate get of a specific Skills Sheet
	 * 
	 * @param name the name of the Skills Sheet
	 * @param personMail the mail of the person attached to the Skills Sheet
	 * @param versionNumber the version number of the Skills Sheet
	 * @param role the current logged user's role
	 * @return a Skills Sheet if there is a match
	 * @throws a {@link ForbiddenException} if the user hasn't the right to perform this action
	 * @author Lucas Royackkers
	 */
	public SkillsSheet getSpecificSkillsSheet(final String name, final String personMail, final long versionNumber, final UserRole role) {
		if(this.isManager(role)) {
			return this.skillsSheetEntityController.getSkillsSheet(name, personMail, versionNumber);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate CV update in a Skills Sheet
	 *
	 * @param cv                   the CV as a File
	 * @param name                 the name of the Skills Sheet
	 * @param mailPersonAttachedTo the mail of the person attached to this Skills
	 *                             Sheet
	 * @param versionNumber        the version number of this Skills Sheet
	 * @param role                 the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database, {@link ResourceNotFoundException} if there is no such
	 *         resource as the one that are given, {@link ForbiddenException} if the
	 *         current logged user hasn't the rights to perform this action and
	 *         {@link OkException} if the CV in this skills sheet is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateSkillsSheetCV(final File cv, final String name, final String mailPersonAttachedTo,
			final long versionNumber, final UserRole role) {
		return isManager(role)
				? this.skillsSheetEntityController.updateSkillsSheetCV(cv, name, mailPersonAttachedTo, versionNumber)
				: new ForbiddenException();

	}

}
