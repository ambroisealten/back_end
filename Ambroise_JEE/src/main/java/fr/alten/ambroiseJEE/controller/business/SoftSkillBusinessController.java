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
import fr.alten.ambroiseJEE.model.entityControllers.SoftSkillEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.SoftSkillGrade;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * SoftSkill controller for business rules.
 *
 * @author Thomas Decamp
 *
 */
@Service
public class SoftSkillBusinessController {

	private UserRoleLists roles = UserRoleLists.getInstance();
	
	@Autowired
	private SoftSkillEntityController softSkillEntityController;

	/**
	 * Method to delegate softSkill creation
	 *
	 * @param jUser JsonNode with all softSkill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the softSkill is created
	 * @author Thomas Decamp
	 */
	public HttpException createSoftSkill(JsonNode jSoftSkill, UserRole role) {
		return (roles.isAdmin(role))
				? softSkillEntityController.createSoftSkillAndGrade(jSoftSkill)
				: new ForbiddenException();
	}

	/**
	 *
	 * @param params the softSkill name to delete
	 * @param role   the user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link CreatedException} if the softSkill is deleted
	 * @author Thomas Decamp
	 */
	public HttpException deleteSoftSkill(JsonNode jSoftSkill, UserRole role) {
		return (roles.isAdmin(role))
				? softSkillEntityController.deleteSoftSkill(jSoftSkill)
				: new ForbiddenException();
	}

	public List<SoftSkill> getSoftSkill(String name) {
		return softSkillEntityController.getSoftSkillByName(name);
	}

	public Optional<SoftSkill> getSoftSkillByNameAndGrade(String name, SoftSkillGrade grade) {
		return softSkillEntityController.getSoftSkillByNameAndGrade(name, grade);
	}

	/**
	 * @param role the user role
	 * @return the list of all softSkills
	 * @author Thomas Decamp
	 */
	public List<SoftSkill> getSoftSkills(UserRole role) {
		if (roles.isAdmin(role)) {
			return softSkillEntityController.getSoftSkills();
		}
		throw new ForbiddenException();
	}

	/**
	 *
	 * @param jSoftSkill JsonNode with all softSkill parameters and the old name to
	 *                   perform the update even if the name is changed
	 * @param role       user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the softSkill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateSoftSkill(JsonNode jSoftSkill, UserRole role) {
		return (roles.isAdmin(role))
				? softSkillEntityController.updateSoftSkill(jSoftSkill)
				: new ForbiddenException();
	}

}
