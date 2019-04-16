/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.dao.FileVersionRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class FileVersionEntityController {

	@Autowired
	private FileVersionRepository fileVersionRepository;

	/**
	 * 
	 * @param appVersion    App Document version
	 * @param serverVersion Server document version
	 * @return {@link OkException} if the data is the same or if the data is
	 *         different
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException compareVersionData(JsonNode appVersion) {
		System.out.println(appVersion);
		return null;
		/*if (appVersion "[{\"uri\": \"http://localhost:8080/file/Image1.png\",\"dateOfAddition\": 1555404675061,\"isForForum\": false},{\"uri\": \"http://localhost:8080/file/images.jfif\",\"dateOfAddition\": 1555404847987,\"isForForum\": false}]") == 0) {
			return new OkException();
		} else {
			return new CreatedException();
		}*/
	}

	public HttpException updateAppDataVersion(String appVersion, String serverVersion) {
		return null;
	}

	/**
	 * Get all the document from the current version
	 * 
	 * @return the list of all the versionData
	 * @author MAQUINGHEN MAXIME
	 */
	public List<File> getVersionData() {
		return fileVersionRepository.findAll();
	}
}
