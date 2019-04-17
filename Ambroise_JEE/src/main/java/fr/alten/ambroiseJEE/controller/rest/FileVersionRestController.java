/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.util.HashMap;
import java.util.List;

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

import fr.alten.ambroiseJEE.controller.business.FileVersionBusinessController;
import fr.alten.ambroiseJEE.model.beans.FileVersion;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@RestController
public class FileVersionRestController {

	@Autowired
	private FileVersionBusinessController fileVersionBusinessController;

	private final Gson gson;

	public FileVersionRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * 
	 * @param appVersion
	 * @param role
	 * @return
	 * @author MAQUINGHEN MAXIME
	 */
	@PostMapping("/version")
	@ResponseBody
	public HashMap<String, List<String>> getAppStatut(@RequestBody JsonNode appVersion,
			@RequestAttribute("role") UserRole role) {
		return fileVersionBusinessController.synchroAppMobile(appVersion, role);
	}

	@PostMapping("/fileversion")
	@ResponseBody
	public HttpException addFile(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
		return fileVersionBusinessController.newFileVersion(params, role);
	}
	
	@GetMapping("/version")
	@ResponseBody
	public List<FileVersion> setAppStatut(@RequestAttribute("role") UserRole role) {
		return fileVersionBusinessController.setAppVersion(role);
	}

	@PutMapping("/version")
	@ResponseBody
	public HttpException updateAppData(@RequestParam("appVersion") String appVersion,
			@RequestAttribute("role") UserRole role) {
		return null;
	}

	@GetMapping("/appversion")
	@ResponseBody
	public String getAppData(@RequestAttribute("role") UserRole role) {
		return gson.toJson(fileVersionBusinessController.setAppVersion(role));
	}

}
