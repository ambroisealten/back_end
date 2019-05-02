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

	
	Optional<DocumentSet> findByName(String name);

	//Optional<DocumentSet> findByUri(String textValue);

}
