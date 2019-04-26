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
	private String name;

	public ObjectId get_id() {
		return this._id;
	}

	public long getDateOfCreation() {
		return this.dateOfCreation;
	}

	public String getExtension() {
		return this.extension;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setDateOfCreation(final long dateOfAddition) {
		this.dateOfCreation = dateOfAddition;
	}

	public void setExtension(final String extension) {
		this.extension = extension;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPath(final String uri) {
		this.path = uri;
	}

}
