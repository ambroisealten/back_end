package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

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
	private Geographic place;
	private int radius;
	private String unit;
	
	public Mobility() {
		super();
	}

	public Geographic getPlace() {
		return place;
	}

	public void setPlace(Geographic place) {
		this.place = place;
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
	
	
}
