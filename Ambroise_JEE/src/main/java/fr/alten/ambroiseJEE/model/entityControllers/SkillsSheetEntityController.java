package fr.alten.ambroiseJEE.model.entityControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

	private final ObjectMapper mapper;

	private final Gson gson;

	public SkillsSheetEntityController() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
		this.mapper = new ObjectMapper();
	}

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
	 * Checks if a specific version of this Skills Sheet exists
	 *
	 * @param name          the name of the Skills List
	 * @param mailPerson    the mail of the person attached to this Skills Sheet
	 * @param versionNumber the verion number of this Skills Sheet
	 * @return true if the specific version of this Skills Sheet exists, otherwise
	 *         false
	 * @author Lucas Royackkers
	 */
	public boolean checkIfSkillsSheetVersion(final String name, final String mailPerson, final long versionNumber) {
		boolean check = false;
		final List<SkillsSheet> skillsSheetList = getSkillsSheetVersion(name, mailPerson);
		for (final SkillsSheet skillsSheet : skillsSheetList) {
			if (skillsSheet.getVersionNumber() == versionNumber) {
				check = true;
			}
		}
		return check;
	}

	/**
	 * Compare two skill grades from two different skillsList from 2 skillsSheets,
	 * in order to sort a skillsSheets' list
	 *
	 * @param skillSheet1 JsonNode containing skillsList of first skillsSheet
	 * @param skillSheet2 JsonNode containing skillsList of seconde skillsSheet
	 * @param fieldSort   String name of the skill to sort on
	 * @return 0 if grades are equal, <0 if grade1 < grade 2, >0 if grade1 > grade2
	 * @author Camille Schnell
	 */
	private int compareSpecificSkillGrades(final JsonNode skillSheet1, final JsonNode skillSheet2,
			final String fieldSort) {
		// get first grade corresponding to "fieldSort" skill
		final JsonNode skillsList1 = skillSheet1.get("skillsSheet").get("skillsList");
		double grade1 = 0.0;
		for (final JsonNode skill : skillsList1) {
			if (skill.get("skill").get("name").textValue().equals(fieldSort)) {
				grade1 = Double.valueOf(skill.get("grade").asDouble());
				break;
			}
		}

		// get second grade corresponding to "fieldSort" skill
		final JsonNode skillsList2 = skillSheet2.get("skillsSheet").get("skillsList");
		double grade2 = 0.0;
		for (final JsonNode skill : skillsList2) {
			if (skill.get("skill").get("name").textValue().equals(fieldSort)) {
				grade2 = Double.valueOf(skill.get("grade").asDouble());
				break;
			}
		}

		// compare both grades
		return Double.valueOf(grade1).compareTo(Double.valueOf(grade2));
	}

	/**
	 * Method to create a new Skills Sheet, first version or as an update
	 *
	 * @param jSkillsSheet  the JsonNode containing all the parameters
	 * @param versionNumber the versionNumber of the Skills Sheet
	 * @param versionAuthor the mail of the author of this version of this Skills
	 *                      Sheet
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ResourceNotFoundException} if there is no such
	 *         resource as the one that are given, {@link ConflictException} if
	 *         there is a conflict in the database and {@link CreatedException} if
	 *         the skills sheet is created
	 * @author Lucas Royackkers
	 */
	private HttpException createSkillsSheet(final JsonNode jSkillsSheet, final long versionNumber,
			final String versionAuthor) {
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

			newSkillsSheet.setMailVersionAuthor(versionAuthor);

			newSkillsSheet.setVersionDate(String.valueOf(System.currentTimeMillis()));

			newSkillsSheet.setSoftSkillAverage(softSkillAverageCalculation(newSkillsSheet.getSkillsList()));

			newSkillsSheet.setComment(jSkillsSheet.get("comment").textValue());

			this.skillsSheetRepository.save(newSkillsSheet);
			
			return new CreatedException(this.gson.toJson(newSkillsSheet));
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			e.printStackTrace();

			return new ConflictException();
		}
		
	}

	/**
	 * Method to create a skills sheet for the first time
	 *
	 * @param jUser         JsonNode with all skills sheet parameters
	 * @param versionAuthor the mail of the author of this version of this Skills
	 *                      Sheet
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skills sheet is created
	 * @author Lucas Royackkers
	 */
	public HttpException createSkillsSheet(final JsonNode jSkillsSheet, final String versionAuthor) {
		return this.createSkillsSheet(jSkillsSheet, 1, versionAuthor);

	}

	/**
	 * @param columnSorting
	 * @return
	 * @author Andy Chabalier
	 */
	public List<JsonNode> getAllAndSortByField(final String columnSorting) {
		if (columnSorting.equals(",")) {
			return getSkillsSheets();
		} else {
			final String fieldSort = columnSorting.split(",")[0];
			// -1 is call to reverse order, 1 to keep natural order
			final int order = columnSorting.split(",")[1].equals("asc") ? 1 : -1;
			return getSkillsSheetsWithFieldSorting(getSkillsSheets(), fieldSort, order);
		}
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
		final List<String> softSkillsUsed = new ArrayList<String>();
		final List<Skill> softSkillsList = this.skillEntityController.getSoftSkills();

		for (final JsonNode skillGraduated : jSkills) {
			final String skillName = skillGraduated.get("skill").get("name").textValue();
			Skill skill = null;
			if (skillGraduated.get("skill").has("isSoft")) {
				softSkillsUsed.add(skillGraduated.get("skill").get("name").textValue());
			}
			try {
				skill = this.skillEntityController.getSkill(skillName);

			} catch (final ResourceNotFoundException e) {
				skill = this.skillEntityController.createSkill(skillName, null).get();
			}

			final double skillGrade = skillGraduated.get("grade").asDouble();
			if (checkGrade(skillGrade)) {
				allSkills.add(new SkillGraduated(skill, skillGrade));
			}
		}

		for (final Skill softSkill : softSkillsList) {
			final String softSkillName = softSkill.getName();
			if (!softSkillsUsed.contains(softSkillName)) {
				Skill skill = null;
				try {
					skill = this.skillEntityController.getSkill(softSkillName);

				} catch (final ResourceNotFoundException e) {
					skill = this.skillEntityController.createSkill(softSkillName, null).get();
				}

				allSkills.add(new SkillGraduated(skill, 1));
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
			skillsList.stream().forEach(String::toLowerCase);
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
		averageSoft = totalTech + totalSoft != 0 ? (averageSoft + averageTech) / (totalTech + totalSoft) : 0.0;

		return getOpinionMultiplier(averageSoft, opinion);

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
	private double getOpinionMultiplier(Double averageSkill, final String opinion) {
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
	 * @return a Skill Sheet if there is a match
	 * @throws a {@link ResourceNotFoundException} if there is no match
	 * @author Lucas Royackkers
	 */
	public SkillsSheet getSkillsSheet(final String name, final String personMail, final long versionNumber) {
		return this.skillsSheetRepository
				.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(name, personMail, versionNumber)
				.orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Try to fetch all skills sheets
	 *
	 * @return A List with all skills sheets (might be empty) sorted by date
	 * @author Lucas Royackkers, Camille Schnell, Andy Chabalier
	 */
	public List<JsonNode> getSkillsSheets() {
		final List<SkillsSheet> allSkillsSheets = this.skillsSheetRepository.findAll().parallelStream()
				.filter(skillSheet -> !skillSheet.getMailPersonAttachedTo().contains("deactivated"))
				.collect(Collectors.toList());
		final List<JsonNode> finalResult = new ArrayList<JsonNode>();
		final ObjectMapper mapper = new ObjectMapper();
		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.create();
		// Create a stream in allSkillsSheets, sort it by date and for each element
		// fetch the last skill sheet
		allSkillsSheets.parallelStream().sorted((ss1, ss2) -> Double.compare(Double.parseDouble(ss2.getVersionDate()),
				Double.parseDouble(ss1.getVersionDate()))).forEachOrdered(skillsSheet -> {
					final long latestVersionNumber = this.skillsSheetRepository
							.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseOrderByVersionNumberDesc(
									skillsSheet.getName(), skillsSheet.getMailPersonAttachedTo())
							.get(0).getVersionNumber();
					if (skillsSheet.getVersionNumber() == latestVersionNumber) {
						try {
							final JsonNode jResult = mapper.createObjectNode();
							((ObjectNode) jResult).set("skillsSheet", JsonUtils.toJsonNode(gson.toJson(skillsSheet)));
							((ObjectNode) jResult).set("person",
									JsonUtils.toJsonNode(gson.toJson(this.personEntityController
											.getPersonByMail(skillsSheet.getMailPersonAttachedTo()))));
							finalResult.add(jResult);
						} catch (final IOException e) {
							LoggerFactory.getLogger(SkillsSheetEntityController.class).error(e.getMessage());
						}
					}
				});
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
		if (identity.length() == 1 && identity.equals(",") && skills.length() == 1 && skills.equals(",")) {
			return getAllAndSortByField(columnSorting);
		}

		// Initialize variables
		final List<String> identitiesList = Arrays.asList(identity.split(","));
		final List<String> skillsList = Arrays.asList(skills.toLowerCase().split(","));
		final HashSet<Skill> filteredSkills = new HashSet<Skill>();
		final List<Person> allPersons = this.personEntityController.getAllPersons().parallelStream()
				.filter(person -> !person.getMail().contains("deactivated")).collect(Collectors.toList());

		final PersonSetWithFilters filteredPersons = new PersonSetWithFilters(identitiesList);

		filteredPersons.addAll(allPersons);

		// Get all Skills in the filter that are in the database
		skillsList.stream().forEach(skillFilter -> {
			final Skill filterSkill = new Skill();
			filterSkill.setName(skillFilter);
			filteredSkills.add(filterSkill);
		});

		final List<JsonNode> finalResult = new ArrayList<JsonNode>();

		// First, filter the skills sheets given the Persons object that we get before
		// (given their mail)
		filteredPersons.parallelStream().forEach(person -> {
			final SkillsSheet skillsSheetExample = new SkillsSheet();
			skillsSheetExample.setMailPersonAttachedTo(person.getMail());

			final ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues()
					.withMatcher("mailPersonAttachedTo", GenericPropertyMatchers.exact())
					.withIgnorePaths("versionNumber").withIgnorePaths("softSkillAverage");

			this.skillsSheetRepository.findAll(Example.of(skillsSheetExample, matcher)).parallelStream()
					.filter(skillSheet -> !skillSheet.getMailPersonAttachedTo().contains("deactivated"))
					.filter(skillSheet -> skillsMatch(filteredSkills, skillSheet))
					// Secondly, filter the skills sheets on the Skills object (the skills sheet
					// have to match all the skills given in the filter)
					// we filter the stream If there is a total match on the skills in the skills
					// sheet
					.forEach(skillSheet -> {
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
								final JsonNode jResult = this.mapper.createObjectNode();
								((ObjectNode) jResult).set("skillsSheet",
										JsonUtils.toJsonNode(this.gson.toJson(skillSheet)));
								final Person personToFetch = this.personEntityController.getPersonByMail(personMail);
								((ObjectNode) jResult).set("person",
										JsonUtils.toJsonNode(this.gson.toJson(personToFetch)));
								((ObjectNode) jResult).put("fiability",
										getFiabilityGrade(skillSheet, personToFetch.getOpinion(), skillsList));
								finalResult.add(jResult);
							} catch (final IOException e) {
								LoggerFactory.getLogger(SkillsSheetEntityController.class).error(e.getMessage());
							}
						}
					});
		});
		if (columnSorting.equals(",")) {
			return finalResult.parallelStream().sorted((e1, e2) -> Double.valueOf(e2.get("fiability").asDouble())
					.compareTo(Double.valueOf(e1.get("fiability").asDouble()))).collect(Collectors.toList());
		} else {
			final String fieldSort = columnSorting.split(",")[0];
			// -1 is call to reverse order, 1 to keep natural order
			final int order = columnSorting.split(",")[1].equals("asc") ? 1 : -1;
			return getSkillsSheetsWithFieldSorting(finalResult, fieldSort, order).parallelStream()
					.collect(Collectors.toList());
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
	 * Sort a skills sheets' list given a specific sort field
	 *
	 * @param listToSort JsonNode containing skillsSheets' list to sort
	 * @param fieldSort  field to sort on
	 * @param order      -1 is call to reverse order, 1 to keep natural order
	 * @return a sorted list of skillsSheets
	 * @author Camille Schnell, Andy Chabalier
	 */
	private List<JsonNode> getSkillsSheetsWithFieldSorting(final List<JsonNode> listToSort, final String fieldSort,
			final int order) {
		switch (fieldSort) {
		case "softskillsAverage":
			return listToSort.parallelStream().sorted((e1, e2) -> order * softSkillAverageComparator(e1, e2))
					.collect(Collectors.toList());
		case "job":
			return listToSort.parallelStream()
					.sorted((e1, e2) -> order * personIdentityFieldComparator(fieldSort, e1, e2))
					.collect(Collectors.toList());
		case "opinion":
			return listToSort.parallelStream()
					.sorted((e1, e2) -> order * personIdentityFieldComparator(fieldSort, e1, e2))
					.collect(Collectors.toList());
		case "disponibility":
			return listToSort.parallelStream()
					.sorted((e1, e2) -> order * personIdentityFieldComparator(fieldSort, e1, e2))
					.collect(Collectors.toList());
		case "name":
			return listToSort.parallelStream().sorted((e1, e2) -> order * personNameComparator(e1, e2))
					.collect(Collectors.toList());
		default:
			return listToSort.parallelStream().sorted((e1, e2) -> order * compareSpecificSkillGrades(e1, e2, fieldSort))
					.collect(Collectors.toList());
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
	 * @param fieldSort
	 * @param e1
	 * @param e2
	 * @return
	 * @author Andy Chabalier
	 */
	private int personIdentityFieldComparator(final String fieldSort, final JsonNode e1, final JsonNode e2) {
		return e1.get("person").get(fieldSort).textValue()
				.compareToIgnoreCase(e2.get("person").get(fieldSort).textValue());
	}

	/**
	 * @param e1
	 * @param e2
	 * @return
	 * @author Andy Chabalier
	 */
	private int personNameComparator(final JsonNode e1, final JsonNode e2) {
		return (e1.get("person").get("name").textValue() + e1.get("person").get("surname").textValue())
				.compareToIgnoreCase(
						e2.get("person").get("name").textValue() + e2.get("person").get("surname").textValue());
	}

	/**
	 * @param filteredSkills
	 * @param skillSheet
	 * @return
	 * @author Andy Chabalier
	 */
	private boolean skillsMatch(final HashSet<Skill> filteredSkills, final SkillsSheet skillSheet) {
		boolean skillsMatch = true;
		if (!filteredSkills.isEmpty()) {
			for (final Skill skill : filteredSkills) {
				skillsMatch = skillsMatch && ifSkillsInSheet(skill, skillSheet);
			}
		}
		return skillsMatch;
	}

	private double softSkillAverageCalculation(final List<SkillGraduated> softSkillList) {

		double sum = 0;
		int count = 0;

		for (final SkillGraduated skill : softSkillList) {
			if (skill.isSoft()) {
				sum += skill.getGrade();
				count++;
			}
		}

		final double average = count != 0 ? sum / count : 1;

		return (double) Math.round(average * 100) / 100;
	}

	/**
	 * @param e1
	 * @param e2
	 * @return
	 * @author Andy Chabalier
	 */
	private int softSkillAverageComparator(final JsonNode e1, final JsonNode e2) {
		return Double.valueOf(e1.get("skillsSheet").get("softSkillAverage").asDouble())
				.compareTo(Double.valueOf(e2.get("skillsSheet").get("softSkillAverage").asDouble()));
	}

	/**
	 * Method to update a Skills Sheet, the update save a new version of the skills
	 * sheet
	 *
	 * @param jSkillsSheet  JsonNode with all skills sheet parameters, including its
	 *                      name to perform an update on the database
	 * @param versionAuthor the mail of the author of this version of this Skills
	 *                      Sheet
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request {@link ResourceNotFoundException} if the resource is not
	 *         found, {@link ConflictException} if there is a conflict in the
	 *         database and {@link OkException} if the skills sheet is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateSkillsSheet(final JsonNode jSkillsSheet, final String versionAuthor) {
		try {
			// We retrieve the latest version number of the skills sheet, in order to
			// increment it later
			final long latestVersionNumber = jSkillsSheet.get("versionNumber").longValue();

			return this.createSkillsSheet(jSkillsSheet, latestVersionNumber + 1, versionAuthor);
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
			if (this.skillsSheetRepository.existsByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(name,
					mailPersonAttachedTo, versionNumber + 1)) {
				return new ConflictException();
			}
			final SkillsSheet skillsSheet = this.skillsSheetRepository
					.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(name, mailPersonAttachedTo,
							versionNumber)
					.orElseThrow(ResourceNotFoundException::new);

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

}
