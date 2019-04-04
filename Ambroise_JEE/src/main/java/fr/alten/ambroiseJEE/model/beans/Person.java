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
	private Job job;
	private Employer employer;
	private int monthlyWage;
	private Date canStartsAt;
	private PersonRole role;
	private String mail;
	private User managerInCharge;
	private Diploma highestDiploma;
	private List<Mobility> mobility;
	private String grade;
	private String commentary;

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
	public User getManagerInCharge() {
		return managerInCharge;
	}
	public void setManagerInCharge(User managerInCharge) {
		this.managerInCharge = managerInCharge;
	}
	public Diploma getHighestDiploma() {
		return highestDiploma;
	}
	public void setHighestDiploma(Diploma highestDiploma) {
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
	public void setJob(Job job) {
		this.job = job;
	}
	public Job getJob() {
		return this.job;
	}
	public List<Mobility> getMobility() {
		return mobility;
	}
	public void setMobility(List<Mobility> mobility) {
		this.mobility = mobility;
	}
	public Employer getEmployer() {
		return employer;
	}
	public void setEmployer(Employer employer) {
		this.employer = employer;
	}
	
}
