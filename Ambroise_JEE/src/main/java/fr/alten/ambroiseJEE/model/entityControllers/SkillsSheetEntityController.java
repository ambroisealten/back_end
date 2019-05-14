package fr.alten.ambroiseJEE.model.entityControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import fr.alten.ambroiseJEE.controller.business.FileBusinessController;
import fr.alten.ambroiseJEE.model.PersonSetWithFilters;
import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.beans.SkillGraduated;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.Constants;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Skills Sheet controller for entity gestion rules
 * 
 * @author Lucas Royackkers
 *
 */
@Service
public class SkillsSheetEntityController {
	@Autowired
	private SkillsSheetRepository skillsSheetRepository;

	@Autowired
	private PersonEntityController personEntityController;

	@Autowired
	private SkillEntityController skillEntityController;

	@Autowired
	private FileBusinessController fileBusinessController;

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
	 * Method to create a new Skills Sheet, first version or as an update
	 *
	 * @param jSkillsSheet  the JsonNode containing all the parameters
	 * @param versionNumber the versionNumber of the Skills Sheet
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ResourceNotFoundException} if there is no such
	 *         resource as the one that are given, {@link ConflictException} if
	 *         there is a conflict in the database and {@link CreatedException} if
	 *         the skills sheet is created
	 * @author Lucas Royackkers
	 */
	private HttpException createSkillsSheet(final JsonNode jSkillsSheet, final long versionNumber) {
		try {

			final PersonRole status = PersonRole.valueOf(jSkillsSheet.get("rolePersonAttachedTo").textValue());
			final String personMail = jSkillsSheet.get("mailPersonAttachedTo").textValue();
			final String skillsSheetName = jSkillsSheet.get("name").textValue();

			if (this.skillsSheetRepository.existsByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(
					skillsSheetName, personMail, versionNumber)) {
				return new ConflictException();
			}
			// Given the created person status
			Person personAttachedTo = null;
			switch (status) {
			case CONSULTANT:
				personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail,
						PersonRole.CONSULTANT);
				break;
			case APPLICANT:
				personAttachedTo = this.personEntityController.getPersonByMailAndType(personMail, PersonRole.APPLICANT);
				break;
			default:
				return new ResourceNotFoundException();
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

			if (jSkillsSheet.has("cv")) {
				newSkillsSheet.setCvPerson(this.fileBusinessController
						.getDocument(jSkillsSheet.get("cv").get("_id").textValue(), UserRole.MANAGER_ADMIN));
			} else {
				newSkillsSheet.setCvPerson(null);
			}

			newSkillsSheet.setMailVersionAuthor(jSkillsSheet.get("mailVersionAuthor").textValue());

			newSkillsSheet.setVersionDate(String.valueOf(System.currentTimeMillis()));

			this.skillsSheetRepository.save(newSkillsSheet);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			e.printStackTrace();

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
			Skill skill = null;
			try {
				skill = this.skillEntityController.getSkill(skillName);

			} catch (ResourceNotFoundException e) {
				skill = this.skillEntityController.createSkill(skillName, null).get();
			}

			final double skillGrade = skillGraduated.get("grade").asDouble();
			if (checkGrade(skillGrade)) {
				allSkills.add(new SkillGraduated(skill, skillGrade));
			}
		}
		return allSkills;
	}

	/**
	 * Get a fiability grade given the average of Soft and Tech SKills and the
	 * opinion of the Person as : fiability = opinion x AVERAGE(SUM(Soft) +
	 * SUM(Tech))
	 *
	 * @param skillsSheet the skills sheet
	 * @param opinion     the opinion of the Person
	 * @param skillsList  the list of skills that are searched (can be empty)
	 * @return a double that indicates the fiability grade
	 * @author Lucas Royackkers
	 */
	private double getFiabilityGrade(final SkillsSheet skillsSheet, final String opinion, List<String> skillsList) {
		Double averageSoft = 0.0;
		Double averageTech = 0.0;
		int totalTech = 0;
		int totalSoft = 0;

		if (skillsList.isEmpty()) {
			skillsList = new ArrayList<String>(Constants.DEFAULT_SKILLS);
			skillsList.replaceAll(String::toLowerCase);
		}

		for (final SkillGraduated skillGraduated : skillsSheet.getSkillsList()) {
			final Skill skill = skillGraduated.getSkill();
			if (skill.isSoft()) {
				averageSoft += skillGraduated.getGrade();
				totalSoft++;
			} else if (skillsList.contains(skill.getName().toLowerCase())) {
				averageTech += skillGraduated.getGrade();
				totalTech++;
			}

		}
		averageSoft = (totalTech + totalSoft != 0) ? (averageSoft + averageTech) / (totalTech + totalSoft) : 0.0;

		return this.getOpinionMultiplier(averageSoft, opinion);

	}

	/**
	 * Defines the rules when we multiply the "fiability" grade with a value
	 * representing the opinion of the person attached to a Skills Sheet. Here, we
	 * defines the rules of our calculation.
	 * 
	 * @param averageSkill the average grade of the Skills in the Skills Sheet
	 * @param opinion      the opinion of the Person attached to the Skills Sheet
	 * @return a double that represents the final grade of a Person
	 * @author Lucas Royackkers
	 */
	private double getOpinionMultiplier(Double averageSkill, String opinion) {
		if (averageSkill != 0.0) {
			switch (opinion) {
			case "+++":
				averageSkill *= 1;
				break;
			case "++":
				averageSkill *= 0.7;
				break;
			case "+":
				averageSkill *= 0.6;
				break;
			case "-":
				averageSkill *= 0.4;
				break;
			case "--":
				averageSkill *= 0.2;
				break;
			case "---":
				averageSkill *= 0.1;
				break;
			case "NOK":
				averageSkill = 0.0;
				break;
			default:
				averageSkill = 0.0;
				break;
			}
		}

		return averageSkill;
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
	public Optional<SkillsSheet> getSkillsSheet(final String name, final String personMail, final long versionNumber) {
		return this.skillsSheetRepository.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(name,
				personMail, versionNumber);
	}

	/**
	 * Try to fetch all skills sheets
	 *
	 * @return A List with all skills sheets (might be empty) sorted by date
	 * @author Lucas Royackkers, Camille Schnell
	 */
	public List<JsonNode> getSkillsSheets() {
		final List<SkillsSheet> allSkillsSheets = this.skillsSheetRepository.findAll();
		final List<JsonNode> finalResult = new ArrayList<JsonNode>();
		final ObjectMapper mapper = new ObjectMapper();
		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.create();

		for (final SkillsSheet skillsSheet : allSkillsSheets) {
			final long latestVersionNumber = this.skillsSheetRepository
					.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseOrderByVersionNumberDesc(
							skillsSheet.getName(), skillsSheet.getMailPersonAttachedTo())
					.get(0).getVersionNumber();
			if (skillsSheet.getVersionNumber() == latestVersionNumber) {
				try {
					final JsonNode jResult = mapper.createObjectNode();
					((ObjectNode) jResult).set("skillsSheet", JsonUtils.toJsonNode(gson.toJson(skillsSheet)));
					((ObjectNode) jResult).set("person", JsonUtils.toJsonNode(gson.toJson(
							this.personEntityController.getPersonByMail(skillsSheet.getMailPersonAttachedTo()))));
					finalResult.add(jResult);
				} catch (final IOException e) {
					LoggerFactory.getLogger(SkillsSheetEntityController.class).error(e.getMessage());
				}
			}

		}
		finalResult.sort((e1, e2) -> (Double.valueOf(e2.get("skillsSheet").get("versionDate").asDouble()))
				.compareTo(Double.valueOf(e1.get("skillsSheet").get("versionDate").asDouble())));

		return finalResult;
	}

	public double getAverageSoftSkillsGrade(JsonNode jSkillsList) {
		double sumGrades = 0;
		int softSkillNb = 0;
		for (JsonNode skill : jSkillsList) {
			if (skill.get("skill").get("isSoft") != null) {
				sumGrades += skill.get("grade").asDouble();
				softSkillNb++;
			}
		}
		
		return softSkillNb > 0 ? sumGrades / softSkillNb : 1;
	}
	
	/**
	 * Sort a skills sheets' list given a specific sort field
	 * 
	 * @param listToSort JsonNode containing skillsSheets' list to sort
	 * @param fieldSort field to sort on
	 * @param isAsc boolean if true sort ascending, else descending
	 * @return a sorted list of skillsSheets
	 * @author Camille Schnell
	 */
	public List<JsonNode> getSkillsSheetsWithFieldSorting(List<JsonNode> listToSort, String fieldSort, boolean isAsc) {
		List<JsonNode> finalResult = listToSort;
		if (fieldSort.equals("softskillsAverage")) { // sort on soft skill average grade
			finalResult.sort(
					(e1, e2) -> (Double.valueOf(getAverageSoftSkillsGrade(e1.get("skillsSheet").get("skillsList"))))
							.compareTo(Double.valueOf(getAverageSoftSkillsGrade(e2.get("skillsSheet").get("skillsList")))));
		} else if (fieldSort.equals("name") || fieldSort.equals("job") || fieldSort.equals("opinion")
				|| fieldSort.equals("disponibility")) { // sort on specific identity field
			if (fieldSort.equals("name")) { // compare name + surname string
				finalResult.sort((e1,e2) -> (e1.get("person").get("surname").textValue()).compareToIgnoreCase(e2.get("person").get("surname").textValue()));
			} else {
				finalResult.sort((e1, e2) -> (e1.get("person").get(fieldSort).textValue())
						.compareToIgnoreCase((e2.get("person").get(fieldSort).textValue())));
			}
		} else { // sort on specific skill grade field
			return null;
		}

		if (!isAsc) {
			Collections.reverse(finalResult);
		}
		
		return finalResult;
	}

	/**
	 * Get all Skills Sheets that match the given filters
	 *
	 * @param identity the filters about the Person (name, surname, job, etc.)
	 * @param skills   the filters about Skills (name)
	 * @return a List of Skills Sheets that match the query
	 * @author Lucas Royackkers, Camille Schnell
	 */
	public List<JsonNode> getSkillsSheetsByIdentityAndSkills(final String identity, final String skills,
			final String columnSorting) {

		// If there is no parameters given (e.g. a space for identity and skills
		// filter), returns all skills sheets
		if ((identity.length() == 1 && identity.equals(",")) && (skills.length() == 1 && skills.equals(","))) {
			// if we need to sort given a specific column
			if (!columnSorting.equals(",")) {
				String fieldSort = columnSorting.split(",")[0];
				boolean isAsc = columnSorting.split(",")[1].equals("asc");
				return getSkillsSheetsWithFieldSorting(getSkillsSheets(), fieldSort, isAsc);
			} else {
				return getSkillsSheets();
			}
		}

		// Initialize variables
		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.create();
		final ObjectMapper mapper = new ObjectMapper();
		final List<String> identitiesList = Arrays.asList(identity.split(","));
		final List<String> skillsList = Arrays.asList(skills.toLowerCase().split(","));

		final HashSet<Skill> filteredSkills = new HashSet<Skill>();
		final List<Person> allPersons = this.personEntityController.getAllPersons();

		final PersonSetWithFilters filteredPersons = new PersonSetWithFilters(identitiesList);

		filteredPersons.addAll(allPersons);

		// Get all Skills in the filter that are in the database
		for (final String skillFilter : skillsList) {
			Skill filterSkill = new Skill();
			filterSkill.setName(skillFilter);
			filteredSkills.add(filterSkill);
		}

		final Set<SkillsSheet> result = new HashSet<SkillsSheet>();
		final List<JsonNode> finalResult = new ArrayList<JsonNode>();

		// First, filter the skills sheets given the Persons object that we get before
		// (given their mail)
		for (final Person person : filteredPersons) {
			final SkillsSheet skillsSheetExample = new SkillsSheet();
			skillsSheetExample.setMailPersonAttachedTo(person.getMail());

			final ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues()
					.withMatcher("mailPersonAttachedTo", GenericPropertyMatchers.exact())
					.withIgnorePaths("versionNumber");
			final List<SkillsSheet> personSkillSheet = this.skillsSheetRepository
					.findAll(Example.of(skillsSheetExample, matcher));
			result.addAll(personSkillSheet);
		}

		// Secondly, filter the skills sheets on the Skills object (the skills sheet
		// have to match all the skills given in the filter)
		for (final SkillsSheet skillSheet : result) {
			boolean skillsMatch = true;
			if (!filteredSkills.isEmpty()) {
				for (final Skill skill : filteredSkills) {
					skillsMatch = skillsMatch && ifSkillsInSheet(skill, skillSheet);
				}
			}
			// If there is a total match on the skills in the skills sheet
			if (skillsMatch) {
				final long latestVersionNumber = this.skillsSheetRepository
						.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseOrderByVersionNumberDesc(
								skillSheet.getName(), skillSheet.getMailPersonAttachedTo())
						.get(0).getVersionNumber();
				// If the skills is the latest to date, put it in the final result
				if (skillSheet.getVersionNumber() == latestVersionNumber) {
					final String personMail = skillSheet.getMailPersonAttachedTo();
					// Build a JsonNode with Skills Sheet and Person objects together, if not throw
					// an Exception
					try {
						final JsonNode jResult = mapper.createObjectNode();
						((ObjectNode) jResult).set("skillsSheet", JsonUtils.toJsonNode(gson.toJson(skillSheet)));
						final Person person = this.personEntityController.getPersonByMail(personMail);
						((ObjectNode) jResult).set("person", JsonUtils.toJsonNode(gson.toJson(person)));
						((ObjectNode) jResult).put("fiability",
								getFiabilityGrade(skillSheet, person.getOpinion(), skillsList));
						finalResult.add(jResult);
					} catch (final IOException e) {
						LoggerFactory.getLogger(SkillsSheetEntityController.class).error(e.getMessage());
					}
				}
			}
		}
		
		// if we need to sort given a specific column
		if (!columnSorting.equals(",")) {
			String columnSort = columnSorting.split(",")[0];
			boolean isAsc = columnSorting.split(",")[1].equals("asc");
			return getSkillsSheetsWithFieldSorting(finalResult, columnSort, isAsc);
		}
		else { // sorted by default on fiability grade
			finalResult.sort((e1, e2) -> Double.valueOf(e2.get("fiability").asDouble())
					.compareTo(Double.valueOf(e1.get("fiability").asDouble())));
			return finalResult;
		}
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
		final List<SkillsSheet> finalResult = new ArrayList<SkillsSheet>();
		final List<SkillsSheet> skillsSheetList = this.skillsSheetRepository
				.findByMailPersonAttachedToIgnoreCase(mailPerson);
		for (final SkillsSheet skillsSheet : skillsSheetList) {
			final long latestVersionNumber = this.skillsSheetRepository
					.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseOrderByVersionNumberDesc(
							skillsSheet.getName(), skillsSheet.getMailPersonAttachedTo())
					.get(0).getVersionNumber();
			if (skillsSheet.getVersionNumber() == latestVersionNumber) {
				finalResult.add(skillsSheet);
			}
		}
		return finalResult;
	}

	/**
	 * Try to fetch skills sheets by a name
	 *
	 * @param name the skills sheet's name to fetch
	 * @return A list of Skills sheets (might be empty if there is no match)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheetsByName(final String name) {
		return this.skillsSheetRepository.findByNameIgnoreCase(name);
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
	public List<SkillsSheet> getSkillsSheetVersion(final String name, final String mail) {
		return this.skillsSheetRepository
				.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseOrderByVersionNumberDesc(name, mail);
	}

	/**
	 * Check if a Skill is contained in a specific SkillsSheet
	 *
	 * @param filterSkill the Skill that we are searching for
	 * @param skillSheet  the skills Sheet
	 * @return true if the Skill is in the Skills Sheet, else false
	 * @author Lucas Royackkers
	 */
	public boolean ifSkillsInSheet(final Skill filterSkill, final SkillsSheet skillSheet) {
		for (final SkillGraduated skillGraduated : skillSheet.getSkillsList()) {
			if (skillGraduated.getSkill().getName().toLowerCase().equals(filterSkill.getName())) {
				return true;
			}
		}
		return false;
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
	 * Method to update a CV on a Skills Sheet, the update save a new version of the
	 * skills sheet with the new CV in it
	 * 
	 * @param cv                   the CV as a File
	 * @param name                 the name of the Skills Sheet
	 * @param mailPersonAttachedTo the mail of the person attached to this Skills
	 *                             Sheet
	 * @param versionNumber        the version number of this Skills Sheet
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request {@link ResourceNotFoundException} if the resource is not
	 *         found, {@link ConflictException} if there is a conflict in the
	 *         database and {@link OkException} if the skills sheet is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateSkillsSheetCV(final File cv, final String name, final String mailPersonAttachedTo,
			final long versionNumber) {
		try {

			final SkillsSheet skillsSheet = this.skillsSheetRepository
					.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(name, mailPersonAttachedTo,
							versionNumber)
					.orElseThrow(ResourceNotFoundException::new);

			if (this.skillsSheetRepository.existsByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(name,
					mailPersonAttachedTo, versionNumber + 1)) {
				return new ConflictException();
			}

			skillsSheet.setCvPerson(cv);
			skillsSheet.set_id(null);
			skillsSheet.setVersionNumber(versionNumber + 1);

			this.skillsSheetRepository.save(skillsSheet);

		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * Checks if a specific version of this Skills Sheet exists
	 * 
	 * @param name          the name of the Skills List
	 * @param mailPerson    the mail of the person attached to this Skills Sheet
	 * @param versionNumber the verion number of this Skills Sheet
	 * @return true if the specific version of this Skills Sheet exists, otherwise
	 *         false
	 * @author Lucas Royackkers
	 */
	public boolean checkIfSkillsSheetVersion(String name, String mailPerson, long versionNumber) {
		boolean check = false;
		List<SkillsSheet> skillsSheetList = this.getSkillsSheetVersion(name, mailPerson);
		for (SkillsSheet skillsSheet : skillsSheetList) {
			if (skillsSheet.getVersionNumber() == versionNumber) {
				check = true;
			}
		}
		return check;
	}

}
