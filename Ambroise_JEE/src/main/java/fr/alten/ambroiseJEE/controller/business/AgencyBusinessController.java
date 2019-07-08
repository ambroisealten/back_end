/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.entityControllers.AgencyEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Agency controller for business rules.
 *
 * @author Andy Chabalier
 *
 */
@Service
public class AgencyBusinessController {

	private final UserRoleLists roles = UserRoleLists.getInstance();

	@Autowired
	private AgencyEntityController agencyEntityController;

	/**
	 * Method to delegate agency creation
	 *
	 * @param jUser JsonNode with all agency parameters (name and geographic
	 *              position)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the agency is created
	 * @author Andy Chabalier
	 */
	public HttpException createAgency(final JsonNode jAgency, final UserRole role) {
		return isAdmin(role) ? this.agencyEntityController.createAgency(jAgency) : new ForbiddenException();
	}

	/**
	 * Method to delegate agency deletion
	 *
	 * @param params the agency name to delete
	 * @param role   the user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link OkException} if the agency is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteAgency(final JsonNode params, final UserRole role) {
		return isAdmin(role) ? this.agencyEntityController.deleteAgency(params) : new ForbiddenException();
	}

	/**
	 * Get the list of all agencies
	 *
	 * @param role the user role
	 * @return the list of all agencies
	 * @author Andy Chabalier
	 */
	public List<Agency> getAgencies(final UserRole role) {
		if (isConnected(role)) {
			return this.agencyEntityController.getAgencies();
		}
		throw new ForbiddenException();
	}

	/**
	 * Fetch an agency
	 *
	 * @param name the agency's name to fetch
	 * @return an optional with the fetched agency or empty if the agency is not
	 *         foud
	 * @author Andy Chabalier
	 */
	public Agency getAgency(final String name) {
		return this.agencyEntityController.getAgency(name);
	}

	public boolean isAdmin(final UserRole role) {
		return this.roles.isAdmin(role);
	}

	public boolean isConnected(final UserRole role) {
		return this.roles.isNot_ConsultantOrDeactivated(role);
	}

	/**
	 * Method to delegate agency update
	 *
	 * @param jAgency JsonNode with all agency parameters (name, place and
	 *                placeType) and the old name to perform the update even if the
	 *                name is changed
	 * @param role    user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the agency is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateAgency(final JsonNode jAgency, final UserRole role) {
		return isAdmin(role) ? this.agencyEntityController.updateAgency(jAgency) : new ForbiddenException();
	}
}
