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
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * User controller for entity gestion rules
 *
 * @author Andy Chabalier
 *
 */
@Service
public class UserEntityController {

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Method to validate if the mail math with the mail pattern
	 *
	 * @param emailStr the string to validate
	 * @return true if the string match with the mail pattern
	 */
	private static boolean validateMail(final String emailStr) {
		final Matcher matcher = UserEntityController.VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	@Autowired
	private UserRepository userRepository;

	//@Autowired
	//private AgencyEntityController agencyEntityController;

	/**
	 * Method to create an user. User role are by default choosed by application
	 * default settings.
	 *
	 * @param jUser JsonNode with all user parameters (forname, mail, name,
	 *              password)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the user is created
	 * @author Andy Chabalier
	 */
	public HttpException createUser(final JsonNode jUser) {
		try {
			// if the mail don't match with the mail pattern
			if (!UserEntityController.validateMail(jUser.get("mail").textValue())) {
				return new UnprocessableEntityException();
			}

			final User newUser = new User();

			newUser.setForname(jUser.get("forname").textValue());
			newUser.setMail(jUser.get("mail").textValue());
			newUser.setName(jUser.get("name").textValue());
			newUser.setPswd(jUser.get("pswd").textValue());
			UserRole newRole;
			try {
				newRole = UserRole.valueOf(jUser.get("role").textValue());
			} catch (final Exception e) {
				newRole = UserRole.CONSULTANT; // in case of wrong role input, we get the default role
			}
			newUser.setRole(newRole);
			final Agency agency = this.agencyEntityController.getAgency(jUser.get("agency").textValue());
			newUser.setAgency(agency.getName());
			this.userRepository.save(newUser);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param mail the user mail to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link CreatedException} if the user is deactivated
	 * @author MAQUINGHEN MAXIME
	 * @throws {@link ResourceNotFoundException} if the ressource can't be found
	 */
	public HttpException deleteUser(final String mail) {
		try {
			final User user = this.userRepository.findByMail(mail).orElseThrow(ResourceNotFoundException::new);
			user.setForname("");
			user.setMail("deactivated" + System.currentTimeMillis());
			user.setName("");
			user.setPswd("");
			user.setRole(UserRole.DEACTIVATED);
			user.setAgency(null);
			this.userRepository.save(user);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 *
	 * @param usermail
	 * @return
	 * @throws {@link ResourceNotFoundException} if the ressource can't be found
	 */
	public User getUser(final String usermail) {
		return this.userRepository.findByMail(usermail).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Try to fetch an user by is credentials (mail and password)
	 *
	 * @param mail the user's mail to fetch
	 * @param pswd the user's password to fetch
	 * @return the corresponding user
	 * @author Andy Chabalier
	 */
	public Optional<User> getUserByCredentials(final String mail, final String pswd) {
		return this.userRepository.findByMailAndPswd(mail, pswd);

	}

	/**
	 * Try to fetch an user by is mail
	 *
	 * @param mail the user mail to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 * @throws {@link ResourceNotFoundException} if the ressource can't be found
	 */
	public User getUserByMail(final String mail) {
		return this.userRepository.findByMail(mail).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 *
	 * @return the list of all user
	 * @author MAQUINGHEN MAXIME
	 */
	public List<User> getUsers() {
		return this.userRepository.findAll();
	}

	public HttpException newPasswordUser(final String token) {
		// TODO creation de la partie de verification du token et url.
		return null;
	}

	/**
	 *
	 * @param mail the mail concerned by the password changement
	 *
	 * @return {@link HttpException} corresponding to the status of the request and
	 *         {@link OkException} if the password is changed
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException resetUserPassword(final String mail) {
		final User user = this.userRepository.findByMail(mail).orElseThrow(ResourceNotFoundException::new);
		try {
			final String new_pass = RandomString.getAlphaNumericString(20);
			user.setPswd(new_pass);
			this.userRepository.save(user);
			MailCreator.AdminUserResetPassword(new_pass); // TODO
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 *
	 * @param jUser JsonNode with all user parameters (forname, mail, name,
	 *              password) and the oldMail to perform the update even if the mail
	 *              is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the user is updated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateUser(final JsonNode jUser) {
		try {
			final User user = this.userRepository.findByMail(jUser.get("oldMail").textValue())
					.orElseThrow(ResourceNotFoundException::new);

			user.setForname(jUser.get("forname").textValue());
			user.setMail(jUser.get("mail").textValue());
			user.setName(jUser.get("name").textValue());
			user.setPswd(jUser.get("pswd").textValue());
			UserRole newRole;
			try {
				newRole = UserRole.valueOf(jUser.get("role").textValue());
				user.setRole(newRole);
			} catch (final Exception e) {
			}
			final Agency agency = this.agencyEntityController.getAgency(jUser.get("agency").textValue());
			user.setAgency(agency.getName());
			this.userRepository.save(user);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}
}