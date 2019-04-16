/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.entityControllers.FileVersionEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class FileVersionBusinessController {

	@Autowired
	private FileVersionEntityController fileVersionEntityController;

	/**
	 * Compare the data received by the app and the version store on the
	 * server
	 * 
	 * @param appVersion    the App Version data
	 * @param serverVersion the Server Version data
	 * @param role          the user role
	 * @return return {@link OkException} When the comparison is complete
	 *         {@link ForbiddenException} when the role is not authorize to access
	 *         this data
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException compareDataVersion(JsonNode appVersion, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) ? fileVersionEntityController.compareVersionData(appVersion)
						: new ForbiddenException();
	}

	/**
	 * Update files Data Version
	 * 
	 * @param appVersion    the App Version data
	 * @param serverVersion the Server Version data
	 * @param role          the user role
	 * @return {@link OkException} When the comparison is complete
	 *         {@link ForbiddenException} when the role is not authorize to access
	 *         this data
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateDataVersion(String appVersion, String serverVersion, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) ? fileVersionEntityController.updateAppDataVersion(appVersion, serverVersion)
						: new ForbiddenException();
	}

	/**
	 * Get AppVersion of the documents
	 * 
	 * @param appVersion the version of document of the mobile app
	 * @param role       role of the user
	 * @return return the list of documents or {@link ForbiddenException} when the
	 *         role is not authorized to access to this data
	 * @author MAQUINGHEN MAXIME
	 */
	public List<File> getAppVersion(String appVersion, UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) {
			return fileVersionEntityController.getVersionData();
		}
		throw new ForbiddenException();
	}

	/**
	 * Get the version and the file version
	 * 
	 * @param role the user role
	 * @return the List of all the file {@link ForbiddenException}
	 * @author MAQUINGHEN MAXIME
	 */
	public List<File> setAppVersion(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) {
			return fileVersionEntityController.getVersionData();
		}
		throw new ForbiddenException();
	}

	// public Optional<Version> getVersion() {

	// }
}
