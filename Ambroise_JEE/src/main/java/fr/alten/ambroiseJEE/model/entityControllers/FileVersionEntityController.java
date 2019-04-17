/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.beans.FileVersion;
import fr.alten.ambroiseJEE.model.dao.FileVersionRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
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
	 * @param appVersion
	 * @return
	 * @author MAQUINGHEN MAXIME
	 */
	public HashMap<String, List<String>> compareVersionData(JsonNode appVersion) {
		HashMap<String, List<String>> newAppVersion = new HashMap<>();
		List<String> addVersion = new ArrayList<String>();
		List<String> changesVersion = new ArrayList<String>();
		List<String> deleteVersion = new ArrayList<String>();
		List<String> test = new ArrayList<String>();
		List<FileVersion> serverVersion = fileVersionRepository.findAll();
		JsonNode newVersion = appVersion.get("files");
		for (JsonNode version : newVersion) {
			Optional<File> newFile = fileVersionRepository.findByUri(version.get("uri").textValue());
			if (!newFile.isPresent()) {
				deleteVersion.add(version.get("uri").textValue());
				test.add(version.get("uri").textValue());
			} else {
				if (!(version.get("dateOfAddition").asLong() == newFile.get().getDateOfAddition())) {
					changesVersion.add(version.get("uri").textValue());
					test.add(version.get("uri").textValue());
				} else {
					test.add(version.get("uri").textValue());
				}
			}
		}
		for (File file : serverVersion) {
			String fileUri = file.getUri();
			if (!test.contains(fileUri)) {
				addVersion.add(fileUri);
			}
		}
		newAppVersion.put("add", addVersion);
		newAppVersion.put("changes", changesVersion);
		newAppVersion.put("delete", deleteVersion);
		newAppVersion.put("testlol", test);

		return newAppVersion;
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
	public List<FileVersion> getVersionData() {
		return fileVersionRepository.findAll();
	}

	public HttpException newFileVersion(JsonNode params) {
		
		db.fileVersionRepository.drop();
		
		JsonNode newVersion = params.get("files");
		for (JsonNode version : newVersion) {
			
			return new OkException();
		}
		return new ConflictException();
		// MongoOperations.dropCollection(FileVersion.class;
	}
}
