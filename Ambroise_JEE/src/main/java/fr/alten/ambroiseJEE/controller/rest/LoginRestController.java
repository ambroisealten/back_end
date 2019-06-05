/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.jose4j.jwt.consumer.InvalidJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.security.JWTokenUtility;
import fr.alten.ambroiseJEE.security.Token;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnauthorizedException;
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
			final boolean stayConnected = params.get("stayConnected").asBoolean(false);

			final String subject = this.userBusinessController.checkIfCredentialIsValid(mail, pswd)
					.orElseThrow(ForbiddenException::new);
			// Si un sujet est present, alors l'utilisateur existe bien. On construit son
			// token d'acces aux ressources
			final Token accessToken = JWTokenUtility.buildAccessJWT(subject);
			// Et on construit son token de rafraichissement
			final Token refreshToken = JWTokenUtility.buildRefreshJWT(subject, stayConnected);
			userBusinessController.saveRefreshToken(mail, refreshToken);
			final HashMap<String, Token> response = new HashMap<String, Token>();
			response.put("access", accessToken);
			response.put("refresh", refreshToken);
			return this.gson.toJson(response);
		} catch (final NullPointerException npe) {
			throw new UnprocessableEntityException();
		}
	}

	/**
	 * Authenticate user. HTTP Method : Get. this method ask for
	 *
	 * @param request the servlet request
	 * @return String containing the Json formatted JWToken
	 * @throws Exception @see ForbiddenException if wrong identifiers and
	 *                   {@link UnauthorizedException} if the refresh token is
	 *                   invalid
	 */
	@GetMapping(value = "/login")
	@ResponseBody
	public String refreshAcesstoken(HttpServletRequest request) throws Exception {
		try {
			final String token = request.getHeader("authorization");
			final String[] tokenInfo = JWTokenUtility.validate(token).split("\\|");
			String mail = tokenInfo[0];
			UserRole role = UserRole.valueOf(tokenInfo[1]);
			User user = this.userBusinessController.getUserByMail(mail, UserRole.MANAGER_ADMIN);
			if (user.getRole().equals(role) && user.getRefreshToken().getToken().equals(token)) {
				return this.gson.toJson(JWTokenUtility.buildAccessJWT(user.getMail() + "|" + user.getRole().name()));
			} else {
				throw new ForbiddenException();
			}
		} catch (final InvalidJwtException e) {
			throw new UnauthorizedException();
		}
	}

	/**
	 * logout user. HTTP Method : POST.
	 *
	 * @return {@link OkException} if the user is logged out
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 */
	@PostMapping(value = "/signout")
	@ResponseBody
	public HttpException signout(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.userBusinessController.logout(mail);
	}
}
