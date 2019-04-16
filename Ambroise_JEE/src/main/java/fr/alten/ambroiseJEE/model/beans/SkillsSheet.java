package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Defines what a Skills Sheet is in our app
 * 
 * @author Lucas Royackkers
 *
 */
@Document(collection = "skillsSheet")
public class SkillsSheet implements Serializable{

	private static final long serialVersionUID = 4878950681041229894L;
	@Id
	private transient ObjectId _id;
	private String name;
	private String mailPersonAttachedTo;
	private String mailVersionAuthor;
	private long versionNumber;
	private List<TechSkill> techSkillsList;
	private List<SoftSkill> softSkillsList;
	private String versionDate;
	
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

	public String getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(String versionDate) {
		this.versionDate = versionDate;
	}
	
}
