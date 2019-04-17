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
	private String place;
	private String placeType;

	public Agency() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}

	public String getName() {
		return name;
	}

	public String getPlace() {
		return place;
	}

	public String getPlaceType() {
		return placeType;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

}
