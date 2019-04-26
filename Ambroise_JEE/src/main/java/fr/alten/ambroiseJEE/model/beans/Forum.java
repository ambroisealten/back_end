/**
 *
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Forum bean objects.
 *
 * @author MAQUINGHEN MAXIME
 *
 */
@Document(collection = "forum")
public class Forum implements Serializable {

	private static final long serialVersionUID = 6478313237765560223L;

	@Id
	private transient ObjectId _id;

	private String name;
	private String date;
	private String place;

	/**
	 * @return the _id
	 */
	public ObjectId get_id() {
		return this._id;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return this.date;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return this.place;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(final String date) {
		this.date = date;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(final String place) {
		this.place = place;
	}
}
