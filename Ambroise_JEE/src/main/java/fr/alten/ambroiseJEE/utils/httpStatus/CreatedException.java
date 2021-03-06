/**
 *
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception sent when an entity is successfully created in database. HTTP
 * Status : 201.
 *
 * @author Andy Chabalier
 *
 */
@ResponseStatus(value = HttpStatus.CREATED, reason = "Entité créée")
public class CreatedException extends HttpException {

	private static final long serialVersionUID = 1247315017130903282L;

	public CreatedException() {
		super("Created");
		final StackTraceElement[] newStackTrace = { new StackTraceElement(this.getClass().getSimpleName(),
				HttpStatus.CREATED.name(), "", HttpStatus.CREATED.value()) };
		setStackTrace(newStackTrace);
	}
	
	/**
	 * 
	 * @param message
	 * @author Lucas Royackkers
	 */
	public CreatedException(String message) {
		super(message);
		final StackTraceElement[] newStackTrace = { new StackTraceElement(this.getClass().getSimpleName(),
				HttpStatus.CREATED.name(), "", HttpStatus.CREATED.value()) };
		setStackTrace(newStackTrace);
	}

	@Override
	public String getLocalizedMessage() {
		return "Entité créée";
	}
}
