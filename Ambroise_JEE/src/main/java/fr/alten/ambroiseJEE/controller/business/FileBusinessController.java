/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.entityControllers.FileEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

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
		if (haveAccess(role)) {
			return this.fileEntityController.pushDocument(filePath, fileName);
		} else {
			throw new ForbiddenException();
		}
	}

	/**
	 * check if the role have access to method
	 * 
	 * @param role role to check
	 * @return true if granted
	 * @author Andy Chabalier
	 */
	public boolean haveAccess(final UserRole role) {
		return isAdmin(role) || UserRole.MANAGER == role;
	}

	/**
	 * delete a file
	 *
	 * @param _id  the id of file to delete
	 * @param role the current logged user's role
	 * @return the {@link HttpException} corresponding to the status of the request
	 *         ( {@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the document is deleted or
	 *         {@link ForbiddenException} if the user is not allowed
	 * @author Andy Chabalier
	 */
	public HttpException deleteFile(final String _id, final UserRole role) {
		return isAdmin(role) ? this.fileEntityController.deleteFile(_id) : new ForbiddenException();
	}

	/**
	 * check if the user is admin
	 * 
	 * @param role the user'srole to check
	 * @return true if admin
	 * @author Andy Chabalier
	 */
	public boolean isAdmin(final UserRole role) {
		return UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role;
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
		if (isAdmin(role)) {
			return this.fileEntityController.getCollectionFiles(path);
		}
		throw new ForbiddenException();
	}

	public File getDocument(final String _id, final UserRole role) {
		if (haveAccess(role)) {
			if (isValid(_id)) {
				return this.fileEntityController.getFile(new ObjectId(_id));
			} else {
				throw new UnprocessableEntityException();
			}
		}
		throw new ForbiddenException();
	}

	public boolean isValid(final String _id) {
		return ObjectId.isValid(_id);
	}

	/**
	 * Fetch the list of all files
	 *
	 * @param role the current logged user's role
	 * @return the list of files
	 * @author Andy Chabalier
	 */
	public List<File> getFiles(final UserRole role) {
		if (isAdmin(role)) {
			return this.fileEntityController.getFiles();
		}
		throw new ForbiddenException();
	}

	/**
	 * Fetch the list of all forum's filess
	 *
	 * @param role the current logged user's role
	 * @return the list of file forum
	 * @author Andy Chabalier
	 */
	public List<File> getFilesForum(final UserRole role) {
		if (isAdmin(role)) {
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
	 * @return the {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the document is not found
	 *         {@link OkException} if the document is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateDocument(final String _id, final String path, final String displayName,
			final UserRole role) {
		return isAdmin(role) ? this.fileEntityController.updateFile(_id, path, displayName) : new ForbiddenException();
	}
}
