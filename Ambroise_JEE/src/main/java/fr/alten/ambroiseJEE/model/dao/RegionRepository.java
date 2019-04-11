/**
 * 
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.Region;

/**
 * @author Andy Chabalier
 *
 */
public interface RegionRepository extends MongoRepository<Region, Long> {

	/**
	 * @param name the region name
	 * @return An Optional with the corresponding region or not. 
	 * @author Andy Chabalier
	 */
	Optional<Region> findByName(String name);
	
}
