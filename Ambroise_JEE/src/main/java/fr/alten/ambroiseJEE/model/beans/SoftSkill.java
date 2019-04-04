package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Defines what a Soft Skill is in our app
 * 
 * @author Lucas Royackkers
 *
 */
@Document(collection = "softskill")
public class SoftSkill implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5649453725610035070L;
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
