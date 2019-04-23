/**
 *
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Andy Chabalier
 *
 */
@Document(collection = "files")
public class File implements Serializable {

	private static final long serialVersionUID = 2192182119022876865L;

	@Id
	private transient ObjectId _id;
	@Indexed(unique = true)
	private String uri;
	private long dateOfModification;
	private boolean isForForum;

	public ObjectId get_id() {
		return _id;
	}

	public long getDateOfModification() {
		return dateOfModification;
	}

	public String getUri() {
		return uri;
	}

	public boolean isForForum() {
		return isForForum;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public void setDateOfModification(long dateOfAddition) {
		this.dateOfModification = dateOfAddition;
	}

	public void setForForum(boolean isForForum) {
		this.isForForum = isForForum;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
