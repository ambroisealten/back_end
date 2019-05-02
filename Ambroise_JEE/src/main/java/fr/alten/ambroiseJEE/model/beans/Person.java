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
public class Person implements Serializable {

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
		return this._id;
	}

	public String getEmployer() {
		return this.employer;
	}

	public String getHighestDiploma() {
		return this.highestDiploma;
	}

	public String getHighestDiplomaYear() {
		return this.highestDiplomaYear;
	}

	public String getJob() {
		return this.job;
	}

	public String getMail() {
		return this.mail;
	}

	public float getMonthlyWage() {
		return this.monthlyWage;
	}

	public String getName() {
		return this.name;
	}

	public String getPersonInChargeMail() {
		return this.personInChargeMail;
	}

	public PersonRole getRole() {
		return this.role;
	}

	public String getSurname() {
		return this.surname;
	}

	public List<String> getUrlDocs() {
		return this.urlDocs;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setEmployer(final String employer) {
		this.employer = employer;
	}

	public void setHighestDiploma(final String highestDiploma) {
		this.highestDiploma = highestDiploma;
	}

	public void setHighestDiplomaYear(final String highestDiplomaYear) {
		this.highestDiplomaYear = highestDiplomaYear;
	}

	public void setJob(final String job) {
		this.job = job;
	}

	public void setMail(final String mail) {
		this.mail = mail;
	}

	public void setMonthlyWage(final float monthlyWage) {
		this.monthlyWage = monthlyWage;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPersonInChargeMail(final String personInChargeMail) {
		this.personInChargeMail = personInChargeMail;
	}

	public void setRole(final PersonRole role) {
		this.role = role;
	}

	public void setSurname(final String surname) {
		this.surname = surname;
	}

	public void setUrlDocs(final List<String> urlDocs) {
		this.urlDocs = urlDocs;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
//		return true;
		if(super.equals(other)) {
			return true;
		}
		else if(!(other instanceof Person)) {
			return false;
		}
		else {
			Person otherPerson = (Person) other;
			return this._id.equals(otherPerson.get_id());
		}
		
	}
}
