/**
 * 
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.Sector;

/**
 * @author Andy Chabalier
 *
 */
public interface SectorRepository extends MongoRepository<Sector, Long> {

	/**
	 * find a sector by is name
	 * @param name the name to fetch
	 * @return the sector fetched
	 * @author Andy Chabalier
	 */
	Optional<Sector> findByName(String name);

}
