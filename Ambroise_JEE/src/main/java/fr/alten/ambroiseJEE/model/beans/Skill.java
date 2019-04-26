/**
 *
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Thomas Decamp
 *
 */
@Document(collection = "skill")
public class Skill implements Serializable {

	private static final long serialVersionUID = -4597432921266925981L;

	@Id
	private transient ObjectId _id;
	private String name;
	private String isSoft;

	public Skill() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}

	public String getName() {
		return name;
	}

	public String getIsSoft() {
		return isSoft;
	}

	public void setIsSoft(String isSoft) {
		this.isSoft = isSoft;
	}
	
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public void setName(String name) {
		this.name = name;
	}
}
