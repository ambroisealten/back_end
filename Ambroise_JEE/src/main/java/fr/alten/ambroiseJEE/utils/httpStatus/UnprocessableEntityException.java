/**
 *
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final Logger logger = LoggerFactory.getLogger(UnprocessableEntityException.class);

	public UnprocessableEntityException() {
		super("Unprocessable Entity.");
		final StackTraceElement[] newStackTrace = { new StackTraceElement(this.getClass().getSimpleName(),
				HttpStatus.UNPROCESSABLE_ENTITY.name(), "", HttpStatus.UNPROCESSABLE_ENTITY.value()) };
		setStackTrace(newStackTrace);
	}
	
	public UnprocessableEntityException(Exception e) {
		super("Internal Server Error");
		logger.error(e.getStackTrace().toString());
		
	}

	@Override
	public String getLocalizedMessage() {
		return "L’entité fournie avec la requête est incompréhensible ou incomplète.";
	}
}
