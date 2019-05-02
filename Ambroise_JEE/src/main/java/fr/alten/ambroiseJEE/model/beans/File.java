/**
 * 
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Andy Chabalier
 *
 */
@Document(collection = "files")
public class File implements Serializable {

	private static final long serialVersionUID = 2715853052440964943L;
	
	@Id
	private transient ObjectId _id;
	@Indexed(unique = true)
	private String uri;
	private long dateOfAddition;
	private boolean isForForum;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public long getDateOfAddition() {
		return dateOfAddition;
	}

	public void setDateOfAddition(long dateOfAddition) {
		this.dateOfAddition = dateOfAddition;
	}

	public boolean isForForum() {
		return isForForum;
	}

	public void setForForum(boolean isForForum) {
		this.isForForum = isForForum;
	}

}
