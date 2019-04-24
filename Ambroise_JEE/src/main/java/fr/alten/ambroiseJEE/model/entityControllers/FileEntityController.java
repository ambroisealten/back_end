/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.dao.FileRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author Andy Chabalier
 *
 */
@Service
public class FileEntityController {

	@Autowired
	private FileRepository fileRepository;

	/**
	 * Method to delete a file by is Id
	 *
	 * @param _id the id of file to delete
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the ressource is not
	 *         found and {@link OkException} if the document is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteFile(final String _id) {
		try {
			this.fileRepository.delete(this.fileRepository.findBy_id(new ObjectId(_id)).get());
		} catch (final Exception e) {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * @return the list of files
	 * @author Andy Chabalier
	 */
	public List<File> getFiles() {
		return this.fileRepository.findAll();
	}

	/**
	 * Method to create a document
	 *
	 * @param path the path of the document to create
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database{@link CreatedException} if the document is created
	 * @author Andy Chabalier
	 */
	public File pushDocument(final String path, final String fileName) {
		final File file = new File();
		file.setPath(path);
		file.setExtension(FilenameUtils.getExtension(fileName));
		file.setDateOfCreation(System.currentTimeMillis());
		file.setName(fileName);
		try {
			return this.fileRepository.save(file);
		} catch (final Exception e) {
			throw new ConflictException();
		}
	}

}
