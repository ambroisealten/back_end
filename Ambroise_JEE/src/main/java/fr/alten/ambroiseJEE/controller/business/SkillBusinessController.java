/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.ArrayList;
import java.util.List;

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

	private final UserRoleLists roles = UserRoleLists.getInstance();

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
	public HttpException createSkill(final JsonNode jSkill, final UserRole role) {
		return isAdmin(role) ? this.skillEntityController.createSkill(jSkill) : new ForbiddenException();
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
	public HttpException deleteSkill(final JsonNode jSkill, final UserRole role) {
		return isAdmin(role) ? this.skillEntityController.deleteSkill(jSkill) : new ForbiddenException();
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

	public Skill getSkill(final JsonNode jSkill, final UserRole role) {
		if (isAdmin(role)) {
			return this.skillEntityController.getSkill(jSkill.get("name").textValue());
		}
		throw new ForbiddenException();
	}

	/**
	 * @param role the user role
	 * @return the list of all skills
	 * @author Thomas Decamp
	 */
	public List<Skill> getSkills(final UserRole role) {
		if (isAdmin(role) || UserRole.MANAGER.equals(role)) {
			return this.skillEntityController.getSkills();
		}
		throw new ForbiddenException();
	}

	/**
	 * fetch the soft skills
	 *
	 * @param role current logged user role
	 * @return the list of all soft skills
	 * @author Andy Chabalier
	 */
	public List<Skill> getSoftSkills(final UserRole role) {
		if (isAdmin(role) || UserRole.MANAGER.equals(role)) {
			return this.skillEntityController.getSoftSkills();
		}
		throw new ForbiddenException();
	}

	/**
	 * Fetch all the tech skills
	 *
	 * @param role current logged user's role
	 * @return the list of all tech skills
	 * @author Lucas Royackkers
	 */
	public List<Skill> getTechSkills(final UserRole role) {
		if (isAdmin(role) || UserRole.MANAGER.equals(role)) {
			return this.skillEntityController.getTechSkills();
		}
		throw new ForbiddenException();
	}

	public boolean isAdmin(final UserRole role) {
		return role.equals(UserRole.MANAGER_ADMIN) || role.equals(UserRole.CDR_ADMIN);
	}

	/**
	 *
	 * @param jSkill JsonNode with all skill parameters and the old name to perform
	 *               the update even if the name is changed
	 * @param role   current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the skill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateSkill(final JsonNode jSkill, final UserRole role) {
		return isAdmin(role) ? this.skillEntityController.updateSkill(jSkill) : new ForbiddenException();
	}

	/**
	 * update the order of each soft skills
	 * 
	 * @param jSoftSkillsList the list of softs skills
	 * @param role            current logged user's role
	 * @return a list of Http Exception
	 * @author Andy Chabalier
	 */
	public ArrayList<HttpException> updateSoftSkillsOrder(JsonNode jSoftSkillsList, UserRole role) {
		if(isAdmin(role)) {
			return this.skillEntityController.updateSoftSkillsOrder(jSoftSkillsList.get("softSkillsList"));
		}
		ArrayList<HttpException> result = new ArrayList<HttpException>();
		result.add(new ForbiddenException());
		return result;
	}

	/**
	 * Method to delegate the checking of a Soft Skill
	 * 
	 * @param name the name of the Soft Skill
	 * @param role the current logged user's role
	 * @return true if the specific Soft Skill exists, otherwise false
	 * @author Lucas Royackkers
	 */
	public boolean checkIfSoftSkillExists(String name, UserRole role) {
		if(isManager(role)) {
			return this.skillEntityController.checkIfSoftSkillsExists(name);
		}
		throw new ForbiddenException();
	}
}
