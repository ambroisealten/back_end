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

	public List<String> getMobilities() {
		return mobilities;
	}

	public void setMobilities(List<String> mobilities) {
		this.mobilities = mobilities;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStartAt() {
		return startAt;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
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

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getContractDuration() {
		return contractDuration;
	}

	public void setContractDuration(String contractDuration) {
		this.contractDuration = contractDuration;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	public boolean isVehicule() {
		return vehicule;
	}

	public void setVehicule(boolean vehicule) {
		this.vehicule = vehicule;
	}

	public boolean isPermis() {
		return permis;
	}

	public void setPermis(boolean permis) {
		this.permis = permis;
	}

	public Nationality getNationality() {
		return nationality;
	}

	public void setNationality(Nationality nationality) {
		this.nationality = nationality;
	}

}
