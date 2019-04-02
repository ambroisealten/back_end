/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.AgencyBusinessController;

/**
 * Rest controller for the agency web service
 * 
 * @author Andy Chabalier
 *
 */
@Controller
public class AgencyRestController {

	@Autowired
	private AgencyBusinessController agencyBusinessController;

	private final Gson gson;

	public AgencyRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

}

