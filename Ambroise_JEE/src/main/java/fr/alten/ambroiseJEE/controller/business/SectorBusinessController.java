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
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

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
	 * @param JSector JsonNode with all sector parameters
	 * @param role    the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the sector is created
	 *         * @author Andy Chabalier
	 */
	public HttpException createSector(JsonNode JSector, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? sectorEntityController.createSector(JSector)
				: new ForbiddenException();
	}

	/**
	 * @param JSector JsonNode with sector's name parameter
	 * @param role    the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not
	 *         found and {@link CreatedException} if the sector is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteSector(JsonNode JSector, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? sectorEntityController.deleteSector(JSector)
				: new ForbiddenException();
	}

	/**
	 * @param role the current logged user's role
	 * @return the list of sectors
	 * @author Andy Chabalier
	 */
	public List<Sector> getSectors(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return sectorEntityController.getSectors();
		}
		throw new ForbiddenException();
	}

	/**
	 * @param JSector JsonNode with all sector parameters
	 * @param role    the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not
	 *         found and {@link CreatedException} if the sector is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateSector(JsonNode JSector, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? sectorEntityController.updateSector(JSector)
				: new ForbiddenException();
	}
}
