/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.SkillForum;
import fr.alten.ambroiseJEE.model.entityControllers.SkillForumEntityController;
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
public class SkillForumBusinessController {

	private final UserRoleLists roles = UserRoleLists.getInstance();

	@Autowired
	private SkillForumEntityController skillForumEntityController;

	/**
	 * Method to delegate skill creation
	 *
	 * @param jUser JsonNode with all skill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skill is created
	 * @author Thomas Decamp
	 */
	public HttpException createSkillForum(final JsonNode jSkillForum, final UserRole role) {
		return isAdmin(role) ? this.skillForumEntityController.createSkillForum(jSkillForum) : new ForbiddenException();
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
	public HttpException deleteSkillForum(final JsonNode params, final UserRole role) {
		return isAdmin(role) ? this.skillForumEntityController.deleteSkillForum(params) : new ForbiddenException();
	}

	public SkillForum getSkillForum(final JsonNode params, final UserRole role) {
		if (isAdmin(role)) {
			return this.skillForumEntityController.getSkillForum(params);
		}
		throw new ForbiddenException();
	}

	/**
	 * @param role the user role
	 * @return the list of all skills
	 * @author Thomas Decamp
	 */
	public List<SkillForum> getSkillsForum(final UserRole role) {
		if (isAdmin(role)) {
			return this.skillForumEntityController.getSkillsForum();
		}
		throw new ForbiddenException();
	}

	/**
	 * @param role role to check
	 * @return true if the role is admin
	 * @author Andy Chabalier
	 */
	public boolean isAdmin(final UserRole role) {
		return this.roles.isAdmin(role);
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
	public HttpException updateSkillForum(final JsonNode jSkillForum, final UserRole role) {
		return isAdmin(role) ? this.skillForumEntityController.updateSkillForum(jSkillForum) : new ForbiddenException();
	}
}
