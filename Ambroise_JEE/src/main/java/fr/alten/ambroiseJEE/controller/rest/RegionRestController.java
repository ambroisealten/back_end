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

import fr.alten.ambroiseJEE.controller.business.geographic.RegionBusinessController;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
* Rest controller for the Region web service
* 
* @author Andy Chabalier
*
*/
@Controller
public class RegionRestController {

	@Autowired
	private RegionBusinessController regionBusinessController;

	private final Gson gson;

	public RegionRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/region")
	@ResponseBody
	public HttpException createRegion(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? regionBusinessController.createRegion(params, role)
				: new UnprocessableEntityException();
	}

	@GetMapping(value = "/regions")
	@ResponseBody
	public String getRegions(@RequestAttribute("mail") String mail, @RequestAttribute("role") int role) {
		return gson.toJson(regionBusinessController.getRegions(role));
	}

	@PutMapping(value = "/region")
	@ResponseBody
	public HttpException updateRegion(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? regionBusinessController.createRegion(params, role)
				: new UnprocessableEntityException();
	}

	@DeleteMapping(value = "/region")
	@ResponseBody
	public HttpException deleteRegion(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? regionBusinessController.deleteRegion(params, role)
				: new UnprocessableEntityException();
	}
}


