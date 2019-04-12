/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.entityControllers.FileVersionEntityController;
import fr.alten.ambroiseJEE.security.UserRole;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class FileVersionBusinessController {

	@Autowired
	private FileVersionEntityController fileVersionEntityController;

	public String compareDataVersion(String appVersion, String serverVersion, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) ? fileVersionEntityController.compareVersionData(appVersion, serverVersion)
						: "bug";
	}

	// public Optional<Version> getVersion() {

	// }
}
