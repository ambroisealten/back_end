/**
 *
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.Agency;

/**
 * @author Andy Chabalier
 *
 */
public interface AgencyRepository extends MongoRepository<Agency, Long> {

	/**
	 * @param id the agency id
	 * @return An Optional with the corresponding agency or not.
	 * @author Camille Schnell
	 */
	Optional<Agency> findBy_id(String id);

	/**
	 * @param name the agency name
	 * @return An Optional with the corresponding agency or not.
	 * @author Andy Chabalier
	 */
	Optional<Agency> findByName(String name);

}
