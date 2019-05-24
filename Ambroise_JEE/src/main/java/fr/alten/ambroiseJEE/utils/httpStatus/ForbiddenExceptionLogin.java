/**
*
*/
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
* Exception sent when user doesn't have the right rights to access a resource.
* HTTP Status : 403.
*
* @author Thomas Decamp
*
*/
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Requête refusée. Identifiants et/ou mot de passe incorrects")
public class ForbiddenExceptionLogin extends HttpException {

	private static final long serialVersionUID = 6861810970115266522L;

	public ForbiddenExceptionLogin() {
		super("Forbidden");
		final StackTraceElement[] newStackTrace = { new StackTraceElement(this.getClass().getSimpleName(),
				HttpStatus.FORBIDDEN.name(), "", HttpStatus.FORBIDDEN.value()) };
		setStackTrace(newStackTrace);
	}

	@Override
	public String getLocalizedMessage() {
		return "Requête refusée. Vous n'avez pas les privilèges requis";
	}
}
