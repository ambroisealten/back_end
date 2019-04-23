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
	private ObjectId _id;
	private String path;
	private String extension;
	private long dateOfCreation;

	public ObjectId get_id() {
		return _id;
	}

	public long getDateOfCreation() {
		return dateOfCreation;
	}

	public String getPath() {
		return path;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public void setDateOfCreation(long dateOfAddition) {
		this.dateOfCreation = dateOfAddition;
	}

	public void setPath(String uri) {
		this.path = uri;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

}
