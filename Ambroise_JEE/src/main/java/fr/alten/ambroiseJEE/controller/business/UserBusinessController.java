/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.entityControllers.UserEntityController;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * User controller for business rules.
 * @author Andy Chabalier
 *
 */
@Service
public class UserBusinessController {

	@Autowired
	private UserEntityController userEntityController;
	
	/**
	 * Method to delegate user creation
	 * @param jUser JsonNode with all user parameters (forname, mail, name, password
	 *              and role)
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the user is created
	 * @author Andy Chabalier
	 */
	public HttpException createUser(JsonNode jUser) {
		return userEntityController.createUser(jUser);
	}
	
	/**
	 * ask for fetch an user by is mail
	 * @param mail the user mail to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 */
	public Optional<User> getUserByMail(String mail) {
		return userEntityController.getUserByMail(mail);
	}

	/**
	 * Method to determine if the provided credential are valid
	 * @param mail the user's mail to check
	 * @param pswd the user's password to check
	 * @return an empty optional
	 * @author Andy Chabalier
	 */
	public Optional<String> checkIfCredentialIsValid(String mail, String pswd) {	
		Optional<User> optionalUser = userEntityController.getUserByCredentials(mail,pswd);	
		return optionalUser.isPresent() ? Optional.of(optionalUser.get().getMail() + "|" + optionalUser.get().getRole()):Optional.empty();	
	}
}
