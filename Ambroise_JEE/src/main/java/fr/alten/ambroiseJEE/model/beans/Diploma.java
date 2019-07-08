package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Defines what a Diploma is in our app
 *
 * @author Lucas Royackkers
 *
 */
@Document(collection = "diploma")
public class Diploma implements Serializable {

	private static final long serialVersionUID = -6191997074025351479L;
	@Id
	private transient ObjectId _id;
	private String name;
	private String yearOfResult;

	public Diploma() {
		super();
	}

	public ObjectId get_id() {
		return this._id;
	}

	public String getName() {
		return this.name;
	}

	public String getYearOfResult() {
		return this.yearOfResult;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setYearOfResult(final String yearOfResult) {
		this.yearOfResult = yearOfResult;
	}

}
