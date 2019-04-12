/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.dao.FileVersionRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
public class FileVersionEntityController {

	@Autowired
	private FileVersionRepository fileVersionRepository;

	public HttpException compareVersionData(String appVersion, String serverVersion) {
		if (appVersion.compareTo(serverVersion) == 0)  {
			return new OkException();
		}else {
			 return new OkException();
		}
	}

	public HttpException updateAppDataVersion(String appVersion, String serverVersion) {
		return null;
	}

	public List<File> getVersionData() {
		return fileVersionRepository.findAll();
	}
}
