/**
 *
 */
package fr.alten.ambroiseJEE.security;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Token wrapper
 *
 * @author Andy Chabalier
 *
 */
@Document(collection = "token")
public class Token implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5658199060845598754L;
	private String token;

	public Token(final String token) {
		this.token = token;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(final String token) {
		this.token = token;
	}
}
