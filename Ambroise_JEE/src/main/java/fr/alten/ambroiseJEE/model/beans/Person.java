package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.alten.ambroiseJEE.utils.PersonRole;

/**
 * Defines what a person is in our app
 * 
 * @author Lucas Royackkers
 *
 */
@Document(collection = "person")
public class Person implements Serializable{

	private static final long serialVersionUID = 5313704620913617832L;
	@Id
	private transient ObjectId _id;
	private String name;
	private String job;
	private String employer;
	private int monthlyWage;
	private Date canStartsAt;
	private PersonRole role;
	private String mail;
	private String managerInCharge;
	private String highestDiploma;
	private List<String> mobilities;
	private String grade;
	private String commentary;
	private List<String> urlDocs;
	private boolean fromForum;

	public Person() {
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

	public int getMonthlyWage() {
		return monthlyWage;
	}

	public void setMonthlyWage(int monthlyWage) {
		this.monthlyWage = monthlyWage;
	}
	public Date getCanStartsAt() {
		return canStartsAt;
	}
	public void setCanStartsAt(Date canStartsAt) {
		this.canStartsAt = canStartsAt;
	}
	public PersonRole getRole() {
		return role;
	}
	public void setRole(PersonRole type) {
		this.role = type;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getManagerInCharge() {
		return managerInCharge;
	}
	public void setManagerInCharge(String managerInCharge) {
		this.managerInCharge = managerInCharge;
	}
	public String getHighestDiploma() {
		return highestDiploma;
	}
	public void setHighestDiploma(String highestDiploma) {
		this.highestDiploma = highestDiploma;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getCommentary() {
		return commentary;
	}
	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getJob() {
		return this.job;
	}
	public List<String> getMobilities() {
		return mobilities;
	}
	public void setMobilities(List<String> mobilities) {
		this.mobilities = mobilities;
	}
	public String getEmployer() {
		return employer;
	}
	public void setEmployer(String employer) {
		this.employer = employer;
	}
	public List<String> getUrlDocs() {
		return urlDocs;
	}
	public void setUrlDocs(List<String> urlDocs) {
		this.urlDocs = urlDocs;
	}
	public boolean isFromForum() {
		return fromForum;
	}
	public void setFromForum(boolean fromForum) {
		this.fromForum = fromForum;
	}
}
