/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.security.UserRole;

/**
 * Rest Controller for User Administration !!!! ONLY FOR DEVELOPPMENT !!!!
 * 
 * @author Andy Chabalier
 *
 */
@Controller
public class InitBaseWebService {

	@Autowired
	private UserBusinessController userBusinessController;

	private final Gson gson;

	public InitBaseWebService() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/admin/init")
	@ResponseBody
	public String init() {
		
		User userAdmin = new User();
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mai.com");
		userAdmin.setName("tempUserAdminName");
		userAdmin.setPswd("pass");
		userAdmin.setRole(UserRole.MANAGER_ADMIN);
		
		User userAdmin = new User();
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mai.com");
		userAdmin.setName("tempUserAdminName");
		userAdmin.setPswd("pass");
		userAdmin.setRole(UserRole.MANAGER_ADMIN);
		
		User userAdmin = new User();
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mai.com");
		userAdmin.setName("tempUserAdminName");
		userAdmin.setPswd("pass");
		userAdmin.setRole(UserRole.MANAGER_ADMIN);
		
		User userAdmin = new User();
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mai.com");
		userAdmin.setName("tempUserAdminName");
		userAdmin.setPswd("pass");
		userAdmin.setRole(UserRole.MANAGER_ADMIN);
		
		User userAdmin = new User();
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mai.com");
		userAdmin.setName("tempUserAdminName");
		userAdmin.setPswd("pass");
		userAdmin.setRole(UserRole.MANAGER_ADMIN);
		
		userBusinessController.createUser(toJsonNode(jsonUser),UserRole.MANAGER_ADMIN);
		return null;
	}
	
	
	private JsonNode toJsonNode(JsonObject jsonObj) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readTree(jsonObj.toString());
	}
}
