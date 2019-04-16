package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.alten.ambroiseJEE.utils.SoftSkillGrade;


/**
 * Defines what a Soft Skill is in our app
 * 
 * @author Lucas Royackkers
 *
 */
@Document(collection = "softskill")
public class SoftSkill implements Serializable{

	private static final long serialVersionUID = 5649453725610035070L;
	
	@Id
	private ObjectId _id;
	private String name;
	private SoftSkillGrade grade;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SoftSkillGrade getGrade() {
		return grade;
	}
	public void setGrade(SoftSkillGrade grade) {
		this.grade = grade;
	}
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
}
