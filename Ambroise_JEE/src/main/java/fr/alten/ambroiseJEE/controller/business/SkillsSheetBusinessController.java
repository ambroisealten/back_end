package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

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
	 * Method to delegate skills sheet creation
	 *
	 * @param jSkillsSheet JsonNode with all skills sheet parameters
	 * @param role the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the request,
	 *  	{@link ConflictException} if there is a conflict in the database,
	 *  	{@link ForbiddenException} if the current logged user hasn't the rights to perform this action
	 *  	and {@link CreatedException} if the skills sheet is created
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException createSkillsSheet(JsonNode jSkillsSheet, UserRole role) {
		return (UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role)
				? skillsSheetEntityController.createSkillsSheet(jSkillsSheet)
				: new ForbiddenException();
	}

	/**
	 * Get all Skills Sheet
	 * @param role the user's role
	 * @return the list of all skills sheets
	 * @throws {@link ForbiddenException} if the current logged user hasn't the rights to perform this action
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getAllSkillsSheets(UserRole role) {
		if (UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return skillsSheetEntityController.getSkillsSheets();
		}
		throw new ForbiddenException();
	}

	/**
	 * Get a specific version of a skills sheet
	 *
	 * @param name          the searched skills sheet's name
	 * @param versionNumber the searched skills sheet's number
	 * @param role          the current logged user's role
	 * @return a skills sheet given a name and a version number (Optional, might be empty if the resource isn't found or if the user hasn't the rights to performs this action)
	 * @author Lucas Royackkers
	 */
	public Optional<SkillsSheet> getSkillsSheet(String name, long versionNumber, UserRole role) {
		return (UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role)
				? skillsSheetEntityController.getSkillsSheetByNameAndVersionNumber(name, versionNumber)
				: Optional.empty();
	}

	/**
	 * Get all versions of a skills sheet 
	 * @param role the current logged user's role
	 * @param name the searched skills sheet's name
	 * @return the list of all skills sheets given a name (might be empty if there is no match)
	 * @throws {@link ForbiddenException} if the current logged user hasn't the rights to perform this action
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheets(String name, UserRole role) {
		if (UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return skillsSheetEntityController.getSkillsSheetsByName(name);
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
	 *         database, {@link ForbiddenException} if the current logged user hasn't the rights to perform this action
	 *         and {@link CreatedException} if the skills sheet is updated
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException updateSkillsSheet(JsonNode jSkillsSheet, UserRole role) {
		return (UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role)
				? skillsSheetEntityController.updateSkillsSheet(jSkillsSheet)
				: new ForbiddenException();
	}

	/**
	 * Check if a Person already has a Skills sheets with its mail in it
	 * @param mailPerson the mail of the person
	 * @param role the current logged user's role
	 * @return a boolean whether the mail has been used or not
	 * @throws {@link ForbiddenException} if the current logged user hasn't the rights to perform this action
	 * @author Lucas Royackkers
	 */
	public boolean checkIfSkillsWithMailExists(String mailPerson, UserRole role) {
		if (UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role) { 
				skillsSheetEntityController.checkIfSkillsWithMailExists(mailPerson);
		}
		throw new ForbiddenException();
	}

}
