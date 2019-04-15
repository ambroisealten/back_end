package fr.alten.ambroiseJEE.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.MobileDoc;

/**
 * 
 * @author Kylian Gehier
 *
 */
public interface MobileDocRepository extends MongoRepository<MobileDoc, Long>{

	
}
