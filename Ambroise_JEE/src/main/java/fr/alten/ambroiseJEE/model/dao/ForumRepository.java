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

	Optional<Forum> findById(String id);


}
