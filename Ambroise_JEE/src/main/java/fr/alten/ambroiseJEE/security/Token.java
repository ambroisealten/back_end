/**
 *
 */
package fr.alten.ambroiseJEE.security;

/**
 * Token wrapper
 *
 * @author Andy Chabalier
 *
 */
public class Token {

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
