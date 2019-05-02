package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.alten.ambroiseJEE.model.SkillGraduated;

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
	private String rolePersonAttachedTo;
	private String mailVersionAuthor;
	private long versionNumber;
	private List<SkillGraduated> skillsList;
	private String versionDate;
	private File cvPerson;

	public SkillsSheet() {
		super();
	}

	public ObjectId get_id() {
		return this._id;
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

	public String getRolePersonAttachedTo() {
		return this.rolePersonAttachedTo;
	}

	public List<SkillGraduated> getSkillsList() {
		return this.skillsList;
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

	public void setMailPersonAttachedTo(final String mailPersonAttachedTo) {
		this.mailPersonAttachedTo = mailPersonAttachedTo;
	}

	public void setMailVersionAuthor(final String mailVersionAuthor) {
		this.mailVersionAuthor = mailVersionAuthor;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setRolePersonAttachedTo(final String rolePersonAttachedTo) {
		this.rolePersonAttachedTo = rolePersonAttachedTo;
	}

	public void setSkillsList(final List<SkillGraduated> skillsList) {
		this.skillsList = skillsList;
	}

	public void setVersionDate(final String versionDate) {
		this.versionDate = versionDate;
	}

	public void setVersionNumber(final long versionNumber) {
		this.versionNumber = versionNumber;
	}

	public File getCvPerson() {
		return cvPerson;
	}
	
	public void setCvPerson(File cvPerson) {
		this.cvPerson = cvPerson;
	}
	

}
