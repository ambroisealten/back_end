package fr.alten.ambroiseJEE.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kylian Gehier
 *
 */

public class DirAndFileCreator {

	private static String dirPath = "src/main/resources/dev";
	private static Logger logger = LoggerFactory.getLogger(DirAndFileCreator.class);

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

	public static void createFile(File file) {

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

	public static void deleteFile() throws FileNotFoundException {

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

	public static boolean fileIsPresent() {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();

		File file = new File(dirPath + "/" + dateFormat.format(date) + ".txt");

		return file.exists();

	}

}
