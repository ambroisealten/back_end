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
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Une authentification est nécessaire pour accéder à la resource.")
public class UnauthorizedException extends HttpException {

	private static final long serialVersionUID = 900489820760681258L;

	public UnauthorizedException() {
		super("Unauthorized");
		final StackTraceElement[] newStackTrace = { new StackTraceElement(this.getClass().getSimpleName(),
				HttpStatus.UNAUTHORIZED.name(), "", HttpStatus.UNAUTHORIZED.value()) };
		setStackTrace(newStackTrace);
	}

	@Override
	public String getLocalizedMessage() {
		return "Accès refusé. Une authentification est nécessaire pour accéder à la resource.";
	}
}
