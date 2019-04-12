/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.entityControllers.FileVersionEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class FileVersionBusinessController {

	@Autowired
	private FileVersionEntityController fileVersionEntityController;

	public HttpException compareDataVersion(String appVersion, String serverVersion, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) ? fileVersionEntityController.compareVersionData(appVersion, serverVersion)
						: new ForbiddenException();
	}

	public HttpException updateDataVersion(String appVersion, String serverVersion, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) ? fileVersionEntityController.updateAppDataVersion(appVersion, serverVersion)
						: new ForbiddenException();
	}

	public HttpException getAppVersion(String appVersion) {
		return null;
	}

	public HttpException setAppVersion() {
		return null;
	}

	// public Optional<Version> getVersion() {

	// }
}
