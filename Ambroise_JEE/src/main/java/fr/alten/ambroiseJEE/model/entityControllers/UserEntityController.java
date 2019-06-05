/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.MailUtils;
import fr.alten.ambroiseJEE.utils.RandomString;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
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

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AgencyEntityController agencyEntityController;

	@Autowired
	private SkillsSheetRepository skillsSheetRepository;

	/**
	 * Method to create an user. User role are by default chosen by application
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
			if (!validateMail(jUser.get("mail").textValue())) {
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
	 * Delete a specific User given its mail
	 *
	 * @param mail the user mail to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link CreatedException} if the user is deactivated
	 * @author MAQUINGHEN MAXIME
	 * @throws {@link ResourceNotFoundException} if the resource can't be found
	 */
	public HttpException deleteUser(final String mail) {
		try {
			final User user = this.userRepository.findByMailIgnoreCase(mail)
					.orElseThrow(ResourceNotFoundException::new);
			user.setForname("");
			user.setMail("deactivated" + System.currentTimeMillis());
			user.setName("");
			user.setPswd("");
			user.setRole(UserRole.DEACTIVATED);
			user.setAgency(null);
			updateUserMailOnSkillSheetOnCascade(mail, user.getMail());
			this.userRepository.save(user);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * Try to fetch an user by its credentials (mail and password)
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
	 * Try to fetch an user by its mail
	 *
	 * @param mail the user mail to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 * @throws {@link ResourceNotFoundException} if the resource can't be found
	 */
	public User getUserByMail(final String mail) {
		return this.userRepository.findByMailIgnoreCase(mail).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Get all Users within the database
	 *
	 * @return the list of all user
	 * @author MAQUINGHEN MAXIME
	 */
	public List<User> getUsers() {
		return this.userRepository.findAll().parallelStream().filter(user -> !user.getMail().contains("deactivated"))
				.collect(Collectors.toList());
	}

	/**
	 * Set a new Password for a corresponding User
	 * 
	 * @param mail   the mail of an User
	 * @param params the JsonNode containing all parameters (only its new password)
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link ConflictException} if there is a duplicate in the database,
	 *         {@link ResourceNotFoundException} if the resource can't be found,
	 *         {@link InternalServerErrorException} if there are any other errors
	 *         and {@link OkException} if the password of the User is correctly set
	 * @author Lucas Royackkers
	 */
	public HttpException newPasswordUser(final String mail, final JsonNode params) {
		final User user = this.userRepository.findByMailIgnoreCase(mail).orElseThrow(ResourceNotFoundException::new);
		try {
			final String new_pass = params.get("pswd").textValue();
			user.setPswd(new_pass);

			this.userRepository.save(user);
		} catch (final DuplicateKeyException dke) {
			return new ConflictException();
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			e.printStackTrace();

			return new InternalServerErrorException();
		}
		return new OkException();
	}

	/**
	 * Method to reset an User password
	 *
	 * @param jUser the JsonNode containing all the parameters (the mail of the
	 *              concerned User)
	 *
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link ResourceNotFoundException} if the resource hasn't been found,
	 *         {@link ConflictException} if there is a duplicate in the database and
	 *         {@link OkException} if the password is changed
	 * @author MAQUINGHEN MAXIME, Lucas Royackkers
	 */
	public HttpException resetUserPassword(final JsonNode jUser) {
		final String mail = jUser.get("mail").textValue();
		final User user = this.userRepository.findByMailIgnoreCase(mail).orElseThrow(ResourceNotFoundException::new);
		try {
			final String new_pass = RandomString.getAlphaNumericString(60);
			user.setPswd(new_pass);
			this.userRepository.save(user);
			MailUtils.AdminUserResetPassword(new_pass); // TODO
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
			final String oldMail = jUser.get("oldMail").textValue();
			final User user = this.userRepository.findByMailIgnoreCase(oldMail)
					.orElseThrow(ResourceNotFoundException::new);

			user.setForname(jUser.get("forname").textValue());
			final String newMail = jUser.get("mail").textValue();
			user.setMail(newMail);
			user.setName(jUser.get("name").textValue());
			user.setPswd(jUser.get("pswd").textValue());
			UserRole newRole;
			try {
				newRole = UserRole.valueOf(jUser.get("role").textValue());
				user.setRole(newRole);
			} catch (NullPointerException | IllegalArgumentException e) {
				return new UnprocessableEntityException();
			}
			final Agency agency = this.agencyEntityController.getAgency(jUser.get("agency").textValue());

			user.setAgency(agency.getName());
			updateUserMailOnSkillSheetOnCascade(oldMail, newMail);
			this.userRepository.save(user);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * update associated skillSheet on cascade
	 *
	 * @param oldMail
	 * @param newMail
	 * @author Andy Chabalier
	 */
	private void updateUserMailOnSkillSheetOnCascade(final String oldMail, final String newMail) {
		final List<SkillsSheet> skillSheets = this.skillsSheetRepository.findByMailVersionAuthorIgnoreCase(oldMail);
		skillSheets.parallelStream().forEach(skillSheet -> {
			skillSheet.setMailVersionAuthor(newMail);
		});
		this.skillsSheetRepository.saveAll(skillSheets);
	}

	/**
	 * @param mail
	 * @return
	 * @author Andy Chabalier
	 */
	public boolean validateMail(final String mail) {
		return MailUtils.validateMail(mail);
	}
}