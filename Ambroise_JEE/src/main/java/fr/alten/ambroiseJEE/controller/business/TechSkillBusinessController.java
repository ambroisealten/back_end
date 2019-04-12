/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.SoftSkill;
import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.model.entityControllers.TechSkillEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * TechSkill controller for business rules.
 * @author Thomas Decamp
 *
 */
@Service
public class TechSkillBusinessController {

	@Autowired
	private TechSkillEntityController techSkillEntityController;
	

	public Optional<TechSkill> getTechSkill(String name) {
		return techSkillEntityController.getTechSkillByName(name);
	}

	public Optional<TechSkill> getTechSkillByNameAndGrade(String name,float grade) {
		return techSkillEntityController.getTechSkillByNameAndGrade(name,grade);
	}

	/**
	 * Method to delegate techSkill creation
	 * @param jUser JsonNode with all techSkill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the techSkill is created
	 * @author Thomas Decamp
	 */
	public HttpException createTechSkill(JsonNode jTechSkill, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? techSkillEntityController.createTechSkill(jTechSkill) : new ForbiddenException();
	}


}
