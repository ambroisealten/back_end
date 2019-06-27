package fr.alten.ambroiseJEE.model.entityControllers;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.PersonRepository;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
import fr.alten.ambroiseJEE.utils.MailUtils;
import fr.alten.ambroiseJEE.utils.availability.Availability;
import fr.alten.ambroiseJEE.utils.exception.MissingFieldException;
import fr.alten.ambroiseJEE.utils.exception.ToManyFieldsException;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;
import fr.alten.ambroiseJEE.utils.personRole.PersonRole;
import fr.alten.ambroiseJEE.utils.personRole.PersonRoleTranslate;

/**
 * @author Lucas Royackkers
 *
 */
@Service
public class PersonEntityController {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private UserEntityController userEntityController;

	@Autowired
	private DiplomaEntityController diplomaEntityController;

	@Autowired
	private EmployerEntityController employerEntityController;

	@Autowired
	private JobEntityController jobEntityController;

	@Autowired
	private SkillsSheetRepository skillsSheetRepository;

	/**
	 * Method to create a Person. Person type will be defined by business
	 * controllers ahead of this object.
	 *
	 * @param jPerson            JsonNode with all Person parameters, except its
	 *                           type (name, mail, job, monthlyWage, startDate)
	 * @param type               PersonEnum the type of the created Person
	 * @param personInChargeMail TODO
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ResourceNotFoundException} if there is a problem in
	 *         the parameters given, {@link ConflictException} if there is a
	 *         conflict in the database and {@link CreatedException} if the person
	 *         is created
	 * @author Lucas Royackkers, Kylian Gehier
	 */
	public HttpException createPerson(final JsonNode jPerson, final PersonRole type, final String personInChargeMail) {
		try {
			// if the mail don't match with the mail pattern
			if (!validateMail(jPerson.get("mail").textValue())) {
				return new UnprocessableEntityException();
			}

			final Person newPerson = new Person();
			newPerson.setSurname(jPerson.get("surname").textValue());
			newPerson.setName(jPerson.get("name").textValue());
			newPerson.setMonthlyWage(Float.parseFloat(jPerson.get("monthlyWage").asText()));
			newPerson.setExperienceTime(Integer.parseInt(jPerson.get("experienceTime").asText()));
			newPerson.setRole(type);
			newPerson.setMail(jPerson.get("mail").textValue());

			final User personInCharge = this.userEntityController.getUserByMail(personInChargeMail);
			newPerson.setPersonInChargeMail(personInCharge.getMail());

			final String highestDiploma = jPerson.get("highestDiploma").textValue();
			final String highestDiplomaYear = jPerson.get("highestDiplomaYear").textValue();
			Diploma diploma;
			try {
				diploma = this.diplomaEntityController.getDiplomaByNameAndYearOfResult(highestDiploma,
						highestDiplomaYear);
			} catch (final ResourceNotFoundException e) {
				diploma = this.diplomaEntityController.createDiploma(highestDiploma, highestDiplomaYear);
			}

			newPerson.setHighestDiploma(diploma.getName());
			newPerson.setHighestDiplomaYear(diploma.getYearOfResult());

			final String jobName = jPerson.get("job").textValue();
			Job job;
			try {
				job = this.jobEntityController.getJob(jobName);
			} catch (final ResourceNotFoundException e) {
				job = this.jobEntityController.createJob(jobName);
			}
			newPerson.setJob(job.getTitle());

			if (type.equals(PersonRole.APPLICANT)) {
				newPerson.setExperienceTime(jPerson.get("experienceTime").asInt());
				final String employerName = jPerson.get("employer").textValue();
				Employer employer;
				try {
					employer = this.employerEntityController.getEmployer(employerName);

				} catch (final ResourceNotFoundException e) {
					employer = this.employerEntityController.createEmployer(employerName);
				}
				newPerson.setEmployer(employer.getName());

				if (jPerson.has("availability")) {
					final JsonNode jAvailability = jPerson.get("availability");
					newPerson.setAvailability(new Availability(jAvailability.get("initDate").asLong(),
							jAvailability.get("finalDate").asLong(), jAvailability.get("duration").asInt(),
							ChronoUnit.valueOf(jAvailability.get("durationType").textValue())));
				}

			}
			newPerson.setOpinion(jPerson.get("opinion").textValue());

			this.personRepository.save(newPerson);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final DuplicateKeyException dke) {
			return new ConflictException();
		}
		return new CreatedException();

	}

