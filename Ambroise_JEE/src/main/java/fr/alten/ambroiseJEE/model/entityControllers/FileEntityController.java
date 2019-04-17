/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.dao.FileRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;

/**
 * @author Andy Chabalier
 *
 */
@Service
public class FileEntityController {

	@Autowired
	private FileRepository fileRepository;

	/**
	 * @return the list of files
	 * @author Andy Chabalier
	 */
	public List<File> getFiles() {
		return fileRepository.findAll();
	}

	/**
	 * @return the list of forum files
	 * @author Andy Chabalier
	 */
	public List<File> getFilesForum() {
		return fileRepository.findByIsForForum(true);
	}

	/**
	 * Method to create a document
	 *
	 * @param fileDownloadUri the uri of the document to create
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link OkException} if there is a conflict in the database
	 *         (that mean file already exist and then it's an upload. But no change
	 *         to make in base and {@link CreatedException} if the document is
	 *         created
	 * @author Andy Chabalier
	 */
	public HttpException pushDocument(String fileDownloadUri, boolean isForForum) {
		Optional<File> fileOptional = fileRepository.findByUri(fileDownloadUri);
		File file;
		if (fileOptional.isPresent()) {
			file = fileOptional.get();
		} else {
			file = new File();
		}
		file.setUri(fileDownloadUri);
		file.setDateOfModification(System.currentTimeMillis());
		file.setForForum(isForForum);
		try {
			fileRepository.save(file);
		} catch (Exception e) {
			return new OkException();
		}
		return new CreatedException();
	}

}
