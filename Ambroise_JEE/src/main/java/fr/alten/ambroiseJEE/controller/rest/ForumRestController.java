/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.ForumBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Controller
public class ForumRestController {

	@Autowired
	private ForumBusinessController forumBusinessController;

	private final Gson gson;

	public ForumRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Create a new forum
	 * 
	 * @param params JsonNode containing put parameters from http request : name,
	 *               date, place
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link UnprocessableEntityException} if the ressource is not found
	 *         and {@link OkException} if the forum is create successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@PostMapping(value = "/forum")
	@ResponseBody
	public HttpException createForum(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role)
			throws Exception {
		return params.get("name") != null && params.get("date") != null && params.get("place") != null
				? forumBusinessController.createForum(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * get the list of all forums
	 * 
	 * @param role role of the user
	 * @return the list of all users
	 * @author MAQUINGHEN MAXIME
	 */
	@GetMapping(value = "/forums")
	@ResponseBody
	public String getForums(@RequestAttribute("role") UserRole role) {
		return gson.toJson(forumBusinessController.getForums(role));
	}

	/**
	 * Get a forum by name, date, place
	 * 
	 * @param id   the Forum unique ID
	 * @param role the role of the user
	 * @return
	 * @author MAQUINGHEN MAXIME
	 */
	@GetMapping(value = "/forum/{name}/{date}/{place}")
	@ResponseBody
	public String getForum(@PathVariable String name, @PathVariable String date, @PathVariable String place, @RequestAttribute("role") UserRole role) {
		return gson.toJson(forumBusinessController.getForum(name, date, place,role));
	}

	@PutMapping(value = "/forum")
	@ResponseBody
	public HttpException updateForum(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role)
			throws Exception {
		return params.get("name") != null ? forumBusinessController.updateForum(params, role)
				: new UnprocessableEntityException();
	}

	@DeleteMapping(value = "/forum")
	@ResponseBody
	public HttpException deleteForum(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role)
			throws Exception {
		return params.get("name") != null ? forumBusinessController.deleteForum(params, role)
				: new UnprocessableEntityException();
	}
}
