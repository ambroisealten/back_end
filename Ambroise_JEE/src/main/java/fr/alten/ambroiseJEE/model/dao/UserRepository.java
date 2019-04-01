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
	 * Fetch user by forename
	 * 
	 * @param forename
	 * @return the user
	 */
	Optional<User> findByForname(String forename);

	/**
	 * Fetch user by mail
	 * 
	 * @param mail
	 * @return the user
	 */
	Optional<User> findByMail(String mail);

}
