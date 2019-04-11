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
@Document(collection = "mobility")
public class Mobility implements Serializable {

	private static final long serialVersionUID = -2144367851428317559L;
	@Id
	private transient ObjectId _id;
	private String placeName;
	private String placeType;
	private int radius;
	private String unit;
	
	public Mobility() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}


	public void set_id(ObjectId _id) {
		this._id = _id;
	}


	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPlaceType() {
		return placeType;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}
	
}
