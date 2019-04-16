/**
 *
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.City;

/**
 * @author Andy Chabalier
 *
 */
public interface CityRepository extends MongoRepository<City, Long> {

	/**
	 * @param name the city name
	 * @return An Optional with the corresponding city or not.
	 * @author Andy Chabalier
	 */
	Optional<City> findByName(String name);

}
