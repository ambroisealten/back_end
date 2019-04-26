/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.JobBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Rest controller for the job web service
 *
 * @author Lucas Royackkers
 *
 */
@Controller
public class JobRestController {

	@Autowired
	private JobBusinessController jobBusinessController;

	private final Gson gson;

	public JobRestController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Create a Job
	 *
	 * @param params the JsonNode containing all Job parameters (only its title, e.g
	 *               title = "Ingénieur système")
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database, {@link UnprocessableEntityException} if there are not
	 *         enough parameters to perform the action and {@link CreatedException}
	 *         if the job is created
	 * @author Lucas Royackkers
	 */
	@PostMapping("/job")
	@ResponseBody
	public HttpException createJob(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role) {
		return params.get("title") != null ? this.jobBusinessController.createJob(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Delete a Job
	 *
	 * @param params the JsonNode containing all Job parameters (only its title)
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database, {@link UnprocessableEntityException} if there are not
	 *         enough parameters to perform the action and {@link OkException} if
	 *         the job is deleted
	 * @author Lucas Royackkers
	 */
	@DeleteMapping("/job")
	@ResponseBody
	public HttpException deleteJob(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role) {
		return params.get("title") != null ? this.jobBusinessController.deleteJob(params, role)
				: new UnprocessableEntityException();
	}

	/**
	 * Get all Jobs
	 * 
	 * @param role the current logged user's role
	 * @return a String representing a JsonNode with all jobs contained in the
	 *         database (can be empty)
	 * @author Lucas Royackkers
	 */
	@GetMapping("/jobs")
	@ResponseBody
	public String getJobs(@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.jobBusinessController.getJobs(role));
	}

	/**
	 * Update a Job
	 *
	 * @param params the JsonNode containing all Job parameters (title and oldTitle)
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database, {@link UnprocessableEntityException} if there are not
	 *         enough parameters to perform the action and {@link OkException} if
	 *         the job is updated
	 * @author Lucas Royackkers
	 */
	@PutMapping("/job")
	@ResponseBody
	public HttpException updateJob(@RequestBody final JsonNode params, @RequestAttribute("role") final UserRole role) {
		return params.get("title") != null && params.get("oldTitle") != null
				? this.jobBusinessController.updateJob(params, role)
				: new UnprocessableEntityException();
	}
}
