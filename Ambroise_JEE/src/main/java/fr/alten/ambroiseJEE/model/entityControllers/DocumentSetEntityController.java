/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.DocumentSet;
import fr.alten.ambroiseJEE.model.dao.DocumentSetRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class DocumentSetEntityController {

	@Autowired
	private DocumentSetRepository documentSetRepository;

	/**
	 * Document creation on Server
	 * 
	 * @param JDocumentSet
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database (that mean file already exist and then it's an upload. But
	 *         no change to make in base and {@link CreatedException} if the
	 *         document is created
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException createDocumentSet(JsonNode JDocumentSet) {
		DocumentSet documentSet = new DocumentSet();
		documentSet.setName(JDocumentSet.get("name").textValue());
		List<Pair<String, Integer>> files = new ArrayList<Pair<String, Integer>>();
		for (JsonNode document : JDocumentSet.get("files")) {
			Integer order = Integer.valueOf(document.get("order").asInt());
			String uri = document.get("uri").textValue();
			files.add(Pair.of(uri, order));
		}
		documentSet.setFiles(files);
		try {
			documentSetRepository.save(documentSet);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * Document update on server
	 * 
	 * @param JDocumentSet
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database (that mean file already exist and then it's an upload. But
	 *         no change to make in base and {@link OkException} if the document
	 *         update is valid {@link RessourceNotFoundException} if the document
	 *         don't exist on the server
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateDocumentSet(JsonNode JDocumentSet) {
		String name = JDocumentSet.get("oldName").textValue();
		Optional<DocumentSet> documentSetOptional = documentSetRepository.findByName(name);
		if (documentSetOptional.isPresent()) {
			DocumentSet documentSet = documentSetOptional.get();
			documentSet.setName(JDocumentSet.get("name").textValue());
			List<Pair<String, Integer>> files = new ArrayList<Pair<String, Integer>>();
			for (JsonNode document : JDocumentSet.get("files")) {
				Integer order = Integer.valueOf(document.get("order").asInt());
				String uri = document.get("uri").textValue();
				files.add(Pair.of(uri, order));
			}
			documentSet.setFiles(files);
			try {
				documentSetRepository.save(documentSet);
			} catch (Exception e) {
				return new ConflictException();
			}
			return new OkException();
		}
		return new RessourceNotFoundException();
	}

	public List<Pair<String, Integer>> getDocumentSet(JsonNode jDocumentSet) {
		
		String name = jDocumentSet.get("name").textValue();
		Optional<DocumentSet> documentSetOptional = documentSetRepository.findByName(name);
		if (documentSetOptional.isPresent()) {
			DocumentSet documentSet = documentSetOptional.get();
			documentSet.setName(jDocumentSet.get("name").textValue());
			List<Pair<String, Integer>> files = new ArrayList<Pair<String, Integer>>();
			for (JsonNode document : jDocumentSet.get("files")) {
				Integer order = Integer.valueOf(document.get("order").asInt());
				String uri = document.get("uri").textValue();
				files.add(Pair.of(uri, order));
			}
			//documentSet.setFiles(files);
			try {
				return files; 
			} catch (Exception e) {
				throw new ConflictException();
			}
		}
		throw new RessourceNotFoundException();
		
		/*
		
		String name = jDocumentSet.get("name").textValue();
		Optional<DocumentSet> documentSetOptional = documentSetRepository.findByName(name);
		if (documentSetOptional.isPresent()) {
			DocumentSet documentSet = documentSetOptional.get();
			List<Pair<String, Integer>> files = new ArrayList<Pair<String, Integer>>();
			for (JsonNode document : jDocumentSet.get("files")) {
				Integer order = Integer.valueOf(document.get("order").asInt());
				String uri = document.get("uri").textValue();
				files.add(Pair.of(uri, order));
				System.out.println(files + "  lol " + " MDr " + documentSet);
			}
			return null;
		}
		return null;*/
	}

	public List<DocumentSet> getDocumentSetAdmin() {
		return documentSetRepository.findAll();
	}
}

