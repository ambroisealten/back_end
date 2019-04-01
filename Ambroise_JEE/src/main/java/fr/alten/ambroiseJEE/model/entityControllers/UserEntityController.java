/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.Constants;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * User controller for entity gestion rules
 * @author Andy Chabalier
 *
 */
@Service
public class UserEntityController {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Method to create an user.
	 * User role are by default choosed by application default settings.
	 * @param jUser JsonNode with all user parameters (forname, mail, name, password)
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the user is created
	 * @author Andy Chabalier
	 */
	public HttpException createUser(JsonNode jUser) {

		User u = new User();

		u.setForname(jUser.get("forname").textValue());
		u.setMail(jUser.get("mail").textValue());
		u.setName(jUser.get("name").textValue());
		u.setPswd(jUser.get("pswd").textValue());
		u.setRole(Constants.DEFAULT_USER_ROLE);

		try {
			userRepository.insert(u);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * Try to fetch an user by is mail
	 * @param mail the user mail to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 */
	public Optional<User> getUserByMail(String mail) {
		return userRepository.findByMail(mail);
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	/**
	 * Method to populate database Only for test
	 * @param mail
	 * @param pswd
	 * @author Andy Chabalier
	 * @deprecated
	 */
	private void populateDatabase(String mail,String pswd) {
		User u = new User();

		u.setForname("totoForname");
		u.setMail(mail);
		u.setName("totoName");
		u.setPswd(pswd);
		u.setRole(Constants.DEFAULT_USER_ROLE);
		
		userRepository.insert(u);
	}
}
