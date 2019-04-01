/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
import fr.alten.ambroiseJEE.security.JWTokenUtility;
import fr.alten.ambroiseJEE.security.Token;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;

/**
 * Rest controller for the login web service
 * 
 * @author Andy Chabalier
 *
 */
@Controller
public class LoginRestController {

	@Autowired
	private UserBusinessController userBusinessController;

	private final Gson gson;

	public LoginRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * Authenticate user. HTTP Method : POST.
	 * 
	 * @param params JsonNode containing post parameters from http request : mail &
	 *               password
	 * @return String containing the Json formatted JWToken
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 */
	@PostMapping(value = "/login")
	@ResponseBody
	public String login(@RequestBody JsonNode params) throws Exception {

		String mail = params.get("mail").textValue();
		String pswd = params.get("pswd").textValue();

		Optional<String> subject = userBusinessController.checkIfCredentialIsValid(mail, pswd);
		if (subject.isPresent()) {
			// Si un sujet est present, alors l'utilisateur existe bien. On construit son
			// token
			Token jsonResponse = JWTokenUtility.buildJWT(subject.get());
			return gson.toJson(jsonResponse);
		}
		throw new ForbiddenException();
	}
}
