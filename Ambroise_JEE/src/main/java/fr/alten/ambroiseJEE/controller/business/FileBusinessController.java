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
	 * @param fileDownloadUri the Uri to store
	 * @param role the current logged user's role
	 * @author Andy Chabalier
	 */
	public HttpException createDocument(String fileDownloadUri, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? fileEntityController.pushDocument(fileDownloadUri)
				: new ForbiddenException();
	}

	/**
	 * @param role the current logged user's role
	 * @return the list of files
	 * @author Andy Chabalier
	 */
	public List<File> getFiles(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return fileEntityController.getFiles();
		}
		throw new ForbiddenException();
	}

}
