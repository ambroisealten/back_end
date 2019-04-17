package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.beans.FileVersion;

/**
 * 
 * @author MAQUINGHEN MAXIME
 *
 */
public interface FileVersionRepository extends MongoRepository<FileVersion, Long> {

	/**
	 * fetch file by uri
	 * 
	 * @param fileDownloadUri the uri to fetch
	 * @author MAQUINGHEN MAXIME
	 */
	Optional<File> findByUri(String fileDownloadUri);

	Optional<File> findByUriAndDateOfAddition(String uri, String dateOfAddition);
}
