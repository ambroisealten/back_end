/**
 *
 */
package fr.alten.ambroiseJEE.model.beans;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import fr.alten.ambroiseJEE.utils.Nationality;

/**
 * @author Andy Chabalier
 *
 */
@Document(collection = "applicantForum")
public class ApplicantForum extends Person {

	private static final long serialVersionUID = -6269027301156154990L;

	private List<String> mobilities;
	private String phoneNumber;
	private String startAt;
	private String grade;
	private String commentary;
	private String contractType;
	private String contractDuration;
	private List<String> skills;
	private boolean vehicule;
	private boolean permis;
	private Nationality nationality;

	public String getCommentary() {
		return commentary;
	}

	public String getContractDuration() {
		return contractDuration;
	}

	public String getContractType() {
		return contractType;
	}

	public String getGrade() {
		return grade;
	}

	public List<String> getMobilities() {
		return mobilities;
	}

	public Nationality getNationality() {
		return nationality;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public List<String> getSkills() {
		return skills;
	}

	public String getStartAt() {
		return startAt;
	}

	public boolean isPermis() {
		return permis;
	}

	public boolean isVehicule() {
		return vehicule;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public void setContractDuration(String contractDuration) {
		this.contractDuration = contractDuration;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public void setMobilities(List<String> mobilities) {
		this.mobilities = mobilities;
	}

	public void setNationality(Nationality nationality) {
		this.nationality = nationality;
	}

	public void setPermis(boolean permis) {
		this.permis = permis;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	public void setVehicule(boolean vehicule) {
		this.vehicule = vehicule;
	}

}
