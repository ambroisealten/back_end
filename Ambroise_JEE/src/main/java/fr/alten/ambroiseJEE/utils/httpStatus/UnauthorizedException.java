/**
 * 
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception sent when user isn't connected or gives bad identifiers. HTTP
 * Status : 401.
 * 
 * @author Andy Chabalier
 *
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Accès refusé. Vous n'êtes pas connecté ou les identifiants sont incorrects")
public class UnauthorizedException extends HttpException {

	private static final long serialVersionUID = 900489820760681258L;

	public UnauthorizedException() {
		super();
	}
}
