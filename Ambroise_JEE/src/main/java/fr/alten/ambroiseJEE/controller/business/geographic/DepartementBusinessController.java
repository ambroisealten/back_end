/**
 *
 */
package fr.alten.ambroiseJEE.controller.business.geographic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Departement;
import fr.alten.ambroiseJEE.model.entityControllers.DepartementEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Departement controller for business rules.
 *
 * @author Andy Chabalier
 *
 */
@Service
public class DepartementBusinessController {

	@Autowired
	private DepartementEntityController departementEntityController;

	/**
	 * Method to delegate departement creation
	 *
	 * @param jUser JsonNode with all departement parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the departement is created
	 * @author Andy Chabalier
	 */
	public HttpException createDepartement(JsonNode jDepartement, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? departementEntityController.createDepartement(jDepartement)
				: new ForbiddenException();
	}

	/**
	 * Method to delegate departement deletion
	 *
	 * @param params the departement name to delete
	 * @param role   the user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link OkException} if the departement is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteDepartement(JsonNode params, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? departementEntityController.deleteDepartement(params.get("name").textValue())
				: new ForbiddenException();
	}

	/**
	 * Method to fetch a departement
	 *
	 * @param name the departement name to fetch
	 * @return an optional with the fetched value or empty if it's not found
	 * @author Andy Chabalier
	 */
	public Departement getDepartement(String name) {
		return departementEntityController.getDepartement(name);
	}

	/**
	 * Method to fetch the list of all departements
	 *
	 * @param role the user role
	 * @return the list of all departements
	 * @author Andy Chabalier
	 */
	public List<Departement> getDepartements(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return departementEntityController.getDepartements();
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate departement update
	 *
	 * @param jDepartement JsonNode with all departement parameters and the old name
	 *                     to perform the update even if the name is changed
	 * @param role         user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the departement is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateDepartement(JsonNode jDepartement, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? departementEntityController.updateDepartement(jDepartement)
				: new ForbiddenException();
	}

}