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
		return this._id;
	}

	public String getName() {
		return this.name;
	}

	public String getPlace() {
		return this.place;
	}

	public String getPlaceType() {
		return this.placeType;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPlace(final String place) {
		this.place = place;
	}

	public void setPlaceType(final String placeType) {
		this.placeType = placeType;
	}

}
