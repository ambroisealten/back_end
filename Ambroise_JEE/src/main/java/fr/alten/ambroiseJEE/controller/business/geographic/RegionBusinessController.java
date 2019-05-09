/**
 *
 */
package fr.alten.ambroiseJEE.controller.business.geographic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.model.entityControllers.RegionEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Region controller for business rules.
 *
 * @author Andy Chabalier
 *
 */
@Service
public class RegionBusinessController {
	
	private final UserRoleLists roles = UserRoleLists.getInstance();

	@Autowired
	private RegionEntityController regionEntityController;

	/**
	 * Method to delegate region creation
	 *
	 * @param jRegion JsonNode with all region parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the region is created
	 * @author Andy Chabalier
	 */
	public HttpException createRegion(final JsonNode jRegion, final UserRole role) {
		return isAdmin(role)
				? this.regionEntityController.createRegion(jRegion)
				: new ForbiddenException();
	}

	/**
	 * Method to delegate region deletion
	 *
	 * @param params the region name to delete
	 * @param role   the current logged user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link OkException} if the region is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteRegion(final JsonNode params, final UserRole role) {
		return isAdmin(role)
				? this.regionEntityController.deleteRegion(params)
				: new ForbiddenException();
	}

	/**
	 * Fetch a region
	 *
	 * @param name the region's name to fetch
	 * @return an optional with the fetched value or empy if the region is not found
	 * @author Andy Chabalier
	 */
	public Region getRegion(final String name) {
		return this.regionEntityController.getRegion(name);
	}

	/**
	 * Get the list of all regions
	 *
	 * @param role the user role
	 * @return the list of all regions
	 * @author Andy Chabalier
	 */
	public List<Region> getRegions(final UserRole role) {
		if (isAdmin(role)) {
			return this.regionEntityController.getRegions();
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate region update
	 *
	 * @param jRegion JsonNode with all region parameters and the old name to
	 *                perform the update even if the name is changed
	 * @param role    the current logged user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the region is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateRegion(final JsonNode jRegion, final UserRole role) {
		return isAdmin(role)
				? this.regionEntityController.updateRegion(jRegion)
				: new ForbiddenException();
	}
	
	public boolean isAdmin(final UserRole role) {
		return this.roles.isAdmin(role);
	}

}
