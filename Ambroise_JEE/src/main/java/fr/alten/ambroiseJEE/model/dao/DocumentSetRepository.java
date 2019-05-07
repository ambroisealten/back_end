package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.DocumentSet;

/**
 * 
 * @author MAQUINGHEN MAXIME
 *
 */
public interface DocumentSetRepository extends MongoRepository<DocumentSet, Long> {

	/**
	 * Fetch a file by its name
	 * 
	 * @param name the name of the file
	 * @return an Optional containing a DocumentSet (or not)
	 * @author MAQUINGHEN MAXIME
	 */
	Optional<DocumentSet> findByName(String name);

}
