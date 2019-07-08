/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.DocumentSetBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@RestController
public class DocumentSetRestController {

	@Autowired
	private DocumentSetBusinessController documentSetBusinessController;

	private final Gson gson;

	public DocumentSetRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Check the json Integrity
	 *
	 * @param params ...
	 * @param fields ...
	 * @return the JsonIntegrity
	 * @author MAQUINGHEN MAXIME
	 */
	public boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}

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
	@PostMapping("/admin/documentset")
	@ResponseBody
	public HttpException createDocumentSet(@RequestBody final JsonNode JDocumentSet,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(JDocumentSet, "name", "files")
				? this.documentSetBusinessController.createDocumentSet(JDocumentSet, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Fetch all changes of the document set
	 *
	 * @param jDocumentSet the AppMobile Version
	 * @param role         the user's role
	 * @return the Json demand with 3 List: additions, changes, deletions
	 * @author MAQUINGHEN MAXIME
	 */
	@GetMapping("/documentset")
	@ResponseBody
	public String getDocumentSet(@RequestBody final JsonNode JDocumentSet,
			@RequestAttribute("role") final UserRole role) {
		if (!checkJsonIntegrity(JDocumentSet, "name", "files")) {
			throw new UnprocessableEntityException();
		} else {
			return this.gson.toJson(this.documentSetBusinessController.getDocumentSet(JDocumentSet, role));
		}
	}

	/**
	 * fetch all document set for the administration page
	 *
	 * @param role the user's role
	 * @return the Json ask
	 * @author MAQUINGHEN MAXIME
	 */
	@GetMapping("/admin/documentset/all")
	@ResponseBody
	public String getDocumentSetAdmin(@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.documentSetBusinessController.getAllDocumentSet(role));
	}

	/**
	 * fetch a Document set
	 *
	 * @param set  the set name to fetch
	 * @param role the current logged user's role
	 * @return the document set or {@link RessourceNotFoundException}
	 * @author Andy Chabalier
	 */
	@GetMapping("/admin/documentset")
	@ResponseBody
	public String getSpecificDocumentSet(@RequestParam("set") final String set,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.documentSetBusinessController.getSpecificDocumentSet(set, role));
	}

	/**
	 * Update the document Set on the server
	 *
	 * @param jDocumentSet the AppMobile Version
	 * @param role         the user's role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database (that mean file already exist and then it's an upload. But
	 *         no change to make in base and {@link OkException} if the document
	 *         update is valid {@link RessourceNotFoundException} if the document
	 *         don't exist on the server
	 * @author MAQUINGHEN MAXIME
	 */
	@PutMapping("/admin/documentset")
	@ResponseBody
	public HttpException updateDocumentSet(@RequestBody final JsonNode JDocumentSet,
			@RequestAttribute("role") final UserRole role) {
		return checkJsonIntegrity(JDocumentSet, "name", "oldName")
				? this.documentSetBusinessController.updateDocumentSet(JDocumentSet, role)
				: new UnprocessableEntityException();
	}
}
