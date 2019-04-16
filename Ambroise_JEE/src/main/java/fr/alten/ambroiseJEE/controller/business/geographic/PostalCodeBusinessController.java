/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business.geographic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.entityControllers.PostalCodeEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * City controller for business rules.
 * @author Andy Chabalier
 *
 */
@Service
public class PostalCodeBusinessController {

	@Autowired
	private PostalCodeEntityController postalCodeEntityController;
	

	public Optional<PostalCode> getPostalCode(String name) {
		return postalCodeEntityController.getPostalCode(name);
	}


	/**
	 * Method to delegate postalCode creation
	 * @param jUser JsonNode with all city parameters
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the postalCode is created
	 * @author Andy Chabalier
	 */
	public HttpException createPostalCode(JsonNode jPostalCode, UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role ? postalCodeEntityController.createPostalCode(jPostalCode) : new ForbiddenException();
	}


	/**
	 * @param role the user role
	 * @return the list of all ppostalCodes
	 * @author Andy Chabalier
	 */
	public List<PostalCode> getPostalCodes(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return postalCodeEntityController.getPostalCodes();
		}
		throw new ForbiddenException();	
	}


	/**
	 * 
	 * @param jPostalCode JsonNode with all postalCode parameters and the old name to perform the update even if the name is changed
	 * @param role user role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link CreatedException} if the postalCode is updated
	 * @author Andy Chabalier
	 */
	public HttpException updatePostalCode(JsonNode jPostalCode, UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role ? postalCodeEntityController.updatePostalCode(jPostalCode) : new ForbiddenException();
	}


	/**
	 * 
	 * @param params the postalCode name to delete 
	 * @param role the user role
	 * @return @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ForbiddenException} if the ressource is not found
	 *         and {@link CreatedException} if the postalCode is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deletePostalCode(JsonNode params, UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role ? postalCodeEntityController.deletePostalCode(params.get("name").textValue()) : new ForbiddenException();
	}

}


