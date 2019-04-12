package fr.alten.ambroiseJEE.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.File;

/**
 * 
 * @author MAQUINGHEN MAXIME
 *
 */
public interface FileVersionRepository extends MongoRepository<File, Long> {

}
