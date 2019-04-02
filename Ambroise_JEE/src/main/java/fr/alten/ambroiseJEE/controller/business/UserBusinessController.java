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
import fr.alten.ambroiseJEE.security.Roles;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

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
	 * @author Andy Chabalier, Maxime Maquinghen 
	 */
	public HttpException createUser(JsonNode jUser, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? userEntityController.createUser(jUser) : new ForbiddenException();
	}
	
	/**
	 * ask for fetch an user by is mail
	 * @param mail the user mail to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 */
	public Optional<User> getUserByMail(String mail, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? userEntityController.getUserByMail(mail) : Optional.empty();
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

	/**
	 * 
	 * @param role user role
	 * @return the list of all User
	 * @author MAQUINGHEN MAXIME
	 */
	public List<User> getUsers(int role) {
		if (Roles.ADMINISTRATOR_USER_ROLE.getValue()== role) {
			return userEntityController.getUsers();
		}
		throw new ForbiddenException();		 
	}

	/**
	 * 
	 * @param jUser JsonNode with all user parameters (forname, mail, name,
	 *              password) and the oldMail to perform the update even if the mail is changed
	 * @param role user role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link CreatedException} if the user is updated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateUser(JsonNode jUser, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? userEntityController.updateUser(jUser) : new ForbiddenException();
	}

	public HttpException deleteUser(JsonNode params, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? userEntityController.deleteUser(params.get("mail").textValue()) : new ForbiddenException();
	}
	
}
