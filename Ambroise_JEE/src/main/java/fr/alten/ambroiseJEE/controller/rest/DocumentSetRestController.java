/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.DocumentSetBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

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
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping("/documentset")
	@ResponseBody
	public HttpException createDocumentSet(@RequestBody JsonNode JDocumentSet,
			@RequestAttribute("role") UserRole role) {
		return documentSetBusinessController.createDocumentSet(JDocumentSet, role);
	}

	@PutMapping("/documentset")
	@ResponseBody
	public HttpException updateDocumentSet(@RequestBody JsonNode JDocumentSet,
			@RequestAttribute("role") UserRole role) {
		return documentSetBusinessController.updateDocumentSet(JDocumentSet, role);
	}
//	/**
//	 * 
//	 * @param appVersion
//	 * @param role
//	 * @return
//	 * @author MAQUINGHEN MAXIME
//	 */
//	@PostMapping("/version")
//	@ResponseBody
//	public HashMap<String, List<String>> getAppStatut(@RequestBody JsonNode appVersion,
//			@RequestAttribute("role") UserRole role) {
//		return documentSetBusinessController.synchroAppMobile(appVersion, role);
//	}
//
//	@PostMapping("/fileversion")
//	@ResponseBody
//	public HttpException addFile(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
//		return documentSetBusinessController.newFileVersion(params, role);
//	}
//	
//	@GetMapping("/version")
//	@ResponseBody
//	public List<DocumentSet> setAppStatut(@RequestAttribute("role") UserRole role) {
//		return documentSetBusinessController.setAppVersion(role);
//	}
//	
//	@PutMapping("/version")
//	@ResponseBody
//	public HttpException updateAppData(@RequestBody JsonNode appVersion,
//			@RequestAttribute("role") UserRole role) {
//		return documentSetBusinessController.updateDataVersion(appVersion, role);
//	}
//
//	@GetMapping("/appversion")
//	@ResponseBody
//	public String getAppData(@RequestAttribute("role") UserRole role) {
//		return gson.toJson(documentSetBusinessController.setAppVersion(role));
//	}

}
