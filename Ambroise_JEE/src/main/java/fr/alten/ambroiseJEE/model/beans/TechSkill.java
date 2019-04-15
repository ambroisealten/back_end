package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Defines what a Tech Skill is in our app
 * 
 * @author Lucas Royackkers
 *
 */
@Document(collection = "techskill")
public class TechSkill implements Serializable{

	private static final long serialVersionUID = 8253428049469540677L;
	
	@Id
	private ObjectId _id;
	private String name;
	private float grade;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getGrade() {
		return grade;
	}
	public void setGrade(float grade) {
		this.grade = grade;
	}
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	
	

}
