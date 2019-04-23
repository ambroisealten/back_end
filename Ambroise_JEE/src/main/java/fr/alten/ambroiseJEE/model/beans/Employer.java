package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Defines what an Employer is in our app
 *
 * @author Lucas Royackkers
 *
 */
@Document(collection = "employer")
public class Employer implements Serializable {

	private static final long serialVersionUID = 8056737534510849309L;
	@Id
	private transient ObjectId _id;
	@Indexed(unique = true)
	private String name;

	public Employer() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}

	public String getName() {
		return name;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
