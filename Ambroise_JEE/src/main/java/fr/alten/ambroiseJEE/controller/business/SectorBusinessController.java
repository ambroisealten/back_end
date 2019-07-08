/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Sector;
import fr.alten.ambroiseJEE.model.entityControllers.SectorEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Sector controller for business rules.
 *
 * @author Andy Chabalier
 *
 */
@Service
public class SectorBusinessController {

	@Autowired
	private SectorEntityController sectorEntityController;

	/**
	 * Method to delegate sector creation
	 *
	 * @param JSector JsonNode with all sector parameters
	 * @param role    the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the sector is created
	 *         * @author Andy Chabalier
	 */
	public HttpException createSector(final JsonNode JSector, final UserRole role) {
		return isAdmin(role) ? this.sectorEntityController.createSector(JSector) : new ForbiddenException();
	}

	/**
	 * Method to delegate sector deletion
	 *
	 * @param JSector JsonNode with sector's name parameter
	 * @param role    the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the sector is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteSector(final JsonNode JSector, final UserRole role) {
		return isAdmin(role) ? this.sectorEntityController.deleteSector(JSector) : new ForbiddenException();
	}

	/**
	 * Get the list of all sectors
	 *
	 * @param role the current logged user's role
	 * @return the list of sectors
	 * @author Andy Chabalier
	 */
	public List<Sector> getSectors(final UserRole role) {
		if (isAdmin(role)) {
			return this.sectorEntityController.getSectors();
		}
		throw new ForbiddenException();
	}

	public boolean isAdmin(final UserRole role) {
		return UserRoleLists.getInstance().isAdmin(role);
	}

	/**
	 * Method to delegate sector update
	 *
	 * @param JSector JsonNode with all sector parameters
	 * @param role    the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the sector is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateSector(final JsonNode JSector, final UserRole role) {
		return isAdmin(role) ? this.sectorEntityController.updateSector(JSector) : new ForbiddenException();
	}
}
