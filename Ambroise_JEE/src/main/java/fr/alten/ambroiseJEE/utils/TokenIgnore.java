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

		if (dirPath != null && !"".equals(dirPath.trim())) {
			File dirFile = new File(dirPath);

			if (!dirFile.exists()) {
				boolean result = dirFile.mkdir();
				if (result) {
					logger.trace("Create " + dirPath + " success. ");
					createFile(dirFile);
				} else {
					logger.error("Create " + dirPath + " fail. ");
				}
			} else {
				logger.error(dirPath + " already exist. ");
			}
		}

	}

	/**
	 * Create a file inside of the "dev" directory called "yyyMMdd.txt"
	 * 
	 * @param file {@link File} containing the path of the "dev" folder
	 * @author Kylian Gehier
	 */
	private static void createFile(File file) {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		File newFile = new File(file.getAbsolutePath() + "\\" + dateFormat.format(date) + ".txt");

		try {
			boolean createdFile = newFile.createNewFile();
			logger.trace((createdFile) ? "File created" : "Fail");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete the "dev" directory
	 * 
	 * @author Kylian Gehier
	 */
	public static void deleteDir() throws FileNotFoundException {

		File dir = new File(dirPath);
		if (!dir.exists()) {
			throw new FileNotFoundException();
		} else {
			deleteFile();
			dir.delete();
			logger.trace("Dir deleted");
		}
	}

	/**
	 * Delete the file inside of the "dev" directory called "yyyMMdd.txt"
	 * 
	 * @author Kylian Gehier
	 */
	private static void deleteFile() throws FileNotFoundException {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();

		File file = new File(dirPath + "/" + dateFormat.format(date) + ".txt");
		if (!file.exists()) {
			logger.error("Error trying to delete : " + file.getAbsolutePath() + " -> FileNotFound");
			throw new FileNotFoundException();
		} else {
			file.delete();
			logger.trace("File deleted");
		}
	}

	/**
	 * Detect the presence of the "dev" directory and the txt file inside.
	 * 
	 * @return true if both are present, false otherwhise
	 * @author Kylian Gehier
	 */
	public static boolean fileIsPresent() {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();

		File file = new File(dirPath + "/" + dateFormat.format(date) + ".txt");

		return file.exists();

	}

	/**
	 * get the {@link User}'s mail set in the {@link TokenFilter} when its ignored
	 * @return the {@link User}'s mail set in the {@link TokenFilter} when its ignored
	 * @author Kylian Gehier
	 */
	public static String getTokenIgnoreMail() {
		return tokenIgnoreMail;
	}

	/**
	 * get the {@link User}'s {@link UserRole} set in the {@link TokenFilter} when its ignored
	 * @return the {@link User}'s {@link UserRole} set in the {@link TokenFilter} when its ignored
	 * @author Kylian Gehier
	 */
	public static UserRole getTokenIgnoreUserRole() {
		return tokenIgnoreUserRole;
	}

}
