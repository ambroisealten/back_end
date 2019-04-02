/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business.geographic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.model.entityControllers.RegionEntityController;
import fr.alten.ambroiseJEE.security.Roles;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * Region controller for business rules.
 * @author Andy Chabalier
 *
 */
@Service
public class RegionBusinessController {

	@Autowired
	private RegionEntityController regionEntityController;
	

	public Optional<Region> getRegion(String name) {
		return regionEntityController.getRegion(name);
	}


	/**
	 * Method to delegate region creation
	 * @param jRegion JsonNode with all region parameters
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the region is created
	 * @author Andy Chabalier
	 */
	public HttpException createRegion(JsonNode jRegion, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? regionEntityController.createRegion(jRegion) : new ForbiddenException();
	}


	/**
	 * @param role the user role
	 * @return the list of all regions
	 * @author Andy Chabalier
	 */
	public List<Region> getRegions(int role) {
		if (Roles.ADMINISTRATOR_USER_ROLE.getValue()== role) {
			return regionEntityController.getRegions();
		}
		throw new ForbiddenException();	
	}


	/**
	 * 
	 * @param jRegion JsonNode with all region parameters and the old name to perform the update even if the name is changed
	 * @param role user role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link CreatedException} if the region is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateRegion(JsonNode jRegion, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? regionEntityController.updateRegion(jRegion) : new ForbiddenException();
	}


	/**
	 * 
	 * @param params the region name to delete 
	 * @param role the user role
	 * @return @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ForbiddenException} if the ressource is not found
	 *         and {@link CreatedException} if the region is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteRegion(JsonNode params, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? regionEntityController.deleteRegion(params.get("name").textValue()) : new ForbiddenException();
	}

}