///**
//* 
//* @param appVersion
//* @return
//* @author MAQUINGHEN MAXIME
//*/
//public HashMap<String, List<String>> compareVersionData(JsonNode appVersion) {
//	HashMap<String, List<String>> newAppVersion = new HashMap<>();
//	List<String> addVersion = new ArrayList<String>();
//	List<String> changesVersion = new ArrayList<String>();
//	List<String> deleteVersion = new ArrayList<String>();
//	List<String> allVersion = new ArrayList<String>();
//	List<DocumentSet> serverVersion = fileVersionRepository.findAll();
//	JsonNode newVersion = appVersion.get("files");
//	for (JsonNode version : newVersion) {
//		Optional<DocumentSet> newFile = fileVersionRepository.findByUri(version.get("uri").textValue());
//		if (!newFile.isPresent()) {
//			deleteVersion.add(version.get("uri").textValue());
//			allVersion.add(version.get("uri").textValue());
//		} else {
//			if (!(version.get("dateOfAddition").asLong() == newFile.get().getDateOfAddition())) {
//				changesVersion.add(version.get("uri").textValue());
//				allVersion.add(version.get("uri").textValue());
//			} else {
//				allVersion.add(version.get("uri").textValue());
//			}
//		}
//	}
//	for (File file : serverVersion) {
//		String fileUri = file.getUri();
//		if (!allVersion.contains(fileUri)) {
//			addVersion.add(fileUri);
//		}
//	}
//	newAppVersion.put("add", addVersion);
//	newAppVersion.put("changes", changesVersion);
//	newAppVersion.put("delete", deleteVersion);
//
//	return newAppVersion;
//}
//
//public HttpException updateAppDataVersion(JsonNode appVersion) {
//	List<DocumentSet> serverVersion = fileVersionRepository.findAll();
//
//	JsonNode newVersion = appVersion.get("files");
//
//	for (JsonNode version : newVersion) {
//		// Optional<File> optionalFile = fileVersionRepository.findByUri(version.get("uri").toString());
//		Optional<DocumentSet> optionalFile = fileVersionRepository
//				.findByOrder(Integer.parseInt(version.get("order").textValue()));
//		if (!optionalFile.isPresent()) {
//			DocumentSet file = new DocumentSet();
//			file.setUri(version.get("uri").textValue());
//			file.setDateOfAddition(version.get("dateOfAddition").asLong());
//			file.setForForum(version.get("isForForum").booleanValue());
//
//			file.setOrder(Integer.parseInt(version.get("order").textValue()));
//			fileVersionRepository.save(file);
//		} else {
//			String versionUri = version.get("uri").textValue();
//			String versionBase = optionalFile.get().getUri();
//			System.out.println(versionUri + " : " + versionBase );
//			
//			if (version.get("uri").textValue() != optionalFile.get().getUri()) {
//				System.out.print("version recu : " +version.get("uri").toString() + " versionServer : " +optionalFile.get().getUri().toString() ); 
//				DocumentSet file = optionalFile.get();
//				file.setUri(version.get("uri").toString());
//				file.setDateOfAddition(version.get("dateOfAddition").asLong());
//				file.setForForum(version.get("isForForum").booleanValue());
//				file.setOrder(Integer.parseInt(version.get("order").textValue()));
//				fileVersionRepository.save(file);
//			} else {
//				
//				System.out.print("ok2");
//				// allVersion.add(version.get("uri").textValue());
//			}
//		}
//	}
//	// fileVersionRepository.findByOrderGreaterThan()
//
//	int newListSize = newVersion.size();
//	int oldListSize = fileVersionRepository.findAll().size();
//
//	if (newListSize < oldListSize) {
//		System.out.print("moins grand que ");
//	} 
//	for (File file : serverVersion) {
//		String fileUri = file.getUri();
//		// if (!allVersion.contains(fileUri)) {
//		// addVersion.add(fileUri);
//		// }
//	}
//	return new OkException();
//}
//
///**
//* Get all the document from the current version
//* 
//* @return the list of all the versionData
//* @author MAQUINGHEN MAXIME
//*/
//public List<DocumentSet> getVersionData() {
//	return fileVersionRepository.findAll();
//}
//
