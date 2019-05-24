/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

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
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenExceptionLogin;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

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
		final GsonBuilder builder = new GsonBuilder();
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
	public String login(@RequestBody final JsonNode params) throws Exception {
		try {
			final String mail = params.get("mail").textValue();
			final String pswd = params.get("pswd").textValue();

			final String subject = this.userBusinessController.checkIfCredentialIsValid(mail, pswd)
					.orElseThrow(ForbiddenExceptionLogin::new);
			// Si un sujet est present, alors l'utilisateur existe bien. On construit son
			// token
			final Token jsonResponse = JWTokenUtility.buildJWT(subject);
			return this.gson.toJson(jsonResponse);
		} catch (final NullPointerException npe) {
			throw new UnprocessableEntityException();
		}
	}
}
