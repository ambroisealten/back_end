package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.File;

/**
 * 
 * @author MAQUINGHEN MAXIME
 *
 */
public interface FileVersionRepository extends MongoRepository<File, Long> {

	/**
	 * fetch file by uri
	 * 
	 * @param fileDownloadUri the uri to fetch
	 * @author MAQUINGHEN MAXIME
	 */
	Optional<File> findByUri(String fileDownloadUri);
}
