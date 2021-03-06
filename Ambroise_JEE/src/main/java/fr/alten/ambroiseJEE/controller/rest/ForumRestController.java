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
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
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
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	public boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		return JsonUtils.checkJsonIntegrity(params, fields);
	}

	/**
	 * Create a new forum
	 *
	 * @param params JsonNode containing put parameters from http request : name,
	 *               date, place
	 * @param role   the user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link UnprocessableEntityException} if the resource is not found
	 *         and {@link OkException} if the forum is create successfully
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@PostMapping(value = "/forum")
	@ResponseBody
	public HttpException createForum(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role)
			throws Exception {
		return checkJsonIntegrity(params, "name", "date", "place")
				? this.forumBusinessController.createForum(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Delete a forum
	 *
	 * @param params contain the forum name, date, place
	 * @param role   the user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link UnprocessableEntityException}) when the forum cannot
	 *         be found ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the forum is deleted
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@DeleteMapping(value = "/forum")
	@ResponseBody
	public HttpException deleteForum(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role)
			throws Exception {
		return checkJsonIntegrity(params, "name", "date", "place")
				? this.forumBusinessController.deleteForum(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Get a forum
	 *
	 * @param name  the forum name
	 * @param date  the forum data
	 * @param place the forum place
	 * @param role  the user role
	 * @return the forum data
	 * @author MAQUINGHEN MAXIME
	 */
	@GetMapping(value = "/forum/{name}/{date}/{place}")
	@ResponseBody
	public String getForum(@PathVariable final String name, @PathVariable final String date,
			@PathVariable final String place, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.forumBusinessController.getForum(name, date, place, role));
	}

	/**
	 * get the list of all forums
	 *
	 * @param role the user role
	 * @return the forum list
	 * @author MAQUINGHEN MAXIME
	 */
	@GetMapping(value = "/forums")
	@ResponseBody
	public String getForums(@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.forumBusinessController.getForums(role));
	}

	/**
	 * Update a forum
	 *
	 * @param params contain the forum name, date, place
	 * @param role   the user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link UnprocessableEntityException}) when the forum cannot
	 *         be found ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the forum is updated
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@PutMapping(value = "/forum")
	@ResponseBody
	public HttpException updateForum(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role)
			throws Exception {
		return checkJsonIntegrity(params, "name", "date", "place")
				? this.forumBusinessController.updateForum(params, role)
				: new UnprocessableEntityException();
	}
}
