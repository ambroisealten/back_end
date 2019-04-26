/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.entityControllers.FileEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * @author Andy Chabalier
 *
 */
@Service
public class FileBusinessController {

	@Autowired
	private FileEntityController fileEntityController;

	/**
	 * Method to delegate document creation
	 *
	 * @param filePath the path of file to store
	 * @param role     the current logged user's role
	 * @author Andy Chabalier
	 */
	public File createDocument(final String filePath, final String fileName, final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return this.fileEntityController.pushDocument(filePath, fileName);
		} else {
			throw new ForbiddenException();
		}
	}

	/**
	 * delete a file
	 *
	 * @param _id  the id of file to delete
	 * @param role the current logged user's role
	 * @author Andy Chabalier
	 */
	public HttpException deleteFile(final String _id, final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role ? this.fileEntityController.deleteFile(_id)
				: new ForbiddenException();
	}

	/**
	 * Fetch the list of all collection's files
	 *
	 * @param path the collection path
	 * @param role the current logged user's role
	 * @return the list of the collection file
	 * @author Andy Chabalier
	 */
	public List<File> getCollectionFiles(final String path, final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return this.fileEntityController.getCollectionFiles(path);
		}
		throw new ForbiddenException();
	}

	/**
	 * Fetch the list of all files
	 *
	 * @param role the current logged user's role
	 * @return the list of files
	 * @author Andy Chabalier
	 */
	public List<File> getFiles(final UserRole role) {
		return this.fileEntityController.getFiles();
	}

	/**
	 * Fetch the list of all forum's filess
	 *
	 * @param role the current logged user's role
	 * @return the list of file forum
	 * @author Andy Chabalier
	 */
	public List<File> getFilesForum(final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return this.fileEntityController.getFilesForum();
		}
		throw new ForbiddenException();
	}

	/**
	 * Update a file
	 *
	 * @param _id         the id of file to update
	 * @param path        the path to update
	 * @param displayName the display name to update
	 * @param role        the current logged user's role
	 * @return
	 * @author Andy Chabalier
	 */
	public HttpException updateDocument(final String _id, final String path, final String displayName,
			final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role
				? this.fileEntityController.updateFile(_id, path, displayName)
				: new ForbiddenException();
	}
}
