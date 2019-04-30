package fr.alten.ambroiseJEE.model.entityControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	 * Get a List of Skills Sheet (at the latest version) given a mail of a person
	 * attached to it
	 * 
	 * @param mailPerson the mail of the person
	 * @return a List of Skills Sheets that match the given parameters
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetsByMail(final String mailPerson) {
		List<SkillsSheet> finalResult = new ArrayList<SkillsSheet>();
		List<SkillsSheet> skillsSheetList = this.skillsSheetRepository.findByMailPersonAttachedTo(mailPerson);
		for (SkillsSheet skillsSheet : skillsSheetList) {
			long latestVersionNumber = this.skillsSheetRepository
					.findByNameAndMailPersonAttachedToOrderByVersionNumberDesc(skillsSheet.getName(),
							skillsSheet.getMailPersonAttachedTo())
					.get(0).getVersionNumber();
			if (skillsSheet.getVersionNumber() == latestVersionNumber) {
				finalResult.add(skillsSheet);
			}
		}
		return finalResult;
	}

	/**
	 * Method to create a new Skills Sheet (whatever time it is)
	 * 
	 * @param jSkillsSheet  the JsonNode containing all the parameters
	 * @param versionNumber the versionNumber of the Skills Sheet
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skills sheet is created
	 * @author Lucas Royackkers
	 */
	private HttpException createSkillsSheet(final JsonNode jSkillsSheet, long versionNumber) {
		try {

			final String status = jSkillsSheet.get("rolePersonAttachedTo").textValue();
			final String personMail = jSkillsSheet.get("mailPersonAttachedTo").textValue();
			final String skillsSheetName = jSkillsSheet.get("name").textValue();

			if (this.skillsSheetRepository
					.findByNameAndMailPersonAttachedToAndVersionNumber(skillsSheetName, personMail, versionNumber)
					.isPresent()) {
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
			newSkillsSheet.setVersionNumber(versionNumber);

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
	 * Method to create a skills sheet for the first time
	 *
	 * @param jUser JsonNode with all skills sheet parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skills sheet is created
	 * @author Lucas Royackkers
	 */
	public HttpException createSkillsSheet(final JsonNode jSkillsSheet) {
		return this.createSkillsSheet(jSkillsSheet, 1);

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
	 * Try to fetch all skills sheets
	 *
	 * @return A List with all skills sheets (might be empty)
	 * @author Lucas Royackkers
	 */
	public List<JsonNode> getSkillsSheets() {
		List<SkillsSheet> allSkillsSheets = this.skillsSheetRepository.findAll();
		List<JsonNode> finalResult = new ArrayList<JsonNode>();
		ObjectMapper mapper = new ObjectMapper();
		final GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		for (SkillsSheet skillsSheet : allSkillsSheets) {
			long latestVersionNumber = this.skillsSheetRepository
					.findByNameAndMailPersonAttachedToOrderByVersionNumberDesc(skillsSheet.getName(),
							skillsSheet.getMailPersonAttachedTo())
					.get(0).getVersionNumber();
			if (skillsSheet.getVersionNumber() == latestVersionNumber) {
				try {
					JsonNode jResult = mapper.createObjectNode();
					((ObjectNode) jResult).set("skillsSheet", JsonUtils.toJsonNode(gson.toJson(skillsSheet)));
					((ObjectNode) jResult).set("person", JsonUtils.toJsonNode(gson.toJson(
							this.personEntityController.getPersonByMail(skillsSheet.getMailPersonAttachedTo()))));
					finalResult.add(jResult);
				} catch (IOException e) {
					LoggerFactory.getLogger(SkillsSheetEntityController.class).error(e.getMessage());
				}
			}

		}
		return finalResult;
	}

	/**
	 * Get a specific Skills Sheet given a name, the mail and the versionNumber
	 * 
	 * @param name          the name of the skills sheet
	 * @param personMail    the mail of the person attached to
	 * @param versionNumber the vesion number of the skills sheet
	 * @return a Skill Sheet if there is a match, an empty Optional if not
	 * @author Lucas Royackkers
	 */
	public Optional<SkillsSheet> getSkillsSheet(String name, String personMail, long versionNumber) {
		return this.skillsSheetRepository.findByNameAndMailPersonAttachedToAndVersionNumber(name, personMail,
				versionNumber);
	}

	/**
	 * Get all Skills Sheets that match the given filters
	 *
	 * @param identity the filters about the Person (name, surname, job, etc.)
	 * @param skills   the filters about Skills (name)
	 * @return a List of Skills Sheets that match the query
	 * @author Lucas Royackkers
	 */
	public List<JsonNode> getSkillsSheetsByIdentityAndSkills(final String identity, final String skills) {
		if (identity.length() == 1 && skills.length() == 1) {
			return this.getSkillsSheets();
		}
		final GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		ObjectMapper mapper = new ObjectMapper();
		final String[] identitiesList = identity.split(",");
		final String[] skillsList = skills.split(",");

		final HashSet<Skill> filteredSkills = new HashSet<Skill>();
		final HashSet<Person> filteredPersons = new HashSet<Person>();

		if (!(identitiesList.length == 1 && identitiesList[0].equals(" "))) {
			for (final String identityFilter : identitiesList) {
				filteredPersons.addAll(new HashSet<Person>(this.personEntityController.getPersonsByName(identityFilter)));
				filteredPersons.addAll(new HashSet<Person>(this.personEntityController.getPersonsBySurname(identityFilter)));
				filteredPersons.addAll(new HashSet<Person>(this.personEntityController.getPersonsByHighestDiploma(identityFilter)));
				filteredPersons.addAll(new HashSet<Person>(this.personEntityController.getPersonsByJob(identityFilter)));
			}
		} else {
			filteredPersons.addAll(new HashSet<Person>(personEntityController.getAllPersons()));
		}

		
		for (final String skillFilter : skillsList) {
			this.skillEntityController.getSkill(skillFilter).ifPresent(skill -> filteredSkills.add(skill));
		}

		Set<SkillsSheet> result = new HashSet<SkillsSheet>();
		List<JsonNode> finalResult = new ArrayList<JsonNode>();

		for (Person person : filteredPersons) {
			SkillsSheet skillsSheetExample = new SkillsSheet();
			skillsSheetExample.setMailPersonAttachedTo(person.getMail());

			final ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues()
					.withMatcher("mailPersonAttachedTo", GenericPropertyMatchers.exact())
					.withIgnorePaths("versionNumber");
			List<SkillsSheet> personSkillSheet = this.skillsSheetRepository
					.findAll(Example.of(skillsSheetExample, matcher));
			result.addAll(personSkillSheet);
		}

		for (SkillsSheet skillSheet : result) {
			boolean skillsMatch = true;
			if (!filteredSkills.isEmpty()) {
				for (Skill skill : filteredSkills) {
					skillsMatch = skillsMatch && this.ifSkillsInSheet(skill, skillSheet);
				}
			}
			if (skillsMatch) {
				long latestVersionNumber = this.skillsSheetRepository
						.findByNameAndMailPersonAttachedToOrderByVersionNumberDesc(skillSheet.getName(),
								skillSheet.getMailPersonAttachedTo())
						.get(0).getVersionNumber();
				if (skillSheet.getVersionNumber() == latestVersionNumber) {
					String personResult = skillSheet.getMailPersonAttachedTo();
					try {
						final JsonNode jResult = mapper.createObjectNode();
						((ObjectNode) jResult).set("skillsSheet", JsonUtils.toJsonNode(gson.toJson(skillSheet)));
						((ObjectNode) jResult).set("person", JsonUtils
								.toJsonNode(gson.toJson(this.personEntityController.getPersonByMail(personResult))));
						finalResult.add(jResult);
					} catch (IOException e) {
						LoggerFactory.getLogger(SkillsSheetEntityController.class).error(e.getMessage());
					}
				}
			}
		}

		return finalResult;
	}

	/**
	 * Check if a Skill is contained in a specific SkillsSheet
	 * 
	 * @param filterSkill the Skill that we are searching for
	 * @param skillSheet  the skills Sheet
	 * @return true if the Skill is in the Skills Sheet, else false
	 * @author Lucas Royackkers
	 */
	public boolean ifSkillsInSheet(Skill filterSkill, SkillsSheet skillSheet) {
		for (SkillGraduated skillGraduated : skillSheet.getSkillsList()) {
			if (skillGraduated.getSkill().getName().equals(filterSkill.getName())) {
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
	 *                     name to perform an update on the database
	 *
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request {@link ResourceNotFoundException} if the resource is not
	 *         found, {@link ConflictException} if there is a conflict in the
	 *         database and {@link OkException} if the skills sheet is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateSkillsSheet(final JsonNode jSkillsSheet) {
		try {
			// We retrieve the latest version number of the skills sheet, in order to
			// increment it later
			final long latestVersionNumber = jSkillsSheet.get("versionNumber").longValue();

			return this.createSkillsSheet(jSkillsSheet, latestVersionNumber + 1);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
	}

	/**
	 * Get a List of Skills Sheet (with different versions) given their common name
	 * and mail of the person attached to
	 * 
	 * @param name the name of the skills sheet
	 * @param mail the mail of the person attached to it
	 * @return a List of Skills Sheet that matches the given parameters (can be
	 *         empty)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetVersion(String name, String mail) {
		return this.skillsSheetRepository.findByNameAndMailPersonAttachedToOrderByVersionNumberDesc(name, mail);
	}

}
