/**
 * 
 */
package fr.alten.ambroiseJEE.model.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * 
 * @author Thomas Decamp
 *
 */
@Document(collection = "skill")
public class Skill {

	private static final long serialVersionUID = 8285367427781453220L;

	@Id
	private transient ObjectId _id;

	@Indexed(unique = true)
	private String name;

	public Skill() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
