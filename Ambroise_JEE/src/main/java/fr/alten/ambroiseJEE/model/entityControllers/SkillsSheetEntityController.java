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

import fr.alten.ambroiseJEE.model.SkillGraduated;
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
	 * Checks if a Skill has a grade (Double) in a good format
	 *
	 * @param d the skill's grade (a Double)
	 * @return a boolean if the grade is in a good format
	 * @author Thomas Decamp
	 */
	private boolean checkGrade(final double d) {
		return d == 1 || d == 2 || d == 2.5 || d == 3 || d == 3.5 || d == 4;
	}

	/**
	 * Check if a mail has already been used for a skills sheet
	 *
	 * @param mailPerson the mail of a person linked (or not) to a skills sheet
	 * @return a boolean if the person has been linked to a skills sheet
	 * @author Lucas Royackkers
	 */
	public boolean checkIfSkillsWithMailExists(final String mailPerson) {
		final List<SkillsSheet> listSkillsSheet = this.skillsSheetRepository.findByMailPersonAttachedTo(mailPerson);
		return listSkillsSheet.size() > 0;
	}

	/**
	 * Method to create a skills sheet.
	 *
	 * @param jUser JsonNode with all skills sheet parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skills sheet is created
	 * @author Lucas Royackkers
	 */
	public HttpException createSkillsSheet(final JsonNode jSkillsSheet) {
		final SkillsSheet newSkillsSheet = new SkillsSheet();
		final String skillsSheetName = jSkillsSheet.get("name").textValue();
		newSkillsSheet.setName(skillsSheetName);

		Optional<Person> personAttachedTo;
		final String status = jSkillsSheet.get("rolePersonAttachedTo").textValue();
		final String personMail = jSkillsSheet.get("mailPersonAttachedTo").textValue();
		// Given the created person status
		switch (status) {
		case "consultant":
			personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail, PersonRole.CONSULTANT);
			break;
		default:
			personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail, PersonRole.APPLICANT);
			break;
		}
		if (personAttachedTo.isPresent()) {
			newSkillsSheet.setMailPersonAttachedTo(personAttachedTo.get().getMail());
		}
		newSkillsSheet.setRolePersonAttachedTo(status);
		// Get all skills given several lists of skills (tech and soft)
		newSkillsSheet.setSkillsList(getAllSkills(jSkillsSheet.get("skillsList")));

		// Set an Id and a Version Number on this skills sheet (initialization to 1 for
		// the version number)
		newSkillsSheet.setVersionNumber(1);
		newSkillsSheet.set_id(new ObjectId());

		final String authorMail = jSkillsSheet.get("mailVersionAuthor").textValue();
		final Optional<User> userAuthor = this.userEntityController.getUserByMail(authorMail);
		if (userAuthor.isPresent()) {
			newSkillsSheet.setMailVersionAuthor(userAuthor.get().getMail());
		}

		newSkillsSheet.setVersionDate(LocalDateTime.now().toString());

		try {
			this.skillsSheetRepository.save(newSkillsSheet);
		} catch (final Exception e) {
			return new ConflictException();
		}

		return new CreatedException();
	}

	/**
	 * Get a List of Skills object given a JsonNode containing a List of Skills
	 * object
	 *
	 * @param jSkills the JsonNode containing all skills for this skill sheet
	 * @return A List of Skill (might be empty if there is no match)
	 * @author Lucas Royackkers
	 */
	public List<SkillGraduated> getAllSkills(final JsonNode jSkills) {
		final List<SkillGraduated> allSkills = new ArrayList<SkillGraduated>();

		for (final JsonNode skillGraduated : jSkills) {
			final Optional<Skill> newSkillOptional = this.skillEntityController
					.getSkill(skillGraduated.get("skill").get("name").textValue());
			// Get a specific soft skill by its name in the JsonNode
			if (newSkillOptional.isPresent()) {
				final double skillGrade = skillGraduated.get("grade").asDouble();
				if (checkGrade(skillGrade)) {
					allSkills.add(new SkillGraduated(newSkillOptional.get(), skillGrade));
				}
			} else {
				// Add a new Skill in the db
			}
		}
		return allSkills;
	}

	/**
	 * Try to fetch an skills sheet by its name and its versionNumber
	 *
	 * @param name          the skills sheet's name to fetch
	 * @param versionNumber the skills sheet's version number
	 * @return An Optional with the corresponding skills sheet or not.
	 * @author Lucas Royackkers
	 */
	public Optional<SkillsSheet> getSkillsSheetByNameAndVersionNumber(final String name, final long versionNumber) {
		return this.skillsSheetRepository.findByNameAndVersionNumber(name, versionNumber);
	}

	/**
	 * Try to fetch all skills sheets
	 *
	 * @return A List with all skills sheets (might be empty)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheets() {
		return this.skillsSheetRepository.findAll();
	}

	/**
	 * Get all Skills Sheets that match the given filters
	 *
	 * @param identity the filters about the Person (name, surname, job, etc.)
	 * @param skills   the filters about Skills (name)
	 * @return a List of Skills Sheets that match the query
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetsByIdentityAndSkills(final String identity, final String skills) {
		final String[] identitiesList = identity.split(",");
		final String[] skillsList = skills.split(",");

		final List<Skill> filteredSkills = new ArrayList<Skill>();
		final List<Person> filteredPersons = new ArrayList<Person>();
		final HashMap<String, Person> mailToPerson = new HashMap<String, Person>();

		for (final String identityFilter : identitiesList) {
			filteredPersons.addAll(this.personEntityController.getPersonsByName(identityFilter));
			filteredPersons.addAll(this.personEntityController.getPersonsBySurname(identityFilter));
			filteredPersons.addAll(this.personEntityController.getPersonsByHighestDiploma(identityFilter));
			filteredPersons.addAll(this.personEntityController.getPersonsByJob(identityFilter));
		}

		for (final Person filteredPerson : filteredPersons) {
			mailToPerson.put(filteredPerson.getMail(), filteredPerson);
		}

		for (final String skillFilter : skillsList) {
			filteredSkills.add(this.skillEntityController.getSkill(skillFilter).get());
		}

		// Do the matching

		return null;
	}

	/**
	 * Try to fetch skills sheets by a name
	 *
	 * @param name the skills sheet's name to fetch
	 * @return A list of Skills sheets (might be empty if there is no match)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetsByName(final String name) {
		return this.skillsSheetRepository.findByName(name);
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
	 *         found, {@link ConflictException} if there is a conflict in the
	 *         database and {@link OkException} if the skills sheet is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateSkillsSheet(final JsonNode jSkillsSheet) {
		// We retrieve the latest version number of the skills sheet, in order to
		// increment it later
		final long latestVersionNumber = jSkillsSheet.get("versionNumber").longValue();
		final Optional<SkillsSheet> skillsSheetOptional = getSkillsSheetByNameAndVersionNumber(
				jSkillsSheet.get("name").textValue(), latestVersionNumber);
		// If we find the skills sheet, with its name and its version (the Front part
		// will have to send the latest version number)
		if (skillsSheetOptional.isPresent()) {
			final SkillsSheet skillsSheet = skillsSheetOptional.get();

			Optional<Person> personAttachedTo;
			final String status = jSkillsSheet.get("rolePersonAttachedTo").textValue();
			final String personMail = jSkillsSheet.get("mailPersonAttachedTo").textValue();
			switch (status) {
			case "consultant":
				personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail,
						PersonRole.CONSULTANT);
				break;
			default:
				personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail, PersonRole.APPLICANT);
				break;
			}
			if (personAttachedTo.isPresent()) {
				skillsSheet.setMailPersonAttachedTo(personAttachedTo.get().getMail());
			}
			skillsSheet.setRolePersonAttachedTo(status);

			skillsSheet.setSkillsList(getAllSkills(jSkillsSheet.get("skillsList")));

			skillsSheet.setVersionNumber(latestVersionNumber + 1);

			final Optional<SkillsSheet> newVersionSkillsSheet = getSkillsSheetByNameAndVersionNumber(
					jSkillsSheet.get("name").textValue(), latestVersionNumber + 1);
			if (newVersionSkillsSheet.isPresent()) {
				return new ConflictException();
			}

			skillsSheet.set_id(new ObjectId());

			final String authorMail = jSkillsSheet.get("mailVersionAuthor").textValue();
			final Optional<User> userAuthor = this.userEntityController.getUserByMail(authorMail);
			if (userAuthor.isPresent()) {
				skillsSheet.setMailVersionAuthor(userAuthor.get().getMail());
			}

			skillsSheet.setVersionDate(LocalDateTime.now().toString());

			this.skillsSheetRepository.save(skillsSheet);
		} else {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}

}
