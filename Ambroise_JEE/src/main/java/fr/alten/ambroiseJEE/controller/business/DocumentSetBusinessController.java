/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.entityControllers.DocumentSetEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class DocumentSetBusinessController {

	@Autowired
	private DocumentSetEntityController documentSetEntityController;

	public HttpException createDocumentSet(JsonNode jDocumentSet, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) ? documentSetEntityController.createDocumentSet(jDocumentSet)
						: new ForbiddenException();
	}

	public HttpException updateDocumentSet(JsonNode jDocumentSet, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) ? documentSetEntityController.updateDocumentSet(jDocumentSet)
						: new ForbiddenException();
	}

//	/**
//	 * Update files Data Version
//	 * 
//	 * @param appVersion    the App Version data
//	 * @param serverVersion the Server Version data
//	 * @param role          the user role
//	 * @return {@link OkException} When the comparison is complete
//	 *         {@link ForbiddenException} when the role is not authorize to access
//	 *         this data
//	 * @author MAQUINGHEN MAXIME
//	 */
//	public HttpException updateDataVersion(JsonNode appVersion, UserRole role) {
//		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
//				|| UserRole.CDR == role) ? documentSetEntityController.updateAppDataVersion(appVersion)
//						: new ForbiddenException();
//	}
//
//	/**
//	 * Get AppVersion of the documents
//	 * 
//	 * @param appVersion the version of document of the mobile app
//	 * @param role       role of the user
//	 * @return return the list of documents or {@link ForbiddenException} when the
//	 *         role is not authorized to access to this data
//	 * @author MAQUINGHEN MAXIME
//	 */
//	public List<DocumentSet> getAppVersion(String appVersion, UserRole role) {
//		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
//				|| UserRole.CDR == role) {
//			return documentSetEntityController.getVersionData();
//		}
//		throw new ForbiddenException();
//	}
//
//	/**
//	 * Get the version and the file version
//	 * 
//	 * @param role the user role
//	 * @return the List of all the file {@link ForbiddenException}
//	 * @author MAQUINGHEN MAXIME
//	 */
//	public List<DocumentSet> setAppVersion(UserRole role) {
//		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
//				|| UserRole.CDR == role) {
//			return documentSetEntityController.getVersionData();
//		}
//		throw new ForbiddenException();
//	}
//
//	public HashMap<String, List<String>> synchroAppMobile(JsonNode appVersion, UserRole role) {
//		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
//				|| UserRole.CDR == role) {
//			return documentSetEntityController.compareVersionData(appVersion);
//		}
//		throw new ForbiddenException();
//	}
//
//	public HttpException compareDataVersion(JsonNode params, UserRole role) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public HttpException newFileVersion(JsonNode params, UserRole role) {
//		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
//				|| UserRole.CDR == role) {
//			return documentSetEntityController.newFileVersion(params);
//		}
//		throw new ForbiddenException();
//	}
//
//	// public Optional<Version> getVersion() {
//
//	// }
}
