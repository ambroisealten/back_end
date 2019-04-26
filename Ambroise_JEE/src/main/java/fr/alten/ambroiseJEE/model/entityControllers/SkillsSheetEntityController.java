package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.SkillGraduated;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
import fr.alten.ambroiseJEE.utils.JsonUtils;
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
		return !this.skillsSheetRepository.findByMailPersonAttachedTo(mailPerson).isEmpty();
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

		try {
			final String status = jSkillsSheet.get("rolePersonAttachedTo").textValue();
			final String personMail = jSkillsSheet.get("mailPersonAttachedTo").textValue();
			final String skillsSheetName = jSkillsSheet.get("name").textValue();

			if (this.skillsSheetRepository
					.findByNameAndMailPersonAttachedToAndVersionNumber(skillsSheetName, personMail, 1).isPresent()) {
				return new ConflictException();
			}
			// Given the created person status
			Person personAttachedTo;
			switch (status) {
			case "consultant":
				personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail,
						PersonRole.CONSULTANT);
				break;
			default:
				personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail, PersonRole.APPLICANT);
				;
				break;
			}

			final SkillsSheet newSkillsSheet = new SkillsSheet();
			newSkillsSheet.setName(skillsSheetName);

			newSkillsSheet.setMailPersonAttachedTo(personAttachedTo.getMail());
			newSkillsSheet.setRolePersonAttachedTo(status);
			// Get all skills given several lists of skills (tech and soft)
			newSkillsSheet.setSkillsList(getAllSkills(jSkillsSheet.get("skillsList")));

			// Set a Version Number on this skills sheet (initialization to 1 for
			// the version number)
			newSkillsSheet.setVersionNumber(1);

			final String authorMail = jSkillsSheet.get("mailVersionAuthor").textValue();
			final User userAuthor = this.userEntityController.getUserByMail(authorMail);
			newSkillsSheet.setMailVersionAuthor(userAuthor.getMail());

			newSkillsSheet.setVersionDate(String.valueOf(System.currentTimeMillis()));

			this.skillsSheetRepository.save(newSkillsSheet);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
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
			final String skillName = skillGraduated.get("skill").get("name").textValue();
			final JsonNode jIsSoft = skillGraduated.get("skill").get("isSoft");
			final Skill skill = this.skillEntityController.getSkill(skillName).orElseGet(
					this.skillEntityController.createSkill(skillName, jIsSoft != null ? jIsSoft.textValue() : null));

			final double skillGrade = skillGraduated.get("grade").asDouble();
			if (checkGrade(skillGrade)) {
				allSkills.add(new SkillGraduated(skill, skillGrade));
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
	public SkillsSheet getSkillsSheetByNameAndVersionNumber(final String name, final long versionNumber) {
		return this.skillsSheetRepository.findByNameAndVersionNumber(name, versionNumber)
				.orElseThrow(ResourceNotFoundException::new);
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
	public Map<JsonNode, SkillsSheet> getSkillsSheetsByIdentityAndSkills(final String identity, final String skills) {
		final String[] identitiesList = identity.split(",");
		final String[] skillsList = skills.split(",");

		final Set<Skill> filteredSkills = new HashSet<Skill>();
		final Set<Person> filteredPersons = new HashSet<Person>();

		for (final String identityFilter : identitiesList) {
			filteredPersons.addAll(new HashSet<Person>(personEntityController.getPersonsByName(identityFilter)));
			filteredPersons.addAll(new HashSet<Person>(this.personEntityController.getPersonsBySurname(identityFilter)));
			filteredPersons.addAll(new HashSet<Person>(this.personEntityController.getPersonsByHighestDiploma(identityFilter)));
			filteredPersons.addAll(new HashSet<Person>(this.personEntityController.getPersonsByJob(identityFilter)));
		}

		for (final String skillFilter : skillsList) {
			this.skillEntityController.getSkill(skillFilter).ifPresent(skill -> filteredSkills.add(skill));
		}

		Set<SkillsSheet> result = new HashSet<SkillsSheet>();
		Map<JsonNode,SkillsSheet> finalResult = new HashMap<JsonNode,SkillsSheet>();
		
		for (Person person : filteredPersons) {
			SkillsSheet skillsSheetExample = new SkillsSheet();
			skillsSheetExample.setMailPersonAttachedTo(person.getMail());

			final ExampleMatcher matcher = ExampleMatcher.matching()
					.withIgnoreNullValues()
					.withMatcher("mailPersonAttachedTo", GenericPropertyMatchers.exact())
					.withIgnorePaths("versionNumber");
			List<SkillsSheet> personSkillSheet = this.skillsSheetRepository.findAll(Example.of(skillsSheetExample, matcher));
			result.addAll(personSkillSheet);
		}
		
		if(!filteredSkills.isEmpty()) {
			for(SkillsSheet skillSheet : result) {
				boolean skillsMatch = true;
				for(Skill skill : filteredSkills) {
					skillsMatch = skillsMatch && this.ifSkillsInSheet(skill, skillSheet);
				}
				//if(skillsMatch) finalResult.put(JsonUtils.toJsonNode(JsonUtils.toJson(this.personEntityController.getPersonByMail(skillSheet.getMailPersonAttachedTo())), skillSheet));
			}
		}
		
		return finalResult;
	}
	
	
	public boolean ifSkillsInSheet(Skill filterSkill,SkillsSheet skillSheet) {
		for(SkillGraduated skillGraduated : skillSheet.getSkillsList()) {
			if(skillGraduated.getSkill().getName().equals(filterSkill.getName())) {
				return true;
			}
		}
		return false;
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
		try {
			final long latestVersionNumber = jSkillsSheet.get("versionNumber").longValue();

			final String skillSheetName = jSkillsSheet.get("name").textValue();
			final String personMail = jSkillsSheet.get("mailPersonAttachedTo").textValue();

			if (this.skillsSheetRepository.findByNameAndMailPersonAttachedToAndVersionNumber(skillSheetName, personMail,
					latestVersionNumber + 1).isPresent()) {
				return new ConflictException();
			}

			final SkillsSheet skillsSheet = getSkillsSheetByNameAndVersionNumber(skillSheetName, latestVersionNumber);
			// If we find the skills sheet, with its name and its version (the Front part
			// will have to send the latest version number)

			Person personAttachedTo;
			final String status = jSkillsSheet.get("rolePersonAttachedTo").textValue();
			switch (status) {
			case "consultant":
				personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail,
						PersonRole.CONSULTANT);
				break;
			default:
				personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail, PersonRole.APPLICANT);
				break;
			}
			skillsSheet.setMailPersonAttachedTo(personAttachedTo.getMail());
			skillsSheet.setRolePersonAttachedTo(status);

			skillsSheet.setSkillsList(getAllSkills(jSkillsSheet.get("skillsList")));

			skillsSheet.setVersionNumber(latestVersionNumber + 1);

			final String authorMail = jSkillsSheet.get("mailVersionAuthor").textValue();
			final User userAuthor = this.userEntityController.getUserByMail(authorMail);
			skillsSheet.setMailVersionAuthor(userAuthor.getMail());

			skillsSheet.setVersionDate(String.valueOf(System.currentTimeMillis()));

			this.skillsSheetRepository.save(skillsSheet);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

}
