/**
 *
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.beans.Skill;

/**
 * @author Andy Chabalier
 *
 */
public interface PostalCodeRepository extends MongoRepository<PostalCode, Long> {

	/**
	 * @param name the postalCode name
	 * @return An Optional with the corresponding city or not.
	 * @author Andy Chabalier
	 */
	Optional<PostalCode> findByName(String name);
	
	/**
	 * @param id the postalCode id
	 * @return An Optional with the corresponding city or not.
	 * @author Camille Schnell
	 */
	Optional<PostalCode> findBy_id(String id);
}
