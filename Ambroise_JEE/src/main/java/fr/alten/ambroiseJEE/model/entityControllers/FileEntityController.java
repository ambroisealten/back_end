/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
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
	 * Fetch the list of document of the collection
	 *
	 * @param path the collection path
	 * @return the list of the collection file
	 * @author Andy Chabalier
	 */
	public List<File> getCollectionFiles(final String path) {
		final File fileExemple = new File();
		fileExemple.setPath(path);

		// Create a matcher for this file Example. We want to focus only on path, then
		// we ignore null value and dateOfCreation wich is a long value and can't be
		// null
		final ExampleMatcher matcher = ExampleMatcher.matching()
				.withMatcher("path", GenericPropertyMatchers.startsWith()).withIgnoreNullValues()
				.withIgnorePaths("dateOfCreation");

		return this.fileRepository.findAll(Example.of(fileExemple, matcher));
	}

	/**
	 * @return the list of files
	 * @author Andy Chabalier
	 */
	public List<File> getFiles() {
		return this.fileRepository.findAll();
	}

	/**
	 * Use an file example with the path /forum/ to fetch files with path starting
	 * with /forum/
	 *
	 * @return the list of forum files
	 * @author Andy Chabalier
	 */
	public List<File> getFilesForum() {
		return getCollectionFiles("/forum");
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
