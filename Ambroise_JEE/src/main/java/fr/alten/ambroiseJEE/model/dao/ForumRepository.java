/**
 * 
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.Forum;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
public interface ForumRepository extends MongoRepository<Forum, Long> {

	/**
	 * Fetch the forum by id
	 * 
	 * @param id
	 * @return An Optional with the corresponding forum
	 * @author MAQUINGHEN MAXIME
	 */
	Optional<Forum> findBy_id(String _id);

	/**
	 * Fetch the forum data
	 * 
	 * @param name  the forum name
	 * @param date  the forum date
	 * @param place the forum place
	 * @return An Optional with the corresponding forum
	 * @author MAQUINGHEN MAXIME
	 */
	Optional<Forum> findByNameAndDateAndPlace(String name, String date, String place);

	/**
	 * fetch forums by date
	 * 
	 * @param date the date pass in parameter
	 * @return the list of forum
	 * @author MAQUINGHEN MAXIME
	 */
	List<Forum> findByDate(String date);

}
