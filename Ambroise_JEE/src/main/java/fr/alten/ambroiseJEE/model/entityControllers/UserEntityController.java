/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.security.Roles;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * User controller for entity gestion rules
 * 
 * @author Andy Chabalier
 *
 */
@Service
public class UserEntityController {

	@Autowired
	private UserRepository userRepository;

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Method to create an user. User role are by default choosed by application
	 * default settings.
	 * 
	 * @param jUser JsonNode with all user parameters (forname, mail, name,
	 *              password)
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the user is created
	 * @author Andy Chabalier
	 */
	public HttpException createUser(JsonNode jUser) {

		//if the mail don't match with the mail pattern
		if(!validateMail(jUser.get("mail").textValue())) {
			return new UnprocessableEntityException();
		}

		User newUser = new User();

		newUser.setForname(jUser.get("forname").textValue());
		newUser.setMail(jUser.get("mail").textValue());
		newUser.setName(jUser.get("name").textValue());
		newUser.setPswd(jUser.get("pswd").textValue());
		newUser.setRole(Roles.DEFAULT_USER_ROLE.getValue());

		try {
			userRepository.insert(newUser);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * Try to fetch an user by is mail
	 * 
	 * @param mail the user mail to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 */
	public Optional<User> getUserByMail(String mail) {
		return userRepository.findByMail(mail);
	}

	/**
	 * Try to fetch an user by is credentials (mail and password)
	 * 
	 * @param mail the user's mail to fetch
	 * @param pswd the user's password to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 */
	public Optional<User> getUserByCredentials(String mail, String pswd) {
		return userRepository.findByMailAndPswd(mail, pswd);
	}

	/**
	 * Method to validate if the mail math with the mail pattern
	 * @param emailStr the string to validate
	 * @return true if the string match with the mail pattern
	 */
	private static boolean validateMail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
}
