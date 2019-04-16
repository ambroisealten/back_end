/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.model.entityControllers.TechSkillEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.TechSkillGrade;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * TechSkill controller for business rules.
 *
 * @author Thomas Decamp
 *
 */
@Service
public class TechSkillBusinessController {

	@Autowired
	private TechSkillEntityController techSkillEntityController;

	/**
	 * Method to delegate techSkill creation
	 *
	 * @param jUser JsonNode with all techSkill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the techSkill is created
	 * @author Thomas Decamp
	 */
	public HttpException createTechSkill(JsonNode jTechSkill, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? techSkillEntityController.createTechSkillAndGrade(jTechSkill)
				: new ForbiddenException();
	}

	/**
	 *
	 * @param params the techSkill name to delete
	 * @param role   the user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link CreatedException} if the techSkill is deleted
	 * @author Thomas Decamp
	 */
	public HttpException deleteTechSkill(JsonNode jTechSkill, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? techSkillEntityController.deleteTechSkill(jTechSkill)
				: new ForbiddenException();
	}

	public List<TechSkill> getTechSkill(String name) {
		return techSkillEntityController.getTechSkillByName(name);
	}

	public Optional<TechSkill> getTechSkillByNameAndGrade(String name, TechSkillGrade grade) {
		return techSkillEntityController.getTechSkillByNameAndGrade(name, grade);
	}

	/**
	 * @param role the user role
	 * @return the list of all techSkills
	 * @author Thomas Decamp
	 */
	public List<TechSkill> getTechSkills(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return techSkillEntityController.getTechSkills();
		}
		throw new ForbiddenException();
	}

	/**
	 *
	 * @param jTechSkill JsonNode with all techSkill parameters and the old name to
	 *                   perform the update even if the name is changed
	 * @param role       user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the techSkill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateTechSkill(JsonNode jTechSkill, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? techSkillEntityController.updateTechSkill(jTechSkill)
				: new ForbiddenException();
	}

}
