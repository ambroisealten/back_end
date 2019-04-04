package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Defines what a SkillS Sheet is in our app
 * 
 * @author Lucas Royackkers
 *
 */
@Document(collection = "skillssheet")
public class SkillsSheet implements Serializable{

	private static final long serialVersionUID = 4878950681041229894L;
	private String name;
	private Person personAttachedTo;
	private List<TechSkill> techSkillsList;
	private List<SoftSkill> softSkillsList;
	private HashMap<User,Date> allVersions;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Person getPersonAttachedTo() {
		return personAttachedTo;
	}
	public void setPersonAttachedTo(Person personAttachedTo) {
		this.personAttachedTo = personAttachedTo;
	}
	public List<TechSkill> getTechSkillsList() {
		return techSkillsList;
	}
	public void setTechSkillsList(List<TechSkill> techSkillsList) {
		this.techSkillsList = techSkillsList;
	}
	public List<SoftSkill> getSoftSkillsList() {
		return softSkillsList;
	}
	public void setSoftSkillsList(List<SoftSkill> softSkillsList) {
		this.softSkillsList = softSkillsList;
	}
	public HashMap<User, Date> getAllVersions() {
		return allVersions;
	}
	public void setAllVersions(HashMap<User, Date> allVersions) {
		this.allVersions = allVersions;
	}
	
	
}
