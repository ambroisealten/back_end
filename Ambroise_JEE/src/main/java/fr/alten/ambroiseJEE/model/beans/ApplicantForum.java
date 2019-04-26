/**
 *
 */
package fr.alten.ambroiseJEE.model.beans;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import fr.alten.ambroiseJEE.utils.Nationality;

/**
 * Forum applicant. Extend {@link Person}
 *
 * @author Andy Chabalier
 *
 */
@Document(collection = "applicantForum")
public class ApplicantForum extends Person {

	private static final long serialVersionUID = 3760154743236315923L;

	private List<String> mobilities;
	private String phoneNumber;
	private String startAt;
	private String grade;
	private String commentary;
	private String contractType;
	private String contractDuration;
	private List<String> skills;
	private boolean vehicule;
	private boolean driverLicense;
	private Nationality nationality;

	public String getCommentary() {
		return this.commentary;
	}

	public String getContractDuration() {
		return this.contractDuration;
	}

	public String getContractType() {
		return this.contractType;
	}

	public String getGrade() {
		return this.grade;
	}

	public List<String> getMobilities() {
		return this.mobilities;
	}

	public Nationality getNationality() {
		return this.nationality;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public List<String> getSkills() {
		return this.skills;
	}

	public String getStartAt() {
		return this.startAt;
	}

	public boolean isDriverLicense() {
		return this.driverLicense;
	}

	public boolean isVehicule() {
		return this.vehicule;
	}

	public void setCommentary(final String commentary) {
		this.commentary = commentary;
	}

	public void setContractDuration(final String contractDuration) {
		this.contractDuration = contractDuration;
	}

	public void setContractType(final String contractType) {
		this.contractType = contractType;
	}

	public void setDriverLicense(final boolean permis) {
		this.driverLicense = permis;
	}

	public void setGrade(final String grade) {
		this.grade = grade;
	}

	public void setMobilities(final List<String> mobilities) {
		this.mobilities = mobilities;
	}

	public void setNationality(final Nationality nationality) {
		this.nationality = nationality;
	}

	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setSkills(final List<String> skills) {
		this.skills = skills;
	}

	public void setStartAt(final String startAt) {
		this.startAt = startAt;
	}

	public void setVehicule(final boolean vehicule) {
		this.vehicule = vehicule;
	}

}
