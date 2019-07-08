/**
 *
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.File;

/**
 * @author Andy Chabalier
 *
 */
public interface FileRepository extends MongoRepository<File, Long> {

	/**
	 * fetch a file by is id
	 *
	 * @param _id the id to fetch
	 * @return an optional with the file or empty
	 * @author Andy Chabalier
	 */
	Optional<File> findBy_id(ObjectId _id);

	/**
	 * fetch file by path
	 *
	 * @param fileDownloadpath the path to fetch
	 * @author Andy Chabalier
	 */
	Optional<File> findByPath(String fileDownloadpath);

}
