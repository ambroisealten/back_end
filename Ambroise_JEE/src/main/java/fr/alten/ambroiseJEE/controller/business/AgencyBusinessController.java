/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.entityControllers.AgencyEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * Agency controller for business rules.
 * @author Andy Chabalier
 *
 */
@Service
public class AgencyBusinessController {

	@Autowired
	private AgencyEntityController agencyEntityController;
	

	public Optional<Agency> getAgency(String name) {
		return agencyEntityController.getAgency(name);
	}


	/**
	 * Method to delegate agency creation
	 * @param jUser JsonNode with all agency parameters (name and geographic position)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the user is created
	 * @author Andy Chabalier
	 */
	public HttpException createAgency(JsonNode jAgency, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? agencyEntityController.createAgency(jAgency) : new ForbiddenException();
	}


	/**
	 * @param role the user role
	 * @return the list of all agencies
	 * @author Andy Chabalier
	 */
	public List<Agency> getAgencies(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return agencyEntityController.getAgencies();
		}
		throw new ForbiddenException();	
	}


	/**
	 * 
	 * @param jAgency JsonNode with all user parameters (name, place) and the old name to perform the update even if the name is changed
	 * @param role user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not found
	 *         and {@link CreatedException} if the agency is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateAgency(JsonNode jAgency, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? agencyEntityController.updateAgency(jAgency) : new ForbiddenException();
	}


	/**
	 * 
	 * @param params the agency name to delete 
	 * @param role the user role
	 * @return @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the resource is not found
	 *         and {@link CreatedException} if the user is updated
	 * @author Andy Chabalier
	 */
	public HttpException deleteAgency(JsonNode params, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? agencyEntityController.deleteAgency(params.get("name").textValue()) : new ForbiddenException();
	}

}
