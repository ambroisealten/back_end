/**
 * 
 */
package fr.alten.ambroiseJEE.utils.httpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception sent when the request has been successfully treated . HTTP
 * Status : 200.
 * 
 * @author MAQUINGHEN MAXIME
 *
 */
@ResponseStatus(value = HttpStatus.OK, reason = "requête traitée avec succès")
public class OkException extends HttpException {

	private static final long serialVersionUID = 1247315017130903282L;

	public OkException() {
		super();
	}
}

