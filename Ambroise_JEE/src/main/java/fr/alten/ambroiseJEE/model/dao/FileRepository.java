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
	 * @param isForforum true if we want the list of files of forum
	 * @return the list of forum files
	 * @author Andy Chabalier
	 */
	List<File> findByIsForForum(boolean isForforum);

	/**
	 * fetch file by uri
	 *
	 * @param fileDownloadUri the uri to fetch
	 * @author Andy Chabalier
	 */
	Optional<File> findByUri(String fileDownloadUri);

}
