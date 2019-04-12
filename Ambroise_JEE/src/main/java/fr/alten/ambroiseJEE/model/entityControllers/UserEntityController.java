/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.controller.business.AgencyBusinessController;
import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.MailCreator;
import fr.alten.ambroiseJEE.utils.RandomString;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;
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

	@Autowired
	private AgencyBusinessController agencyEntityController;

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

		// if the mail don't match with the mail pattern
		if (!validateMail(jUser.get("mail").textValue())) {
			return new UnprocessableEntityException();
		}

		User newUser = new User();

		newUser.setForname(jUser.get("forname").textValue());
		newUser.setMail(jUser.get("mail").textValue());
		newUser.setName(jUser.get("name").textValue());
		newUser.setPswd(jUser.get("pswd").textValue());
		UserRole newRole;
		try {
			newRole = UserRole.valueOf(jUser.get("role").textValue());
		} catch (Exception e) {
			newRole = UserRole.CONSULTANT; //in case of wrong role input, we get the default role
		}
		newUser.setRole(newRole);
		Optional<Agency> agency = agencyEntityController.getAgency(jUser.get("agency").textValue());
		if(agency.isPresent()) {
			newUser.setAgency(agency.get().getName());
		}

		try {
			userRepository.save(newUser);
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
	 * 
	 * @param emailStr the string to validate
	 * @return true if the string match with the mail pattern
	 */
	private static boolean validateMail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	/**
	 * 
	 * @return the list of all user
	 * @author MAQUINGHEN MAXIME
	 */
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	/**
	 * 
	 * @param jUser JsonNode with all user parameters (forname, mail, name,
	 *              password) and the oldMail to perform the update even if the mail
	 *              is changed
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not
	 *         found and {@link CreatedException} if the user is updated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateUser(JsonNode jUser) {
		Optional<User> userOptionnal = userRepository.findByMail(jUser.get("oldMail").textValue());

		if (userOptionnal.isPresent()) {
			User user = userOptionnal.get();
			user.setForname(jUser.get("forname").textValue());
			user.setMail(jUser.get("mail").textValue());
			user.setName(jUser.get("name").textValue());
			user.setPswd(jUser.get("pswd").textValue());
			UserRole newRole;
			try {
				newRole = UserRole.valueOf(jUser.get("role").textValue());
				user.setRole(newRole);
			} catch (Exception e) {
				//in case of wrong role input, we not change the role
			}
			Optional<Agency> agency = agencyEntityController.getAgency(jUser.get("agency").textValue());
			if(agency.isPresent()) {
				user.setAgency(agency.get().getName());
			}
			userRepository.save(user);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * 
	 * @param mail the user mail to fetch
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link RessourceNotFoundException} if the ressource is not found and
	 *         {@link CreatedException} if the user is desactivated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException deleteUser(String mail) {
		Optional<User> userOptionnal = userRepository.findByMail(mail);

		if (userOptionnal.isPresent()) {
			User user = userOptionnal.get();
			user.setForname("");
			user.setMail("desactivated" + System.currentTimeMillis());
			user.setName("");
			user.setPswd("");
			user.setRole(UserRole.DESACTIVATED);
			user.setAgency(null);
			userRepository.save(user);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * 
	 * @param mail the mail concerned by the password changement
	 * 
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link RessourceNotFoundException} if the ressource is not found and
	 *         {@link CreatedException} if the password is changed
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException resetUserPassword(String mail) {
		Optional<User> userOptionnal = userRepository.findByMail(mail);

		if (userOptionnal.isPresent()) {
			User user = userOptionnal.get();
			String new_pass = RandomString.getAlphaNumericString(20);
			user.setPswd(new_pass);
			userRepository.save(user);
			MailCreator.AdminUserResetPassword(new_pass); // TODO
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

	public HttpException newPasswordUser(String token) {
		// TODO creation de la partie de verification du token et url.
		return null;
	}

	public Optional<User> getUser(String usermail) {
		return userRepository.findByMail(usermail);
	}
}