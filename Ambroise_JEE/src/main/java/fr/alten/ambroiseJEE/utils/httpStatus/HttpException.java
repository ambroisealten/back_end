/**
 *
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

/**
 * @author Andy Chabalier
 *
 */
public class HttpException extends RuntimeException {

	private static final long serialVersionUID = -7056137105462342131L;

	public HttpException(final String message) {
		super(message);
	}
}
