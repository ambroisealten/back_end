/**
 * 
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception sent when the resource is not found.
 * HTTP Status : 404.
 * 
 * @author MAQUINGHEN MAXIME
 * 
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "La resource demandée n'a pas été trouvée")
public class RessourceNotFoundException extends HttpException {

	private static final long serialVersionUID = 6861810970115266522L;

	public RessourceNotFoundException() {
		super("Not Found.");
		StackTraceElement[] newStackTrace = {new StackTraceElement(this.getClass().getSimpleName(), HttpStatus.NOT_FOUND.name(), "", HttpStatus.NOT_FOUND.value())};
		this.setStackTrace(newStackTrace);
	}

	
	@Override
	public String getLocalizedMessage() {
		return "La ressource demandée n'a pas été trouvée";
	}
}
