/**
 *
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.User;

/**
 * @author Andy Chabalier
 *
 */
public interface UserRepository extends MongoRepository<User, Long> {

	/**
	 * Fetch user by mail
	 *
	 * @param mail
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 */
	Optional<User> findByMail(String mail);

	/**
	 * Fetch user by is credential
	 *
	 * @param mail the user's mail to fetch
	 * @param pswd the user's password to fetch
	 * @return An Optional with the corresponding user or not.
	 * @author Andy Chabalier
	 */
	Optional<User> findByMailAndPswd(String mail, String pswd);
}
