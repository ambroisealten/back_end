package fr.alten.ambroiseJEE.model.entityControllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

@Service
public class SkillsSheetEntityController {
	@Autowired
	private SkillsSheetRepository skillsSheetRepository;

	@Autowired
	private PersonEntityController personEntityController;

	@Autowired
	private SkillEntityController skillEntityController;

	@Autowired
	private UserEntityController userEntityController;

	/**
	 * Method to create a skills sheet.
	 *
	 * @param jUser JsonNode with all skills sheet parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skills sheet is created
	 * @author Lucas Royackkers
	 */
	public HttpException createSkillsSheet(JsonNode jSkillsSheet) {
		SkillsSheet newSkillsSheet = new SkillsSheet();
		String skillsSheetName = jSkillsSheet.get("name").textValue();
		newSkillsSheet.setName(skillsSheetName);

		Optional<Person> personAttachedTo;
		String status = jSkillsSheet.get("rolePersonAttachedTo").textValue();
		String personMail = jSkillsSheet.get("mailPersonAttachedTo").textValue();
		// Given the created person status
		switch (status) {
		case "consultant":
			personAttachedTo = personEntityController.getPersonByMailAndType(personMail, PersonRole.CONSULTANT);
			break;
		default:
			personAttachedTo = personEntityController.getPersonByMailAndType(personMail, PersonRole.APPLICANT);
			break;
		}
		if (personAttachedTo.isPresent()) {
			newSkillsSheet.setMailPersonAttachedTo(personAttachedTo.get().getMail());
		}		
		newSkillsSheet.setRolePersonAttachedTo(status);
		// Get all skills given several lists of skills (tech and soft)
		newSkillsSheet.setSkillsList(this.getAllSkills(jSkillsSheet.get("skillsList")));

		// Set an Id and a Version Number on this skills sheet (initialization to 1 for the version number)
		newSkillsSheet.setVersionNumber(1);
		newSkillsSheet.set_id(new ObjectId());

		String authorMail = jSkillsSheet.get("mailVersionAuthor").textValue();
		Optional<User> userAuthor = userEntityController.getUserByMail(authorMail);
		if (userAuthor.isPresent()) {
			newSkillsSheet.setMailVersionAuthor(userAuthor.get().getMail());
		}

		newSkillsSheet.setVersionDate(LocalDateTime.now().toString());

		try {
			skillsSheetRepository.save(newSkillsSheet);
		} catch (Exception e) {
			return new ConflictException();
		}

		return new CreatedException();
	}

	/**
	 * Get a List of Skills object given a JsonNode containing a List of
	 * Skills object
	 *
	 * @param jSkills the JsonNode containing all skills for this skill
	 *                    sheet
	 * @return A List of Skill (might be empty if there is no match)
	 * @author Lucas Royackkers
	 */
	public List<Skill> getAllSkills(JsonNode jSkills) {
		List<Skill> allSkills = new ArrayList<Skill>();

		for (JsonNode skill : jSkills) {
			Optional<Skill> newSkillOptional = skillEntityController.getSkill(skill.get("name").textValue());
			// Get a specific soft skill by its name in the JsonNode
			if (newSkillOptional.isPresent()) {
				Skill newSkill = newSkillOptional.get(); 
				if(this.checkGrade(skill.get("grade").asDouble())) {
					allSkills.add(newSkill);
				}
			}
		}
		return allSkills;
	}

	/**
	 *  Checks if a Skill has a grade (Double) in a good format
	 * @param d the skill's grade (a Double)
	 * @return a boolean if the grade is in a good format
	 * @author Thomas Decamp
	 */
    private boolean checkGrade(double d) {
        if (d == 1 || d == 2 || d == 2.5 || d == 3 || d == 3.5 || d == 4)
            return true;
        return false;
    }

	/**
	 * Try to fetch an skills sheet by its name and its versionNumber
	 *
	 * @param name          the skills sheet's name to fetch
	 * @param versionNumber the skills sheet's version number
	 * @return An Optional with the corresponding skills sheet or not.
	 * @author Lucas Royackkers
	 */
	public Optional<SkillsSheet> getSkillsSheetByNameAndVersionNumber(String name, long versionNumber) {
		return skillsSheetRepository.findByNameAndVersionNumber(name, versionNumber);
	}

