/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.FileVersionBusinessController;
import fr.alten.ambroiseJEE.model.beans.File;
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
	public List<File> getAppStatut(@RequestParam("appVersion") String appVersion,
			@RequestAttribute("role") UserRole role) {
		return fileVersionBusinessController.getAppVersion(appVersion, role);
	}

	@GetMapping("/version")
	@ResponseBody
	public HttpException setAppStatut() {
		return null;
	}

	@PutMapping("/version")
	@ResponseBody
	public HttpException updateAppData() {
		return null;
	}

	@GetMapping("/version/")
	@ResponseBody
	public String getAppData(@RequestAttribute("role") UserRole role) {
		return gson.toJson(fileVersionBusinessController.setAppVersion(role));
	}

}
