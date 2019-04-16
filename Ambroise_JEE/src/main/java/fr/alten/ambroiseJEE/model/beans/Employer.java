package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * 
 * 
 * @author Lucas Royackkers
 *
 */
public class Employer implements Serializable{

	private static final long serialVersionUID = 8056737534510849309L;
	private transient ObjectId _id;
	@Indexed(unique = true)
	private String name;
	
	public Employer() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	
}
