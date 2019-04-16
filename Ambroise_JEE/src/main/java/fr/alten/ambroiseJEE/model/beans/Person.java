package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
	@Indexed(unique = true)
	private String mail;
	private String surname;
	private String name;
	private String job;
	private String employer;
	private float monthlyWage;
	private PersonRole role;
	private String personInChargeMail;
	private List<String> urlDocs;
	private String highestDiploma;
	private String highestDiplomaYear;

	public Person() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public float getMonthlyWage() {
		return monthlyWage;
	}

	public void setMonthlyWage(float monthlyWage) {
		this.monthlyWage = monthlyWage;
	}

	public PersonRole getRole() {
		return role;
	}

	public void setRole(PersonRole role) {
		this.role = role;
	}

	public List<String> getUrlDocs() {
		return urlDocs;
	}

	public void setUrlDocs(List<String> urlDocs) {
		this.urlDocs = urlDocs;
	}

	public String getHighestDiploma() {
		return highestDiploma;
	}

	public void setHighestDiploma(String highestDiploma) {
		this.highestDiploma = highestDiploma;
	}

	public String getPersonInChargeMail() {
		return personInChargeMail;
	}

	public void setPersonInChargeMail(String personInChargeMail) {
		this.personInChargeMail = personInChargeMail;
	}

	public String getHighestDiplomaYear() {
		return highestDiplomaYear;
	}

	public void setHighestDiplomaYear(String highestDiplomaYear) {
		this.highestDiplomaYear = highestDiplomaYear;
	}
	
	
}
