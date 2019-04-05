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
 * @author Lucas Royackkers
 *
 */
@Service
public class SkillsSheetBusinessController {

	@Autowired
	private SkillsSheetEntityController skillsSheetEntityController;
	
	public Optional<List<SkillsSheet>> getSkillsSheet(String name){
		return skillsSheetEntityController.getSkillsSheetsByName(name);
	}
	
	/**
	 * Method to delegate skills sheet creation
	 * @param jSkillsSheet JsonNode with all skills sheet parameters
	 * @param role the user's role 
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skills sheet is created
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	public HttpException createSkillsSheet(JsonNode jSkillsSheet, UserRole role) {
		return (UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) ? skillsSheetEntityController.createSkillsSheet(jSkillsSheet) : new ForbiddenException();
	}

	/**
	 * @param role the user's role
	 * @return the list of all skills sheets
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheets(UserRole role) {
		if ((UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role)) {
			return skillsSheetEntityController.getSkillsSheets();
		}
		throw new ForbiddenException();	
	}

}
