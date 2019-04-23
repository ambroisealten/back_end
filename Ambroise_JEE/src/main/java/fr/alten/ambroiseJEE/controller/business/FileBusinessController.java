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
	 * @param role the current logged user's role
	 * @return the list of files
	 * @author Andy Chabalier
	 */
	public List<File> getFiles(final UserRole role) {
		return this.fileEntityController.getFiles();
	}
}
