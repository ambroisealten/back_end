/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
	 * @param mail
	 * @return
	 * @author Andy Chabalier
	 */
	public boolean validateMail(final String mail) {
		return MailUtils.validateMail(mail);
	}

	/**
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
	 *
	 * @param usermail
	 * @return
	 * @throws {@link ResourceNotFoundException} if the resource can't be found
	 */
	public User getUser(final String usermail) {
		return this.userRepository.findByMailIgnoreCase(usermail).orElseThrow(ResourceNotFoundException::new);
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
	 * @throws {@link ResourceNotFoundException} if the resource can't be found
	 */
	public User getUserByMail(final String mail) {
		return this.userRepository.findByMailIgnoreCase(mail).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 *
	 * @return the list of all user
	 * @author MAQUINGHEN MAXIME
	 */
	public List<User> getUsers() {
		return this.userRepository.findAll().parallelStream().filter(user -> !user.getMail().contains("deactivated"))
				.collect(Collectors.toList());
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
		final User user = this.userRepository.findByMailIgnoreCase(mail).orElseThrow(ResourceNotFoundException::new);
		try {
			final String new_pass = RandomString.getAlphaNumericString(20);
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
			} catch (final Exception e) {
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
	private void updateUserMailOnSkillSheetOnCascade(String oldMail, String newMail) {
		List<SkillsSheet> skillSheets = this.skillsSheetRepository.findByMailVersionAuthorIgnoreCaseOrder(oldMail);
		skillSheets.parallelStream().forEach(skillSheet -> {
			skillSheet.setMailVersionAuthor(newMail);
		});
		this.skillsSheetRepository.saveAll(skillSheets);
	}
}