/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;
import java.util.Optional;

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
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

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
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the forum is created
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException createForum(JsonNode jForum, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role) ? forumEntityController.createForum(jForum) : new ForbiddenException();
	}

	/**
	 * ask for fetch a forum by is date
	 * 
	 * @param date the date of the forum to fetch
	 * @return an Optional with the corresponding forum or not
	 * @author MAQUINGHEN MAXIME
	 */
	public Optional<Forum> getForumByDate(String id, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role) ? forumEntityController.getForumById(id) : Optional.empty();
	}

	/**
	 * Method to delegate fetching of all users
	 * 
	 * @param role user role
	 * @return the list of all Forum
	 * @author MAQUINGHEN MAXIME
	 */
	public List<Forum> getForums(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role) {
			return forumEntityController.getForums();
		}
		throw new ForbiddenException();
	}

	/**
	 * Update un forum
	 * 
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not
	 *         found and {@link CreatedException} if the forum is updated
	 * @author MAQUINGHEN MAXIME
	 * @param role
	 * @param params
	 */
	public HttpException updateForum(JsonNode params, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? forumEntityController.updateForum(params)
				: new ForbiddenException();
	}

	/**
	 * Delete a forum
	 * 
	 * @return @see {@link HttpException} corresponding to the statut of the request
	 *         ({@link ForbiddenException} if the ressource is not found and
	 *         {@link CreatedException} if the forum is desactivated
	 * @author MAQUINGHEN MAXIME
	 * @param role
	 * @param params
	 */
	public HttpException deleteForum(JsonNode params, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role) ? forumEntityController.deleteForum(params.get("id").textValue())
						: new ForbiddenException();
	}

	public Optional<Forum> getForum(String id, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role) ? forumEntityController.getForumById(id) : Optional.empty();
	}

}
