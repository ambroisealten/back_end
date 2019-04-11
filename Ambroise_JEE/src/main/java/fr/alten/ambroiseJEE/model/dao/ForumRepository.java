/**
 * 
 */
package fr.alten.ambroiseJEE.model.dao;

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
	 * @return An Optional with the corresponding forum or not.
	 * @author MAQUINGHEN MAXIME
	 */
	Optional<Forum> findById(String id);

}
