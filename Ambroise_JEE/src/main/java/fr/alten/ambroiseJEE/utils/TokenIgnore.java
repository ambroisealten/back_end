package fr.alten.ambroiseJEE.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.filter.TokenFilter;

import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.security.UserRole;

/**
 * Class used in integration test in order to ignore the {@link TokenFilter}
 * when calling webservices
 *
 * @author Kylian Gehier
 *
 */

public class TokenIgnore {

	private static String dirPath = "src/main/resources/dev";
	private static Logger logger = LoggerFactory.getLogger(TokenIgnore.class);
	private static String tokenIgnoreMail = "tempUserAdminManager@mail.com";
	private static UserRole tokenIgnoreUserRole = UserRole.MANAGER_ADMIN;

	/**
	 * Create a directory called "dev"
	 *
	 * @author Kylian Gehier
	 */
	public static void createDir() {

		if (TokenIgnore.dirPath != null && !"".equals(TokenIgnore.dirPath.trim())) {
			final File dirFile = new File(TokenIgnore.dirPath);

			if (!dirFile.exists()) {
				final boolean result = dirFile.mkdir();
				if (result) {
					TokenIgnore.logger.trace("Create " + TokenIgnore.dirPath + " success. ");
					TokenIgnore.createFile(dirFile);
				} else {
					TokenIgnore.logger.error("Create " + TokenIgnore.dirPath + " fail. ");
				}
			} else {
				TokenIgnore.logger.error(TokenIgnore.dirPath + " already exist. ");
			}
		}

	}

	/**
	 * Create a file inside of the "dev" directory called "yyyMMdd.txt"
	 *
	 * @param file {@link File} containing the path of the "dev" folder
	 * @author Kylian Gehier
	 */
	private static void createFile(final File file) {

		final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		final Date date = new Date();
		final File newFile = new File(file.getAbsolutePath() + "\\" + dateFormat.format(date) + ".txt");

		try {
			final boolean createdFile = newFile.createNewFile();
			TokenIgnore.logger.trace(createdFile ? "File created" : "Fail");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete the "dev" directory
	 *
	 * @author Kylian Gehier
	 */
	public static void deleteDir() throws FileNotFoundException {

		final File dir = new File(TokenIgnore.dirPath);
		if (!dir.exists()) {
			throw new FileNotFoundException();
		} else {
			TokenIgnore.deleteFile();
			dir.delete();
			TokenIgnore.logger.trace("Dir deleted");
		}
	}

	/**
	 * Delete the file inside of the "dev" directory called "yyyMMdd.txt"
	 *
	 * @author Kylian Gehier
	 */
	private static void deleteFile() throws FileNotFoundException {

		final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		final Date date = new Date();

		final File file = new File(TokenIgnore.dirPath + "/" + dateFormat.format(date) + ".txt");
		if (!file.exists()) {
			TokenIgnore.logger.error("Error trying to delete : " + file.getAbsolutePath() + " -> FileNotFound");
			throw new FileNotFoundException();
		} else {
			file.delete();
			TokenIgnore.logger.trace("File deleted");
		}
	}

	/**
	 * Detect the presence of the "dev" directory and the txt file inside.
	 *
	 * @return true if both are present, false otherwhise
	 * @author Kylian Gehier
	 */
	public static boolean fileIsPresent() {

		final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		final Date date = new Date();

		final File file = new File(TokenIgnore.dirPath + "/" + dateFormat.format(date) + ".txt");

		return file.exists();

	}

	/**
	 * get the {@link User}'s mail set in the {@link TokenFilter} when its ignored
	 * 
	 * @return the {@link User}'s mail set in the {@link TokenFilter} when its
	 *         ignored
	 * @author Kylian Gehier
	 */
	public static String getTokenIgnoreMail() {
		return TokenIgnore.tokenIgnoreMail;
	}

	/**
	 * get the {@link User}'s {@link UserRole} set in the {@link TokenFilter} when
	 * its ignored
	 * 
	 * @return the {@link User}'s {@link UserRole} set in the {@link TokenFilter}
	 *         when its ignored
	 * @author Kylian Gehier
	 */
	public static UserRole getTokenIgnoreUserRole() {
		return TokenIgnore.tokenIgnoreUserRole;
	}

}
