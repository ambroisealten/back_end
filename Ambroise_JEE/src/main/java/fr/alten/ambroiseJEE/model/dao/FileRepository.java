/**
 * 
 */
package fr.alten.ambroiseJEE.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.File;

/**
 * @author Andy Chabalier
 *
 */
public interface FileRepository extends MongoRepository<File, Long> {

	
}
