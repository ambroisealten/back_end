package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.alten.ambroiseJEE.utils.PersonRole;

/**
 * Defines what a Skills Sheet is in our app
 *
 * @author Lucas Royackkers
 *
 */
@Document(collection = "skillsSheet")
public class SkillsSheet implements Serializable {

	private static final long serialVersionUID = 4878950681041229894L;
	@Id
	private transient ObjectId _id;
	
	private String name;
	private String mailPersonAttachedTo;
	private PersonRole rolePersonAttachedTo;
	private String mailVersionAuthor;
	private long versionNumber;
	private List<SkillGraduated> skillsList;
	private String versionDate;
	private File cvPerson;
	private double softSkillAverage;
	private String comment;

	public SkillsSheet() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMailPersonAttachedTo() {
		return mailPersonAttachedTo;
	}

	public void setMailPersonAttachedTo(String mailPersonAttachedTo) {
		this.mailPersonAttachedTo = mailPersonAttachedTo;
	}

	public PersonRole getRolePersonAttachedTo() {
		return rolePersonAttachedTo;
	}

	public void setRolePersonAttachedTo(PersonRole rolePersonAttachedTo) {
		this.rolePersonAttachedTo = rolePersonAttachedTo;
	}

	public String getMailVersionAuthor() {
		return mailVersionAuthor;
	}

	public void setMailVersionAuthor(String mailVersionAuthor) {
		this.mailVersionAuthor = mailVersionAuthor;
	}

	public long getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(long versionNumber) {
		this.versionNumber = versionNumber;
	}

	public List<SkillGraduated> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<SkillGraduated> skillsList) {
		this.skillsList = skillsList;
	}

	public String getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(String versionDate) {
		this.versionDate = versionDate;
	}

	public File getCvPerson() {
		return cvPerson;
	}

	public void setCvPerson(File cvPerson) {
		this.cvPerson = cvPerson;
	}

	public double getSoftSkillAverage() {
		return softSkillAverage;
	}

	public void setSoftSkillAverage(double softSkillAverage) {
		this.softSkillAverage = softSkillAverage;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	

}
