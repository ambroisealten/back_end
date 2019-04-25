/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.entityControllers.SkillEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Skill controller for business rules.
 *
 * @author Thomas Decamp
 *
 */
@Service
public class SkillBusinessController {

	private UserRoleLists roles = UserRoleLists.getInstance();
	
	@Autowired
	private SkillEntityController skillEntityController;

	/**
	 * Method to delegate skill creation
	 *
	 * @param jUser JsonNode with all skill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skill is created
	 * @author Thomas Decamp
	 */
	public HttpException createSkill(JsonNode jSkill, UserRole role) {
		return (roles.isAdmin(role))
				? skillEntityController.createSkill(jSkill)
				: new ForbiddenException();
	}

	/**
	 *
	 * @param params the skill name to delete
	 * @param role   the user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link CreatedException} if the skill is deleted
	 * @author Thomas Decamp
	 */
	public HttpException deleteSkill(JsonNode jSkill, UserRole role) {
		return (roles.isAdmin(role))
				? skillEntityController.deleteSkill(jSkill)
				: new ForbiddenException();
	}

	public List<Skill> getSkill(String name) {
		return skillEntityController.getSkill(name);
	}

	public Optional<Skill> getSkillByNameAndGrade(String name, float grade) {
		return SkillEntityController.getSkillByNameAndGrade(name, grade);
	}
	
	/**
	 * @param role the user role
	 * @return the list of all skills
	 * @author Thomas Decamp
	 */
	public List<Skill> getSkills(UserRole role) {
		if (roles.isAdmin(role)) {
			return skillEntityController.getSkills();
		}
		throw new ForbiddenException();
	}

	/**
	 *
	 * @param jSkill JsonNode with all skill parameters and the old name to perform
	 *               the update even if the name is changed
	 * @param role   user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the skill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateSkill(JsonNode jSkill, UserRole role) {
		return (roles.isAdmin(role))
				? skillEntityController.updateSkill(jSkill)
				: new ForbiddenException();
	}

}