	/**
	 * Method to delete a Person. Person type will be defined by business
	 * controllers ahead of this object.
	 *
	 * @param jPerson contains at least the person's name
	 * @param role    the role of person
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in
	 *         the database and {@link OkException} if the person is deleted
	 * @author Lucas Royackkers
	 */
	public HttpException deletePerson(final String mail) {
		try {
			final Person person = getPersonByMail(mail);

			switch (person.getRole()) {
			case APPLICANT:
				person.setSurname("Deactivated");
				person.setName("Deactivated");
				break;
			case CONSULTANT:
				person.setSurname("Demissionnaire");
				person.setName("Demissionnaire");
				break;
			default:
				throw new UnprocessableEntityException();
			}
			person.setMail("deactivated" + System.currentTimeMillis() + "@deactivated.com");
			person.setEmployer(null);
			person.setRole(PersonRole.DEMISSIONNAIRE);
			person.setMonthlyWage(0);
			person.setJob(null);
			person.setOpinion(null);
			updatePersonMailOnSkillSheetOnCascade(mail, person.getMail());
			this.personRepository.save(person);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final DuplicateKeyException e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * Method to delete a Person. Person type will be defined by business
	 * controllers ahead of this object.
	 *
	 * @param jPerson contains at least the person's name
	 * @param role    the role of person
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in
	 *         the database and {@link OkException} if the person is deleted
	 * @author Lucas Royackkers
	 */
	public HttpException deletePersonByRole(final JsonNode jPerson, final PersonRole role) {
		try {
			final String mail = jPerson.get("mail").textValue();
			final Person person = getPersonByMailAndType(mail, role);

			switch (role) {
			case APPLICANT:
				person.setSurname("Deactivated");
				person.setName("Deactivated");
				break;
			case CONSULTANT:
				person.setSurname("Demissionnaire");
				person.setName("Demissionnaire");
				break;
			default:
				throw new UnprocessableEntityException();

			}
			person.setMail("deactivated" + System.currentTimeMillis() + "@deactivated.com");
			person.setEmployer(null);
			person.setRole(PersonRole.DEMISSIONNAIRE);
			person.setMonthlyWage(0);
			person.setJob(null);
			person.setOpinion(null);
			updatePersonMailOnSkillSheetOnCascade(mail, person.getMail());
			this.personRepository.save(person);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final DuplicateKeyException e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * Get a List of all Persons in the database
	 *
	 * @return the list of all persons
	 * @author Lucas Royackkers
	 */
	public List<Person> getAllPersons() {
		return this.personRepository.findAll();
	}

	/**
	 * Try to fetch persons by a mail
	 *
	 * @param mail the person's mail to fetch
	 * @return A List of Person that match the searched mail (can be empty).
	 * @throws @{@link ResourceNotFoundException} if the resource is not found
	 * @author Lucas Royackkers
	 * @author Camille Schnell
	 */
	public Person getPersonByMail(final String mail) {
		return this.personRepository.findByMail(mail).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Try to fetch a person by its mail and type
	 *
	 * @param mail the person's mail to fetch
	 * @return An Optional with the corresponding person (of the given type) or not.
	 * @author Lucas Royackkers
	 * @throws {@link ResourceNotFoundException} if the resource can't be found
	 *
	 */
	public Person getPersonByMailAndType(final String mail, final PersonRole type) {
		return this.personRepository.findByMailAndRole(mail, type).orElseThrow(ResourceNotFoundException::new);

	}

	/**
	 * Get a List of Person given a Diploma
	 *
	 * @param highestDiploma the highest Diploma of the person
	 * @return the list of all persons
	 * @author Lucas Royackkers
	 */
	public List<Person> getPersonsByHighestDiploma(final String highestDiploma) {
		return this.personRepository.findByHighestDiploma(highestDiploma);
	}

	/**
	 * Get a List of Person given a Job
	 *
	 * @param job the job of the person
	 * @return the list of all persons
	 * @author Lucas Royackkers
	 */
	public List<Person> getPersonsByJob(final String job) {
		return this.personRepository.findByJob(job);
	}

	/**
	 * Get a List of Person given a name
	 *
	 * @param name the name of the person
	 * @return the list of all persons
	 * @author Lucas Royackkers
	 */
	public List<Person> getPersonsByName(final String name) {
		return this.personRepository.findByName(name);
	}

	/**
	 * Get a List of Person given a role
	 *
	 * @param role the type of persons that are searched
	 * @return the list of all persons
	 * @author Lucas Royackkers
	 */
	public List<Person> getPersonsByRole(final PersonRole role) {
		return this.personRepository.findAllByRole(role);
	}

	/**
	 * Get a List of Person given a surname
	 *
	 * @param surname the surname of the person
	 * @return the list of all persons
	 * @author Lucas Royackkers
	 */
	public List<Person> getPersonsBySurname(final String surname) {
		return this.personRepository.findBySurname(surname);
	}

	/**
	 *
	 * @param jOnTimeAvailability
	 * @return
	 * @author Kylian Gehier
	 */
	public boolean hasOnDateAvailabilityFields(final JsonNode jOnTimeAvailability)
			throws ToManyFieldsException, MissingFieldException {
		final Long a = jOnTimeAvailability.get("finalDate").asLong();
		if (a != 0) {
			final int b = jOnTimeAvailability.get("duration").asInt();
			final String c = jOnTimeAvailability.get("durationType").textValue();
			if (b == 0 && c == "") {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 *
	 * @param jOnTimeAvailability
	 * @return
	 * @author Kylian Gehier
	 */
	public boolean hasOnTimeAvailabilityFields(final JsonNode jOnTimeAvailability)
			throws ToManyFieldsException, MissingFieldException {
		final Long c = jOnTimeAvailability.get("finalDate").asLong();
		if (c == 0) {
			final int a = jOnTimeAvailability.get("duration").asInt();
			final String b = jOnTimeAvailability.get("durationType").textValue();
			final boolean test = b.equals("");
			if (a >= 0 && !test) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Method to update a Person. Person type will be defined by business
	 * controllers ahead of this object.
	 *
	 * @param jPerson            JsonNode containing all parameters
	 * @param role               the role of the concerned person (if it's an
	 *                           applicant or a consultant)
	 * @param personInChargeMail TODO
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in
	 *         the database and {@link OkException} if the person is updated
	 * @author Lucas Royackkers, Kylian Gehier, Thomas Decamp
	 */
	public HttpException updatePerson(final JsonNode jPerson, final PersonRole role, final String personInChargeMail) {
		try {
			final String oldMail = jPerson.get("mail").textValue();
			final Person person = getPersonByMailAndType(oldMail, role);

			person.setSurname(jPerson.get("surname").textValue());
			person.setName(jPerson.get("name").textValue());
			person.setMonthlyWage(Float.parseFloat(jPerson.get("monthlyWage").asText()));

			if (jPerson.hasNonNull("newRole"))
				person.setRole(PersonRoleTranslate.translateRole(jPerson.get("newRole").textValue()));
			else
				person.setRole(role);

			final User personInCharge = this.userEntityController.getUserByMail(personInChargeMail);
			person.setPersonInChargeMail(personInCharge.getMail());

			final String highestDiploma = jPerson.get("highestDiploma").textValue();
			final String highestDiplomaYear = jPerson.get("highestDiplomaYear").textValue();

			Diploma diploma;
			try {
				diploma = this.diplomaEntityController.getDiplomaByNameAndYearOfResult(highestDiploma,
						highestDiplomaYear);
			} catch (final ResourceNotFoundException e) {
				diploma = this.diplomaEntityController.createDiploma(highestDiploma, highestDiplomaYear);
			}

			person.setHighestDiploma(diploma.getName());
			person.setHighestDiplomaYear(diploma.getYearOfResult());

			if (person.getRole().equals(PersonRole.APPLICANT)) {
				person.setExperienceTime(jPerson.get("experienceTime").asInt());
				final String employerName = jPerson.get("employer").textValue();
				Employer employer;
				try {
					employer = this.employerEntityController.getEmployer(employerName);

				} catch (final ResourceNotFoundException e) {
					employer = this.employerEntityController.createEmployer(employerName);
				}
				person.setEmployer(employer.getName());

				if (jPerson.has("availability")) {
					final JsonNode jAvailability = jPerson.get("availability");
					person.setAvailability(new Availability(jAvailability.get("initDate").asLong(),
							jAvailability.get("finalDate").asLong(), jAvailability.get("duration").asInt(),
							ChronoUnit.valueOf(jAvailability.get("durationType").textValue())));
				}

			}
			
			final String jobName = jPerson.get("job").textValue();
			Job job;
			try {
				job = this.jobEntityController.getJob(jobName);
			} catch (ResourceNotFoundException e) {
				job = (Job) this.jobEntityController.createJob(jobName);
			}
			person.setJob(job.getTitle());

			person.setOpinion(jPerson.get("opinion").textValue());
			updatePersonMailOnSkillSheetOnCascade(oldMail, person.getMail());
			this.personRepository.save(person);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final DuplicateKeyException dke) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * update associated skillSheet on cascade
	 *
	 * @param oldMail
	 * @param newMail
	 * @author Andy Chabalier
	 */
	private void updatePersonMailOnSkillSheetOnCascade(final String oldMail, final String newMail) {
		final List<SkillsSheet> skillSheets = this.skillsSheetRepository.findByMailPersonAttachedToIgnoreCase(oldMail);
		skillSheets.parallelStream().forEach(skillSheet -> {
			skillSheet.setMailPersonAttachedTo(newMail);
		});
		this.skillsSheetRepository.saveAll(skillSheets);
	}

	/**
	 * @param jPerson
	 * @return
	 * @author Andy Chabalier
	 */
	public boolean validateMail(final String mail) {
		return MailUtils.validateMail(mail);
	}

}
