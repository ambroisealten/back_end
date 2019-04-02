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

import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Controller
public class AdminRestController {

	@Autowired
	private UserBusinessController userBusinessController;

	private final Gson gson;

	public AdminRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * 
	 * @param params
	 * @param mail
	 * @param role
	 * @return
	 * @throws Exception
	 * @author MAQUINGHEN MAXIME
	 */
	@PostMapping(value = "/admin/user")
	@ResponseBody
	public HttpException createUser(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? userBusinessController.createUser(params, role)
				: new UnprocessableEntityException();
	}

	@GetMapping(value = "/admin/users")
	@ResponseBody
	public String getUsers(@RequestAttribute("mail") String mail, @RequestAttribute("role") int role) {
		return gson.toJson(userBusinessController.getUsers(role));
	}

	@PutMapping(value = "/admin/user")
	@ResponseBody
	public HttpException updateUser(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? userBusinessController.updateUser(params, role)
				: new UnprocessableEntityException();
	}

	@DeleteMapping(value = "/admin/user")
	@ResponseBody
	public HttpException deleteUser(@RequestBody JsonNode params, @RequestAttribute("mail") String mail,
			@RequestAttribute("role") int role) throws Exception {
		return params.get("mail") != null ? userBusinessController.deleteUser(params, role)
				: new UnprocessableEntityException();
	}

}
