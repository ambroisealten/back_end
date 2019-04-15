/**
 * 
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception sent when the ressource is not found.
 * HTTP Status : 404.
 * 
 * @author MAQUINGHEN MAXIME
 * 
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "La ressource demandée n'a pas été trouvée")
public class RessourceNotFoundException extends HttpException {

	private static final long serialVersionUID = 6861810970115266522L;

	public RessourceNotFoundException() {
		super();
	}
	
	public RessourceNotFoundException(String message) {
		super(message);
	}
}
