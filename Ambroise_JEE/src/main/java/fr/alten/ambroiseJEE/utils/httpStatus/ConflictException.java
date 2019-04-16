/**
 *
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception sent if conflicts when entity already exists in database. HTTP
 * Status : 409.
 *
 * @authors Andy Chabalier
 *
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Conflit, clé déjà existante")
public class ConflictException extends HttpException {

	private static final long serialVersionUID = -7105156100966441173L;

	public ConflictException() {
		super("Conflict");
		StackTraceElement[] newStackTrace = { new StackTraceElement(this.getClass().getSimpleName(),
				HttpStatus.CONFLICT.name(), "", HttpStatus.CONFLICT.value()) };
		this.setStackTrace(newStackTrace);
	}

	@Override
	public String getLocalizedMessage() {
		return "Conflit, clé déjà existante";
	}

}
