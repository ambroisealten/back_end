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
		return this._id;
	}

	public String getIsSoft() {
		return this.isSoft;
	}

	public String getName() {
		return this.name;
	}

	public boolean isSoft() {
		return this.isSoft != null;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setIsSoft(final String isSoft) {
		this.isSoft = isSoft;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
