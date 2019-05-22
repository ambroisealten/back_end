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
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class DocumentSetBusinessController {

	private final UserRoleLists roles = UserRoleLists.getInstance();

	@Autowired
	private DocumentSetEntityController documentSetEntityController;

	/**
	 * Document set creation on Server
	 *
	 * @param jDocumentSet the AppMobile Version
	 * @param role         the user's role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database (that mean file already exist and then it's an upload. But
	 *         no change to make in base and {@link CreatedException} if the
	 *         document is created and {@link ForbiddenException} when the role is
	 *         not correct
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException createDocumentSet(final JsonNode jDocumentSet, final UserRole role) {
		return isAdmin(role) ? this.documentSetEntityController.createDocumentSet(jDocumentSet)
				: new ForbiddenException();
	}

	/**
	 * fetch all document set for the administration page
	 *
	 * @param role the user's role
	 * @return a list with all the document set {@link ForbiddenException} when the
	 *         role is not correct
	 * @author MAQUINGHEN MAXIME
	 */
	public List<DocumentSet> getAllDocumentSet(final UserRole role) {
		if (isConnected(role)) {
			return this.documentSetEntityController.getAllDocumentSet();
		}
		throw new ForbiddenException();
	}

	/**
	 * fetch all changes of the document set
	 *
	 * @param jDocumentSet the AppMobile Version
	 * @param role         the user's role
	 * @return an HashMap with 3 List: additions, changes, deletions
	 *         {@link ResourceNotFoundException} when the name doesn't exist in the
	 *         database
	 * @author MAQUINGHEN MAXIME
	 */
	public HashMap<String, List<MobileDoc>> getDocumentSet(final JsonNode jDocumentSet, final UserRole role) {
		if (isConnected(role)) {
			return this.documentSetEntityController.getDocumentSetChanges(jDocumentSet);
		}
		throw new ForbiddenException();
	}

	/**
	 * fetch a Document set
	 *
	 * @param set  the set name to fetch
	 * @param role the current logged user's role
	 * @return the document set or {@link RessourceNotFoundException}
	 * @author Andy Chabalier
	 */
	public DocumentSet getSpecificDocumentSet(final String set, final UserRole role) {
		if (isConnected(role)) {
			return this.documentSetEntityController.getSpecificDocumentSet(set);
		}
		throw new ForbiddenException();
	}

	public boolean isAdmin(final UserRole role) {
		return this.roles.isAdmin(role);
	}

	public boolean isConnected(final UserRole role) {
		return this.roles.isNot_ConsultantOrDeactivated(role);
	}

	/**
	 * document set update on server by an admin
	 *
	 * @param jDocumentSet the AppMobile Version
	 * @param role         the user's role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database (that mean file already exist and then it's an upload. But
	 *         no change to make in base and {@link OkException} if the document
	 *         update is valid {@link RessourceNotFoundException} if the document
	 *         don't exist on the server {@link ForbiddenException} when the role is
	 *         not correct
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateDocumentSet(final JsonNode jDocumentSet, final UserRole role) {
		return isAdmin(role) ? this.documentSetEntityController.updateDocumentSet(jDocumentSet)
				: new ForbiddenException();
	}
}
