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
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;


/**
 * Rest controller for the job web service
 * 
 * @author Thomas Decamp
 *
 */
@Controller
public class JobRestController {

	@Autowired
	private JobBusinessController jobBusinessController;

	private final Gson gson;

	public JobRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/job")
	@ResponseBody
	public HttpException createJob(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? jobBusinessController.createJob(params, role)
				: new UnprocessableEntityException();
	}

	@GetMapping(value = "/jobs")
	@ResponseBody
	public String getJobs(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(jobBusinessController.getJobs(role));
	}

	@PutMapping(value = "/job")
	@ResponseBody
	public HttpException updateJob(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? jobBusinessController.createJob(params, role)
				: new UnprocessableEntityException();
	}

	@DeleteMapping(value = "/job")
	@ResponseBody
	public HttpException deleteJob(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") UserRole role) throws Exception {
		return params.get("mail") != null ? jobBusinessController.deleteJob(params, role)
				: new UnprocessableEntityException();
	}
}
