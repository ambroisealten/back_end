/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DuplicateKeyException;

import fr.alten.ambroiseJEE.model.beans.DocumentSet;
import fr.alten.ambroiseJEE.model.beans.mobileDoc.MobileDoc;
import fr.alten.ambroiseJEE.model.beans.mobileDoc.MobileDocArrayList;
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
	 * @param JDocumentSet the AppMobile Version
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database (that mean file already exist and then it's an upload. But
	 *         no change to make in base and {@link CreatedException} if the
	 *         document is created
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException createDocumentSet(final JsonNode JDocumentSet) {
		final DocumentSet documentSet = new DocumentSet();
		documentSet.setName(JDocumentSet.get("name").textValue());
		final List<MobileDoc> files = new ArrayList<MobileDoc>();
		for (final JsonNode document : JDocumentSet.get("files")) {
			final Integer order = Integer.valueOf(document.get("order").asInt());
			final String name = document.get("name").textValue();
			files.add(MobileDoc.of(name, order));
		}
		documentSet.setMobileDocs(files);
		try {
			this.documentSetRepository.save(documentSet);
		} catch (final DuplicateKeyException dke) {
			dke.printStackTrace();
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * Document update on server
	 *
	 * @param JDocumentSet the AppMobile Version
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database (that mean file already exist and then it's an upload. But
	 *         no change to make in base and {@link OkException} if the document
	 *         update is valid {@link RessourceNotFoundException} if the document
	 *         don't exist on the server
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateDocumentSet(final JsonNode JDocumentSet) {
		final DocumentSet documentSet = this.documentSetRepository.findByName(JDocumentSet.get("oldName").textValue())
				.orElseThrow(RessourceNotFoundException::new);
		documentSet.setName(JDocumentSet.get("name").textValue());
		final List<MobileDoc> files = new ArrayList<MobileDoc>();
		for (final JsonNode document : JDocumentSet.get("files")) {
			final Integer order = Integer.valueOf(document.get("order").asInt());
			final String name = document.get("name").textValue();
			files.add(MobileDoc.of(name, order));
		}
		documentSet.setMobileDocs(files);
		try {
			this.documentSetRepository.save(documentSet);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * Fetch all changes of the document set
	 *
	 * @param jDocumentSet the AppMobile Version
	 * @return an HashMap with 3 List: additions, changes, deletions
	 * @author MAQUINGHEN MAXIME, Andy Chabalier
	 */
	public HashMap<String, List<MobileDoc>> getDocumentSetChanges(final JsonNode jDocumentSet) {
		final String documentSetName = jDocumentSet.get("name").textValue();

		// We fetch the corresponding documentSet. If not present we throw
		// RessourceNotFoundException. If is present we get the list of MobileDoc. And
		// we create from this list a MobileDocArrayList
		final DocumentSet documentSet = this.documentSetRepository.findByName(documentSetName)
				.orElseThrow(RessourceNotFoundException::new);
		final List<MobileDoc> mobileDocs = documentSet.getMobileDocs();
		final MobileDocArrayList databaseDocArrayList = new MobileDocArrayList(mobileDocs);

		// We create a mobileDocArrayList from the provided jDocumentSet
		final MobileDocArrayList mobileDocArrayList = new MobileDocArrayList();
		for (final JsonNode document : jDocumentSet.get("files")) {
			final Integer order = Integer.valueOf(document.get("order").asInt());
			final String name = document.get("name").textValue();
			mobileDocArrayList.add(MobileDoc.of(name, order));
		}

		// compare document set of DB with Document set from app and return the result
		final HashMap<String, List<MobileDoc>> result = mobileDocArrayList.compare(databaseDocArrayList);
		// The addition of the other MobileDocArrayList correspond to this
		// MobileDocArrayList deletion
		result.put("deletion", databaseDocArrayList.compare(mobileDocArrayList).get("additions"));
		return result;
	}

	/**
	 * 
	 * @return the List of the all the document Version on the server
	 * @author MAQUINGHEN MAXIME
	 */
	public List<DocumentSet> getDocumentSetAdmin() {
		return this.documentSetRepository.findAll();
	}
}