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
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
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
	 * @param user the user to check
	 * @return true if the user can't connect with the standard method (consultant
	 *         or deactivated)
	 * @author Andy Chabalier
	 */
	private boolean cantBeConnected(final User user) {
		return user.getRole().equals(UserRole.CONSULTANT) || user.getRole().equals(UserRole.DEACTIVATED);
	}

	/**
	 * Method to determine if the provided credential are valid
	 *
	 * @param mail the user's mail to check
	 * @param pswd the user's password to check
	 * @return an optional with the fetched user or empty if user is not found
	 * @author Andy Chabalier
	 */
	public Optional<String> checkIfCredentialIsValid(final String mail, final String pswd) {
		final Optional<User> optionalUser = this.userEntityController.getUserByCredentials(mail, pswd);
		return optionalUser.isPresent() && !cantBeConnected(optionalUser.get())
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
	public HttpException createUser(final JsonNode jUser, final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role
				? this.userEntityController.createUser(jUser)
				: new ForbiddenException();
	}

	/**
	 * Method to delegate the deletion of an User
	 *
	 * @param params the user mail to delete
	 * @param role   the user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link CreatedException} if the user is updated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException deleteUser(final JsonNode params, final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role
				? this.userEntityController.deleteUser(params.get("mail").textValue())
				: new ForbiddenException();
	}

	/**
	 * Method to delegate the getting of a specific User by its mail
	 *
	 * @param mail the user mail to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 */
	public User getUserByMail(final String mail, final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return this.userEntityController.getUserByMail(mail);
		} else {
			throw new ForbiddenException();
		}
	}

	/**
	 * Method to delegate getting of all Users
	 *
	 * @param role user role
	 * @return the list of all Users
	 * @author MAQUINGHEN MAXIME
	 */
	public List<User> getUsers(final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return this.userEntityController.getUsers();
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate the set of a new password for an User
	 * 
	 * @param userMail the mail of the concerned User
	 * @param params   the JsonNode containing all the parameters
	 * @param role     the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link ConflictException} if there is a duplicate in the database,
	 *         {@link ResourceNotFoundException} if the resource can't be found,
	 *         {@link ForbiddenException} if the user hasn't the right to perform
	 *         this action, {@link InternalServerErrorException} if there are any
	 *         other errors and {@link OkException} if the password of the User is
	 *         correctly set
	 * @author MAQUINGHEN MAXIME, Lucas Royackkers
	 */
	public HttpException newPasswordUser(final String userMail, final JsonNode params, final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role
				? this.userEntityController.newPasswordUser(userMail, params)
				: new ForbiddenException();
	}

	/**
	 * Method to delegate the reset the password of a User
	 * 
	 * @param jUser the JsonNode containing all the parameters
	 * @param role  the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link ResourceNotFoundException} if the resource hasn't been found,
	 *         {@link ForbiddenException} if the user hasn't the right to perform
	 *         this action, {@link ConflictException} if there is a duplicate in the
	 *         database and {@link OkException} if the password is changed
	 * @author Lucas Royackkers
	 */
	public HttpException resetUserPassword(final JsonNode jUser, final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role
				? this.userEntityController.resetUserPassword(jUser)
				: new ForbiddenException();
	}

	/**
	 * Method to delegate the update of an User
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
	public HttpException updateUser(final JsonNode jUser, final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role
				? this.userEntityController.updateUser(jUser)
				: new ForbiddenException();
	}

}
