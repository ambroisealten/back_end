package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.stereotype.Controller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Rest Controller for Applicant
 * 
 * @author Lucas Royackkers
 *
 */
@Controller
public class ApplicantRestController {
	
	private final Gson gson;
	
	public ApplicantRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}
	
}
