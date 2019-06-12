/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jose4j.jwt.consumer.InvalidJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

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

	@Autowired
	private ApplicationContext ctx;

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
	@ResponsePayload
	public String login(@RequestBody final JsonNode params, final HttpServletResponse httpServletResponse)
			throws Exception {
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
			this.userBusinessController.saveRefreshToken(mail, refreshToken);

			// create a cookie
			final Cookie cookie = new Cookie("refreshToken", refreshToken.getToken());
			final String maxAge = stayConnected
					? this.ctx.getEnvironment().getProperty("security.cookie.token.refresh.noExpirationTime")
					: this.ctx.getEnvironment().getProperty("security.cookie.token.refresh.expirationTime");
			cookie.setMaxAge(Integer.parseInt(maxAge)); // convert maxAge to second (was mili)
			cookie.setSecure(Boolean
					.parseBoolean(this.ctx.getEnvironment().getProperty("security.cookie.token.refresh.secure")));
			cookie.setHttpOnly(Boolean
					.parseBoolean(this.ctx.getEnvironment().getProperty("security.cookie.token.refresh.httpOnly")));
			cookie.setPath(this.ctx.getEnvironment().getProperty("security.cookie.token.refresh.path"));

			// add cookie to response
			httpServletResponse.addCookie(cookie);

			return this.gson.toJson(accessToken);
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
	public String refreshAcesstoken(@CookieValue(value = "refreshToken") final String refreshTokenCookie,
			final HttpServletRequest request) throws Exception {
		try {
			final String token = refreshTokenCookie;
			final String[] tokenInfo = JWTokenUtility.validate(token).split("\\|");
			final String mail = tokenInfo[0];
			final UserRole role = UserRole.valueOf(tokenInfo[1]);
			final User user = this.userBusinessController.getUserByMail(mail, UserRole.MANAGER_ADMIN);
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
	public HttpException signout(@CookieValue(value = "refreshToken") final String refreshTokenCookie,
			final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
		try {
			final String[] tokenInfo = JWTokenUtility.validate(refreshTokenCookie).split("\\|");
			final String mail = tokenInfo[0];
			for (final Cookie c : httpServletRequest.getCookies()) {
				if (c.getName().equals("refreshToken")) {
					c.setMaxAge(0);
					httpServletResponse.addCookie(c);
				}
			}
			return this.userBusinessController.logout(mail);
		} catch (final InvalidJwtException e) {
			return new UnauthorizedException();
		}
	}
}
