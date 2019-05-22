/**
 *
 */
package fr.alten.ambroiseJEE.controller.business.geographic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.entityControllers.PostalCodeEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * City controller for business rules.
 *
 * @author Andy Chabalier
 *
 */
@Service
public class PostalCodeBusinessController {

	private final UserRoleLists roles = UserRoleLists.getInstance();

	@Autowired
	private PostalCodeEntityController postalCodeEntityController;

	/**
	 * Method to delegate postalCode creation
	 *
	 * @param jUser JsonNode with all city parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the postalCode is created
	 * @author Andy Chabalier
	 */
	public HttpException createPostalCode(final JsonNode jPostalCode, final UserRole role) {
		return isAdmin(role) ? this.postalCodeEntityController.createPostalCode(jPostalCode) : new ForbiddenException();
	}

	/**
	 * Method to delegate postal Code deletion
	 *
	 * @param params the postalCode name to delete
	 * @param role   the current logged user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link OkException} if the postalCode is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deletePostalCode(final JsonNode params, final UserRole role) {
		return isAdmin(role) ? this.postalCodeEntityController.deletePostalCode(params) : new ForbiddenException();
	}

	/**
	 * Fetch a Postal code
	 *
	 * @param name the postal code name to fetch
	 * @return an optional with the requested postal code or empty if not found
	 * @author Andy Chabalier
	 */
	public PostalCode getPostalCode(final String name) {
		return this.postalCodeEntityController.getPostalCode(name);
	}

	/**
	 * Get the list of all postal codes
	 *
	 * @param role the current logged user role
	 * @return the list of allppostalCodes
	 * @author Andy Chabalier
	 */
	public List<PostalCode> getPostalCodes(final UserRole role) {
		if (isAdmin(role)) {
			return this.postalCodeEntityController.getPostalCodes();
		}
		throw new ForbiddenException();
	}

	public boolean isAdmin(final UserRole role) {
		return this.roles.isAdmin(role);
	}

	/**
	 * Method to delegate postal code update
	 *
	 * @param jPostalCode JsonNode with all postalCode parameters and the old name
	 *                    to perform the update even if the name is changed
	 * @param role        user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the postalCode is updated
	 * @author Andy Chabalier
	 */
	public HttpException updatePostalCode(final JsonNode jPostalCode, final UserRole role) {
		return isAdmin(role) ? this.postalCodeEntityController.updatePostalCode(jPostalCode) : new ForbiddenException();
	}
}
