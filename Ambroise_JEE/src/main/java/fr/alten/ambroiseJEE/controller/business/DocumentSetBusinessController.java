/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.DocumentSet;
import fr.alten.ambroiseJEE.model.beans.mobileDoc.MobileDoc;
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

	public HashMap<String, List<MobileDoc>> getDocumentSet(JsonNode jDocumentSet, UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) {
			return documentSetEntityController.getDocumentSetChanges(jDocumentSet);
		}
		throw new ForbiddenException();
	}

	public List<DocumentSet> getAllDocumentSet(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return documentSetEntityController.getAllDocumentSet();
		}
		throw new ForbiddenException();
	}

	/**
	 * fetch a Document set
	 * 
	 * @param set  the set name to fetch
	 * @param role the current logged user's role
	 * @return the document set
	 * @author Andy Chabalier
	 */
	public DocumentSet getSpecificDocumentSet(String set, UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return documentSetEntityController.getSpecificDocumentSet(set);
		}
		throw new ForbiddenException();
	}
}
