/**
 *
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception sent when an internal error occur HTTP Status : 500.
 *
 * @author Andy Chabalier
 *
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Erreur interne du serveur")
public class InternalServerErrorException extends HttpException {

	private static final long serialVersionUID = 6695487880532171802L;
	private final Logger logger = LoggerFactory.getLogger(InternalServerErrorException.class);

	public InternalServerErrorException() {
		super("Internal Server Error");
	}

	public InternalServerErrorException(final Exception e) {
		super("Internal Server Error");
		this.logger.error(e.getStackTrace().toString());

	}
}
