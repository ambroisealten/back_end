/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.dao.FileRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * @author Andy Chabalier
 *
 */
@Service
public class FileEntityController {

	@Autowired
	private FileRepository fileRepository;

	/**
	 * Method to create a document
	 * 
	 * @param fileDownloadUri the uri of the document to create
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the document is created
	 * @author Andy Chabalier
	 */
	public HttpException createDocument(String fileDownloadUri) {
		File newFile = new File();
		newFile.setUri(fileDownloadUri);
		newFile.setDateOfAddition(System.currentTimeMillis());
		try {
			fileRepository.save(newFile);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

}
