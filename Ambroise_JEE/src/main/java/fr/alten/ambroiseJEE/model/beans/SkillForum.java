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
 *
 * @author Thomas Decamp
 *
 */
@Document(collection = "skillforum")
public class SkillForum implements Serializable {

	private static final long serialVersionUID = -4597432921266925981L;

	@Id
	private transient ObjectId _id;

	@Indexed(unique = true)
	private String name;

	public SkillForum() {
		super();
	}

	public ObjectId get_id() {
		return this._id;
	}

	public String getName() {
		return this.name;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setName(final String name) {
		this.name = name;
	}
}