/**
 * 
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception sent when an internal error occur
 * HTTP Status : 500.
 * 
 * @author Andy Chabalier
 * 
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Erreur interne du serveur")
public class InternalServerErrorException extends HttpException {

	private static final long serialVersionUID = 6695487880532171802L;

	public InternalServerErrorException() {
		super();
	}
}
