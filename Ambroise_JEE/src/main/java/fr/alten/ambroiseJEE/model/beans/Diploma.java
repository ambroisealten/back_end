package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * 
 * @author Lucas Royackkers
 *
 */
@Document(collection = "diploma")
public class Diploma implements Serializable{

	private static final long serialVersionUID = -6191997074025351479L;
	@Id
	private ObjectId _id;
	private String name;
	private String yearOfResult;
	
	public Diploma() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYearOfResult() {
		return yearOfResult;
	}

	public void setYearOfResult(String yearOfResult) {
		this.yearOfResult = yearOfResult;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	
	
}
