/**
 *
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.alten.ambroiseJEE.model.beans.mobileDoc.MobileDoc;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Document(collection = "documentSet")
public class DocumentSet implements Serializable {

	private static final long serialVersionUID = 8130484628750269655L;

	@Id
	private transient ObjectId _id;

	@Indexed(unique = true)
	private String name;
	private List<MobileDoc> mobileDocs;

	public ObjectId get_id() {
		return this._id;
	}

	public List<MobileDoc> getMobileDocs() {
		return this.mobileDocs;
	}

	public String getName() {
		return this.name;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setMobileDocs(final List<MobileDoc> mobileDoc) {
		this.mobileDocs = mobileDoc;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
