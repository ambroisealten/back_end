package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.entityControllers.DiplomaEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Diploma controller for business rules
 *
 * @author Lucas Royackkers
 *
 */
@Service
public class DiplomaBusinessController {

	@Autowired
	private DiplomaEntityController diplomaEntityController;

	private final UserRoleLists roles = UserRoleLists.getInstance();

	/**
	 * Method to delegate diploma creation
	 *
	 * @param params the JsonNode containing all diploma parameters (name,
	 *               yearOfResult)
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the diploma is created
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action, ParseException
	 * @author Lucas Royackkers
	 * @author Thomas Decamp
	 */
	public HttpException createDiploma(final JsonNode params, final UserRole role) {
		return isManagerOrCdrOrAdmin(role) ? this.diplomaEntityController.createDiploma(params)
				: new ForbiddenException();
	}

	/**
	 * Method to delegate diploma deletion
	 *
	 * @param params the JsonNode containing all diploma parameters (name,
	 *               yearOfResult)
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ResourceNotFoundException} if the resource hasn't
	 *         been found in the database and {@link OkException} if the diploma is
	 *         deactivated
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 * @author Thomas Decamp
	 */
	public HttpException deleteDiploma(final JsonNode params, final UserRole role) {
		return isAdmin(role) ? this.diplomaEntityController.deleteDiploma(params) : new ForbiddenException();
	}

	/**
	 * Method to get all diplomas
	 *
	 * @param role the current logged user's role
	 * @return a List of Diploma objects (can be empty)
	 *
	 * @author Lucas Royackkers
	 * @author Thomas Decamp
	 */
	public List<Diploma> getDiplomas(final UserRole role) {
		if (isManagerOrCdrOrAdmin(role)) {
			return this.diplomaEntityController.getDiplomas();
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to get a List of diplomas given their name
	 *
	 * @param name the name of the diploma
	 * @param role the current logged user's role
	 * @return a List of Diploma (empty or not)
	 * @author Lucas Royackkers
	 */
	public List<Diploma> getDiplomasByName(final String name, final UserRole role) {
		return this.diplomaEntityController.getDiplomaByName(name);

	}

	public boolean isAdmin(final UserRole role) {
		return this.roles.isAdmin(role);
	}

	public boolean isManagerOrCdrOrAdmin(final UserRole role) {
		return this.roles.isManagerOrCdrOrAdmin(role);
	}

	/**
	 * Method to delegate diploma update
	 *
	 * @param params the JsonNode containing all diploma parameters
	 *               (name,oldName,yearOfResult,oldYearOfResult)
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ResourceNotFoundException} if the resource hasn't
	 *         been found in the database and {@link OkException} if the diploma is
	 *         updated
	 * @throws ParseException, {@link ForbiddenException} if the current logged user
	 *                         hasn't the rights to perform this action
	 * @author Lucas Royackkers
	 * @author Thomas Decamp
	 */
	public HttpException updateDiploma(final JsonNode params, final UserRole role) {
		return isAdmin(role) ? this.diplomaEntityController.updateDiploma(params) : new ForbiddenException();
	}

}
