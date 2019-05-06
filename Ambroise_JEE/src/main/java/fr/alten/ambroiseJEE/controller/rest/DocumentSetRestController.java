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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.DocumentSetBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
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
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping("/admin/documentset")
	@ResponseBody
	public HttpException createDocumentSet(@RequestBody JsonNode JDocumentSet,
			@RequestAttribute("role") UserRole role) {
		return documentSetBusinessController.createDocumentSet(JDocumentSet, role);
	}

	@PutMapping("/admin/documentset")
	@ResponseBody
	public HttpException updateDocumentSet(@RequestBody JsonNode JDocumentSet,
			@RequestAttribute("role") UserRole role) {
		return documentSetBusinessController.updateDocumentSet(JDocumentSet, role);
	}

	@GetMapping("/documentset")
	@ResponseBody
	public String getDocumentSet(@RequestBody JsonNode JDocumentSet, @RequestAttribute("role") UserRole role) {
		if(!JsonUtils.checkJsonIntegrity(JDocumentSet, "name", "files")) throw new UnprocessableEntityException();
		else return gson.toJson(documentSetBusinessController.getDocumentSet(JDocumentSet, role));	
	}

	@GetMapping("/admin/documentset")
	@ResponseBody
	public String getDocumentSetAdmin(@RequestAttribute("role") UserRole role) {
		return gson.toJson(documentSetBusinessController.getDocumentSetAdmin(role));
	}
	
}
