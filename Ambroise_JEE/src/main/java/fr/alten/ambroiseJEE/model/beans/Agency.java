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
 * @author MAQUINGHEN MAXIME, Andy Chabalier
 *
 */
@Document(collection = "agency")
public class Agency implements Serializable {

	private static final long serialVersionUID = 1950102193171991600L;

	@Id
	private transient ObjectId _id;
	
	@Indexed(unique = true)
	private String name;
	private Geographic place;
	
	public Agency() {
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
	public Geographic getPlace() {
		return place;
	}
	public void setPlace(Geographic place) {
		this.place = place;
	}
	
	
}
