/**
 *
 */
package fr.alten.ambroiseJEE.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
public class MailUtils {

	/**
	 * TODO Envoyer le nouveau mot de passe temporaire/ et un lien pour modifier le
	 * mot de passe directement ?
	 *
	 * @param new_pass
	 * @author MAQUINGHEN MAXIME
	 */
	public static void AdminUserResetPassword(final String new_pass) {

	}

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Method to validate if the mail math with the mail pattern
	 *
	 * @param emailStr the string to validate
	 * @return true if the string match with the mail pattern
	 * @author Andy Chabalier
	 */
	public static boolean validateMail(final String emailStr) {
		final Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
}
