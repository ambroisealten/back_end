package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Defines what a Tech Skill is in our app
 * 
 * @author Lucas Royackkers
 *
 */
@Document(collection = "techskill")
public class TechSkill implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8253428049469540677L;
	private String name;
	private int grade;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	
	

}
