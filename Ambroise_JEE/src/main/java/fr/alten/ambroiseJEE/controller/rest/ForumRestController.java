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
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest Controller for Forum objects
 * 
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
	 * @param role   the current logged user's role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ConflictException} if the resource cannot be create,
	 *         {@link InternalServerErrorException} if there is another exception
	 *         encountered, {@link CreatedException} if the forum is created,
	 *         {@link ForbiddenException} if the logged user hasn't the rights to
	 *         perform this action or {@link UnprocessableEntityException} if all
	 *         the required parameters aren't there
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
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource cannot be
	 *         found, {@link InternalServerErrorException} if there is another
	 *         exception encountered, {@link OkException} if the forum is deleted,
	 *         {@link ForbiddenException} if the logged user hasn't the rights to
	 *         perform this action or {@link UnprocessableEntityException} if all
	 *         the required parameters aren't there
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
	 * @param role  the current logged user's role
	 * @return A JsonNode as a String, containing the specific Forum
	 * @author MAQUINGHEN MAXIME
	 */
	@GetMapping(value = "/forum/{name}/{date}/{place}")
	@ResponseBody
	public String getForum(@PathVariable final String name, @PathVariable final String date,
			@PathVariable final String place, @RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.forumBusinessController.getForum(name, date, place, role));
	}

	/**
	 * Get the list of all forums
	 *
	 * @param role the current logged user's role
	 * @return the forum list (can be empty)
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
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource cannot be
	 *         found, {@link ConflictException} if there is a duplicate in the
	 *         database, {@link InternalServerErrorException} if there is another
	 *         exception encountered, {@link OkException} if the forum is updated,
	 *         {@link ForbiddenException} if the logged user hasn't the rights to
	 *         perform this action or {@link UnprocessableEntityException} if all
	 *         the required parameters aren't there
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
