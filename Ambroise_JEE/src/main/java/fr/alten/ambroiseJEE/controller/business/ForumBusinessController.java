/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Forum;
import fr.alten.ambroiseJEE.model.entityControllers.ForumEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Forum Controller for Business rules
 *
 * @author MAQUINGHEN MAXIME
 */
@Service
public class ForumBusinessController {

	@Autowired
	private ForumEntityController forumEntityController;

	/**
	 * Method to delegate forum creation
	 *
	 * @param jForum JsonNode with all user parameters (name, date, place)
	 * @param role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the forum is created
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException createForum(final JsonNode jForum, final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role ? this.forumEntityController.createForum(jForum) : new ForbiddenException();
	}

	/**
	 * Delete a forum
	 *
	 * @param params the forum name, date, place
	 * @param role   the users role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link CreatedException} if the forum is deactivated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException deleteForum(final JsonNode params, final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role ? this.forumEntityController.deleteForum(params) : new ForbiddenException();
	}

	/**
	 * Get a forum
	 *
	 * @param name  the forum name
	 * @param date  the forum date
	 * @param place the forum place
	 * @param role  the user role
	 * @return An Optional with forum data or {@link ForbiddenException} for right
	 *         problem
	 * @author MAQUINGHEN MAXIME
	 */
	public Forum getForum(final String name, final String date, final String place, final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role) {
			return this.forumEntityController.getForum(name, date, place);
		}
		throw new ForbiddenException();

	}

	/**
	 * Method to delegate fetching of all forums
	 *
	 * @param role user role
	 * @return the list of all Forums
	 * @author MAQUINGHEN MAXIME
	 */
	public List<Forum> getForums(final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role) {
			return this.forumEntityController.getForums();
		}
		throw new ForbiddenException();
	}

	/**
	 *
	 * @param params the forum name, date, place
	 * @param role   the user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the forum is updated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateForum(final JsonNode params, final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role
				? this.forumEntityController.updateForum(params)
				: new ForbiddenException();
	}

}
