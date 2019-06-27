package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.alten.ambroiseJEE.utils.personRole.PersonRole;

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

	public ObjectId get_id() {
		return this._id;
	}

	public String getComment() {
		return this.comment;
	}

	public File getCvPerson() {
		return this.cvPerson;
	}

	public String getMailPersonAttachedTo() {
		return this.mailPersonAttachedTo;
	}

	public String getMailVersionAuthor() {
		return this.mailVersionAuthor;
	}

	public String getName() {
		return this.name;
	}

	public PersonRole getRolePersonAttachedTo() {
		return this.rolePersonAttachedTo;
	}

	public List<SkillGraduated> getSkillsList() {
		return this.skillsList;
	}

	public double getSoftSkillAverage() {
		return this.softSkillAverage;
	}

	public String getVersionDate() {
		return this.versionDate;
	}

	public long getVersionNumber() {
		return this.versionNumber;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public void setCvPerson(final File cvPerson) {
		this.cvPerson = cvPerson;
	}

	public void setMailPersonAttachedTo(final String mailPersonAttachedTo) {
		this.mailPersonAttachedTo = mailPersonAttachedTo;
	}

	public void setMailVersionAuthor(final String mailVersionAuthor) {
		this.mailVersionAuthor = mailVersionAuthor;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setRolePersonAttachedTo(final PersonRole rolePersonAttachedTo) {
		this.rolePersonAttachedTo = rolePersonAttachedTo;
	}

	public void setSkillsList(final List<SkillGraduated> skillsList) {
		this.skillsList = skillsList;
	}

	public void setSoftSkillAverage(final double softSkillAverage) {
		this.softSkillAverage = softSkillAverage;
	}

	public void setVersionDate(final String versionDate) {
		this.versionDate = versionDate;
	}

	public void setVersionNumber(final long versionNumber) {
		this.versionNumber = versionNumber;
	}

}
