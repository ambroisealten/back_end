/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.entityControllers.UserEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * User controller for business rules.
 *
 * @author Andy Chabalier
 *
 */
@Service
public class UserBusinessController {

	@Autowired
	private UserEntityController userEntityController;

	/**
	 * Method to determine if the provided credential are valid
	 *
	 * @param mail the user's mail to check
	 * @param pswd the user's password to check
	 * @return an optional with the fetched user or empty if user is not found
	 * @author Andy Chabalier
	 */
	public Optional<String> checkIfCredentialIsValid(String mail, String pswd) {
		Optional<User> optionalUser = userEntityController.getUserByCredentials(mail, pswd);
		return optionalUser.isPresent()
				? Optional.of(optionalUser.get().getMail() + "|" + optionalUser.get().getRole().name())
				: Optional.empty();
	}

	/**
	 * Method to delegate user creation
	 *
	 * @param jUser JsonNode with all user parameters (forname, mail, name, password
	 *              and role)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the user is created
	 * @author Andy Chabalier, Maxime Maquinghen
	 */
	public HttpException createUser(JsonNode jUser, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? userEntityController.createUser(jUser)
				: new ForbiddenException();
	}

	/**
	 *
	 * @param params the user mail to delete
	 * @param role   the user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link CreatedException} if the user is updated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException deleteUser(JsonNode params, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? userEntityController.deleteUser(params.get("mail").textValue())
				: new ForbiddenException();
	}

	/**
	 * ask for fetch an user by is mail
	 *
	 * @param mail the user mail to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 */
	public Optional<User> getUserByMail(String mail, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? userEntityController.getUserByMail(mail)
				: Optional.empty();
	}

	/**
	 *
	 * @param role user role
	 * @return the list of all User
	 * @author MAQUINGHEN MAXIME
	 */
	public List<User> getUsers(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return userEntityController.getUsers();
		}
		throw new ForbiddenException();
	}

	public HttpException newPasswordUser(String token, JsonNode params, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? userEntityController.newPasswordUser(token)
				: new ForbiddenException();
	}

	public HttpException resetUserPassword(String resetPassMail, JsonNode jUser, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? userEntityController.resetUserPassword(resetPassMail)
				: new ForbiddenException();
	}

	/**
	 *
	 * @param jUser JsonNode with all user parameters (forname, mail, name,
	 *              password) and the oldMail to perform the update even if the mail
	 *              is changed
	 * @param role  user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the user is updated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateUser(JsonNode jUser, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? userEntityController.updateUser(jUser)
				: new ForbiddenException();
	}

}