	/**
	 * Try to fetch all skills sheets
	 *
	 * @return A List with all skills sheets (might be empty)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheets() {
		return skillsSheetRepository.findAll();
	}

	/**
	 * Try to fetch skills sheets by a name
	 *
	 * @param name the skills sheet's name to fetch
	 * @return A list of Skills sheets (might be empty if there is no match)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetsByName(String name) {
		return skillsSheetRepository.findByName(name);
	}

	/**
	 * Method to update a Skills Sheet, the update save a new version of the skills
	 * sheet
	 *
	 * @param jSkillsSheet JsonNode with all skills sheet parameters, including its
	 *                     name (which cannot be changed) to perform an update on
	 *                     the database
	 *
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request {@link ResourceNotFoundException} if the resource is not
	 *         found, {@link ConflictException} if there is a conflict in the database
	 *         and {@link OkException} if the skills sheet is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateSkillsSheet(JsonNode jSkillsSheet) {
		// We retrieve the latest version number of the skills sheet, in order to
		// increment it later
		long latestVersionNumber = jSkillsSheet.get("versionNumber").longValue();
		Optional<SkillsSheet> skillsSheetOptional = this
				.getSkillsSheetByNameAndVersionNumber(jSkillsSheet.get("name").textValue(), latestVersionNumber);
		// If we find the skills sheet, with its name and its version (the Front part
		// will have to send the latest version number)
		if (skillsSheetOptional.isPresent()) {
			SkillsSheet skillsSheet = skillsSheetOptional.get();

			Optional<Person> personAttachedTo;
			String status = jSkillsSheet.get("rolePersonAttachedTo").textValue();
			String personMail = jSkillsSheet.get("mailPersonAttachedTo").textValue();
			switch (status) {
			case "consultant":
				personAttachedTo = personEntityController.getPersonByMailAndType(personMail, PersonRole.CONSULTANT);
				break;
			default:
				personAttachedTo = personEntityController.getPersonByMailAndType(personMail, PersonRole.APPLICANT);
				break;
			}
			if (personAttachedTo.isPresent()) {
				skillsSheet.setMailPersonAttachedTo(personAttachedTo.get().getMail());
			}
			skillsSheet.setRolePersonAttachedTo(status);

			skillsSheet.setSkillsList(this.getAllSkills(jSkillsSheet.get("skillsList")));

			skillsSheet.setVersionNumber(latestVersionNumber + 1);

			Optional<SkillsSheet> newVersionSkillsSheet = this.getSkillsSheetByNameAndVersionNumber(
					jSkillsSheet.get("name").textValue(), latestVersionNumber + 1);
			if (newVersionSkillsSheet.isPresent()) {
				return new ConflictException();
			}

			skillsSheet.set_id(new ObjectId());

			String authorMail = jSkillsSheet.get("mailVersionAuthor").textValue();
			Optional<User> userAuthor = userEntityController.getUserByMail(authorMail);
			if (userAuthor.isPresent()) {
				skillsSheet.setMailVersionAuthor(userAuthor.get().getMail());
			}

			skillsSheet.setVersionDate(LocalDateTime.now().toString());

			skillsSheetRepository.save(skillsSheet);
		} else {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * Check if a mail has already been used for a skills sheet
	 * 
	 * @param mailPerson the mail of a person linked (or not) to a skills sheet
	 * @return a boolean if the person has been linked to a skills sheet
	 * @author Lucas Royackkers
	 */
	public boolean checkIfSkillsWithMailExists(String mailPerson) {
		List<SkillsSheet> listSkillsSheet = skillsSheetRepository.findByMailPersonAttachedTo(mailPerson);
		return (listSkillsSheet.size() > 0);
	}

	/**
	 * Get all Skills Sheets that match the given filters 
	 * 
	 * @param identity the filters about the Person (name, surname, job, etc.)
	 * @param skills the filters about Skills (name)
	 * @return a List of Skills Sheets that match the query
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetsByIdentityAndSkills(String identity, String skills) {
		String[] identitiesList = identity.split(",");
		String[] skillsList = skills.split(",");
		
		List<Skill> filteredSkills = new ArrayList<Skill>();
		List<Person> filteredPersons = new ArrayList<Person>();
		HashMap<String,Person> mailToPerson = new HashMap<String,Person>();
		
		for(String identityFilter : identitiesList) {
			filteredPersons.addAll(personEntityController.getPersonsByName(identityFilter));
			filteredPersons.addAll(personEntityController.getPersonsBySurname(identityFilter));
			filteredPersons.addAll(personEntityController.getPersonsByHighestDiploma(identityFilter));
			filteredPersons.addAll(personEntityController.getPersonsByJob(identityFilter));
		}
		
		for(Person filteredPerson : filteredPersons) {
			mailToPerson.put(filteredPerson.getMail(), filteredPerson);
		}
		
		for(String skillFilter : skillsList) {
			filteredSkills.add(skillEntityController.getSkill(skillFilter).get());
		}
		
		// Do the matching
		
		return null;
	}

}
