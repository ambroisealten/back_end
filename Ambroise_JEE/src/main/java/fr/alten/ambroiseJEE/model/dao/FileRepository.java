/**
 *
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.File;

/**
 * @author Andy Chabalier
 *
 */
public interface FileRepository extends MongoRepository<File, Long> {

	/**
	 * fetch file by uri
	 *
	 * @param fileDownloadUri the uri to fetch
	 * @author Andy Chabalier
	 */
	Optional<File> findByPath(String fileDownloadUri);

}
