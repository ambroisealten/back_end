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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.AgencyBusinessController;
import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
import fr.alten.ambroiseJEE.controller.business.geographic.GeographicBusinessController;
import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
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

	@Autowired
	private GeographicBusinessController geographicBusinessController;
	
	@Autowired
	private AgencyBusinessController AgencyBusinessController;

	private final Gson gson;

	public InitBaseWebService() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(value = "/admin/init")
	@ResponseBody
	public HttpException init() throws IOException {

		// peupler la base de données géographiques
		createGeographics();

		// peupler la base de données Géographiques
		createAgencies();

		// peupler la base d'utilisateurs
		createUsers();

		return new CreatedException();
	}

	/**
	 * create and populate Database with geographics
	 * 
	 * @author Andy Chabalier
	 */
	private void createGeographics() {
		geographicBusinessController.synchronise(UserRole.MANAGER_ADMIN);
	}

	/**
	 * 
	 * @author Andy Chabalier
	 * @throws IOException 
	 */
	private void createAgencies() throws IOException {
		Agency agencyStrasbourg = new Agency();
		agencyStrasbourg.setName("Strasbourg");
		JsonNode agencyStrasbourgJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyStrasbourg).getAsJsonObject());
		((ObjectNode) agencyStrasbourgJsonNode).put("place", "Schiltigheim");
		((ObjectNode) agencyStrasbourgJsonNode).put("placeType", "city");


		AgencyBusinessController.createAgency(agencyStrasbourgJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * create and populate Database with users 1 user with Manager_Admin role, 1
	 * user with CDR_Admin role, 1 user with Manager role, 1 user with CDR role,, 1
	 * user with Consultant role, 1 user who will be desactivated and 10 consultants
	 * 
	 * @throws IOException
	 * @author Andy Chabalier
	 */
	private void createUsers() throws IOException {

		User userAdminManager = new User();
		userAdminManager.setForname("tempUserAdminManager");
		userAdminManager.setMail("tempUserAdminManager@mail.com");
		userAdminManager.setName("tempUserAdminManagerName");

		User userAdminCDR = new User();
		userAdminCDR.setForname("tempUserAdminCDR");
		userAdminCDR.setMail("tempUserAdminCDR@mail.com");
		userAdminCDR.setName("tempUserAdminCDRName");

		User userManager = new User();
		userManager.setForname("tempUserManager");
		userManager.setMail("tempUserManager@mail.com");
		userManager.setName("tempUserManagerName");

		User userCDR = new User();
		userCDR.setForname("tempUserCDR");
		userCDR.setMail("tempUserCDR@mail.com");
		userCDR.setName("tempUserCDRName");

		User userConsultant = new User();
		userConsultant.setForname("tempUserConsultant");
		userConsultant.setMail("tempUserConsultant@mail.com");
		userConsultant.setName("tempUserConsultantName");

		User userDesactivated = new User();
		userDesactivated.setForname("");
		userDesactivated.setMail("desactivated" + System.currentTimeMillis());
		userDesactivated.setName("");

		JsonNode userAdminManagerJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userAdminManager).getAsJsonObject());
		((ObjectNode) userAdminManagerJsonNode).put("pswd", "pass");
		((ObjectNode) userAdminManagerJsonNode).put("role", "MANAGER_ADMIN");
		((ObjectNode) userAdminManagerJsonNode).put("agency", "");
		JsonNode userAdminCDRJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userAdminCDR).getAsJsonObject());
		((ObjectNode) userAdminCDRJsonNode).put("pswd", "pass");
		((ObjectNode) userAdminCDRJsonNode).put("role", "CDR_ADMIN");
		((ObjectNode) userAdminCDRJsonNode).put("agency", "");
		JsonNode userManagerJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userManager).getAsJsonObject());
		((ObjectNode) userManagerJsonNode).put("pswd", "pass");
		((ObjectNode) userManagerJsonNode).put("role", "MANAGER");
		((ObjectNode) userManagerJsonNode).put("agency", "");
		JsonNode userCDRJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userCDR).getAsJsonObject());
		((ObjectNode) userCDRJsonNode).put("pswd", "pass");
		((ObjectNode) userCDRJsonNode).put("role", "CDR");
		((ObjectNode) userCDRJsonNode).put("agency", "");
		JsonNode userConsultantJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userConsultant).getAsJsonObject());
		((ObjectNode) userConsultantJsonNode).put("pswd", "pass");
		((ObjectNode) userConsultantJsonNode).put("role", "CONSULTANT");
		((ObjectNode) userConsultantJsonNode).put("agency", "");
		JsonNode userDesactivatedJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userDesactivated).getAsJsonObject());
		((ObjectNode) userDesactivatedJsonNode).put("pswd", "pass");
		((ObjectNode) userDesactivatedJsonNode).put("role", "DESACTIVATED");
		((ObjectNode) userDesactivatedJsonNode).put("agency", "");

		userBusinessController.createUser(userAdminManagerJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userAdminCDRJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userManagerJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userCDRJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userConsultantJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userDesactivatedJsonNode, UserRole.MANAGER_ADMIN);

		// Remplissage d'une population de consultant
		for (int i = 0; i < 10; i++) {
			User useri = new User();
			useri.setForname("tempUserConsultant" + i);
			useri.setMail("tempUserconsultant" + i + "@mail.com");
			useri.setName("tempUserAdminName");

			JsonNode useriJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(useri).getAsJsonObject());
			((ObjectNode) useriJsonNode).put("pswd", "pass");
			((ObjectNode) useriJsonNode).put("role", "CONSULTANT");
			((ObjectNode) useriJsonNode).put("agency", "");

			userBusinessController.createUser(useriJsonNode, UserRole.MANAGER_ADMIN);
		}
	}
}