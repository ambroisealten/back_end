/**
 * 
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception sent when user isn't connected or gives bad identifiers. HTTP
 * Status : 422.
 * 
 * @author Andy Chabalier
 *
 */
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "L’entité fournie avec la requête est incompréhensible ou incomplète.")
public class UnprocessableEntityException extends HttpException {

	private static final long serialVersionUID = 900489820760681258L;

	public UnprocessableEntityException() {
		super();
	}
}
