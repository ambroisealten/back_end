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
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}
	@PostMapping("/job")
	@ResponseBody
	public HttpException createJob(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
		return jobBusinessController.createJob(params, role);
	}

	@DeleteMapping("/job")
	@ResponseBody
	public HttpException deleteJob(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
		return jobBusinessController.deleteJob(params, role);
	}

	@GetMapping("/jobs")
	@ResponseBody
	public String getJobs(@RequestAttribute("role") UserRole role) {
		return gson.toJson(jobBusinessController.getJobs(role));
	}

	@PutMapping("/job")
	@ResponseBody
	public HttpException updateJob(@RequestBody JsonNode params, @RequestAttribute("role") UserRole role) {
		return jobBusinessController.updateJob(params, role);
	}
}
