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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

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
	public HttpException init() throws IOException {
		
		//Creation d'une population de base
		
		User userAdmin = new User();
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
		userAdmin.setPswd("pass");
		userAdmin.setRole(UserRole.MANAGER_ADMIN);
		
		User userManager = new User();
		userManager.setForname("tempUserManager");
		userManager.setMail("tempUserManager@mail.com");
		userManager.setName("tempUserManagerName");
		userManager.setPswd("pass");
		userManager.setRole(UserRole.MANAGER);
		
		User userCDR = new User();
		userCDR.setForname("tempUserCDR");
		userCDR.setMail("tempUserCDR@mail.com");
		userCDR.setName("tempUserCDRName");
		userCDR.setPswd("pass");
		userCDR.setRole(UserRole.CDR);
		
		User userConsultant = new User();
		userConsultant.setForname("tempUserConsultant");
		userConsultant.setMail("tempUserConsultant@mail.com");
		userConsultant.setName("tempUserConsultantName");
		userConsultant.setPswd("pass");
		userConsultant.setRole(UserRole.CONSULTANT);
		
		User userDesactivated = new User();
		userDesactivated.setForname("");
		userDesactivated.setMail("desactivated" + System.currentTimeMillis());
		userDesactivated.setName("");
		userDesactivated.setPswd("");
		userDesactivated.setRole(UserRole.DESACTIVATED);
		
		JsonNode userAdminJsonNode = toJsonNode(gson.toJsonTree(userAdmin).getAsJsonObject());
		((ObjectNode) userAdminJsonNode).put("pswd", "pass");
		((ObjectNode) userAdminJsonNode).put("role", "MANAGER_ADMIN");
		JsonNode userManagerJsonNode = toJsonNode(gson.toJsonTree(userManager).getAsJsonObject());
		((ObjectNode) userManagerJsonNode).put("pswd", "pass");
		((ObjectNode) userManagerJsonNode).put("role", "MANAGER");
		JsonNode userCDRJsonNode = toJsonNode(gson.toJsonTree(userCDR).getAsJsonObject());
		((ObjectNode) userCDRJsonNode).put("pswd", "pass");
		((ObjectNode) userCDRJsonNode).put("role", "CDR");
		JsonNode userConsultantJsonNode = toJsonNode(gson.toJsonTree(userConsultant).getAsJsonObject());
		((ObjectNode) userConsultantJsonNode).put("pswd", "pass");
		((ObjectNode) userConsultantJsonNode).put("role", "CONSULTANT");
		JsonNode userDesactivatedJsonNode = toJsonNode(gson.toJsonTree(userDesactivated).getAsJsonObject());
		((ObjectNode) userDesactivatedJsonNode).put("pswd", "pass");
		((ObjectNode) userDesactivatedJsonNode).put("role", "DESACTIVATED");

		userBusinessController.createUser(userAdminJsonNode,UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userManagerJsonNode,UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userCDRJsonNode,UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userConsultantJsonNode,UserRole.MANAGER_ADMIN);
		
		userBusinessController.createUser(userDesactivatedJsonNode,UserRole.MANAGER_ADMIN);
		
		//Remplissage d'une population de consultant
		for(int i = 0; i<10 ; i++) {
			User useri = new User();
			useri.setForname("tempUserConsultant"+i);
			useri.setMail("tempUserconsultant"+i+"@mail.com");
			useri.setName("tempUserAdminName");
			useri.setPswd("pass");
			useri.setRole(UserRole.CONSULTANT);
			
			JsonNode useriJsonNode = toJsonNode(gson.toJsonTree(useri).getAsJsonObject());
			((ObjectNode) useriJsonNode).put("pswd", "pass");
			((ObjectNode) useriJsonNode).put("role", "DESACTIVATED");

			userBusinessController.createUser(useriJsonNode,UserRole.MANAGER_ADMIN);		}
		return new CreatedException();
	}
	
	
	private JsonNode toJsonNode(JsonObject jsonObj) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readTree(jsonObj.toString());
	}
}