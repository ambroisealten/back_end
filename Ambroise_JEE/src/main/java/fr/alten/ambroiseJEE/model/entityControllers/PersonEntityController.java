package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.PersonRepository;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * @author Lucas Royackkers
 *
 */
@Service
public class PersonEntityController {
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Method to validate if the mail math with the mail pattern
	 *
	 * @param emailStr the string to validate
	 * @return true if the string match with the mail pattern
	 * @author Lucas Royackkers
	 */
	private static boolean validateMail(final String emailStr) {
		final Matcher matcher = PersonEntityController.VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

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

	/**
	 * Method to create a Person. Person type will be defined by business
	 * controllers ahead of this object.
	 *
	 * @param jPerson JsonNode with all Person parameters, except its type (name,
	 *                mail, job, monthlyWage, startDate)
	 * @param type    PersonEnum the type of the created Person
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the person is created
	 * @author Lucas Royackkers
	 */
	public HttpException createPerson(final JsonNode jPerson, final PersonRole type) {
		try {
			// if the mail don't match with the mail pattern
			if (!PersonEntityController.validateMail(jPerson.get("mail").textValue())) {
				return new UnprocessableEntityException();
			}

			final Person newPerson = new Person();
			newPerson.setSurname(jPerson.get("surname").textValue());
			newPerson.setName(jPerson.get("name").textValue());
			newPerson.setMonthlyWage(Float.parseFloat(jPerson.get("monthlyWage").textValue()));
			newPerson.setRole(type);
			newPerson.setMail(jPerson.get("mail").textValue());


			final User personInCharge = this.userEntityController
					.getUserByMail(jPerson.get("personInChargeMail").textValue());
			newPerson.setPersonInChargeMail(personInCharge.getMail());

			final String highestDiploma = jPerson.get("highestDiploma").textValue();
			final String highestDiplomaYear = jPerson.get("highestDiplomaYear").textValue();
			final Diploma diploma = this.diplomaEntityController
					.getDiplomaByNameAndYearOfResult(highestDiploma, highestDiplomaYear)
					.orElseGet(this.diplomaEntityController.createDiploma(highestDiploma, highestDiplomaYear));

			newPerson.setHighestDiploma(diploma.getName());
			newPerson.setHighestDiplomaYear(diploma.getYearOfResult());

			final String jobName = jPerson.get("job").textValue();
			Job job;
			try {
				job = this.jobEntityController.getJob(jobName);
			} catch (ResourceNotFoundException e) {
				job = (Job) this.jobEntityController.createJob(jobName);
			}
			newPerson.setJob(job.getTitle());

			final String employerName = jPerson.get("employer").textValue();
			Employer employer;
			try {
				employer = this.employerEntityController.getEmployer(employerName);
				
			}
			catch (ResourceNotFoundException e) {
				employer = (Employer) this.employerEntityController.createEmployer(employerName);
			}
			newPerson.setEmployer(employer.getName());
			
			newPerson.setOpinion(jPerson.get("opinion").textValue());

			this.personRepository.save(newPerson);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
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
	public HttpException deletePerson(final JsonNode jPerson, final PersonRole role) {
		try {
			final Person person = this.personRepository.findByMailAndRole(jPerson.get("mail").textValue(), role)
					.orElseThrow(ResourceNotFoundException::new);

			switch (role) {
			case APPLICANT:
				person.setSurname("Deactivated");
				person.setName("Deactivated");
				break;
			default:
				person.setSurname("Demissionaire");
				person.setName("Demissionaire");
				break;
			}
			person.setMail("deactivated" + System.currentTimeMillis() + "@deactivated.com");
			person.setEmployer(null);
			person.setRole(PersonRole.DEMISSIONAIRE);
			person.setMonthlyWage(0);
			person.setJob(null);
			person.setOpinion(null);

			this.personRepository.save(person);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
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
	 * Get a List of all Persons in the database
	 * 
	 * @return the list of all persons
	 * @author Lucas Royackkers
	 */
	public List<Person> getAllPersons(){
		return this.personRepository.findAll();
	}

	/**
	 * Method to update a Person. Person type will be defined by business
	 * controllers ahead of this object.
	 *
	 * @param jPerson JsonNode containing all parameters
	 * @param role    the role of the concerned person (if it's an applicant or a
	 *                consultant)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in
	 *         the database and {@link OkException} if the person is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updatePerson(final JsonNode jPerson, final PersonRole role) {
		try {
			final Person person = this.personRepository.findByMailAndRole(jPerson.get("mail").textValue(), role)
					.orElseThrow(ResourceNotFoundException::new);

			person.setSurname(jPerson.get("surname").textValue());
			person.setName(jPerson.get("name").textValue());
			person.setMonthlyWage(Float.parseFloat(jPerson.get("monthlyWage").textValue()));

			person.setRole(role);

			final User personInCharge = this.userEntityController
					.getUserByMail(jPerson.get("personInChargeMail").textValue());
			person.setPersonInChargeMail(personInCharge.getMail());

			final String highestDiploma = jPerson.get("highestDiploma").textValue();
			final String highestDiplomaYear = jPerson.get("highestDiplomaYear").textValue();

			final Diploma diploma = this.diplomaEntityController
					.getDiplomaByNameAndYearOfResult(highestDiploma, highestDiplomaYear)
					.orElseGet(this.diplomaEntityController.createDiploma(highestDiploma, highestDiplomaYear));

			person.setHighestDiploma(diploma.getName());
			person.setHighestDiplomaYear(diploma.getYearOfResult());

			final String jobName = jPerson.get("job").textValue();
			Job job;
			try {
				job = this.jobEntityController.getJob(jobName);
			} catch (ResourceNotFoundException e) {
				job = (Job) this.jobEntityController.createJob(jobName);
			}
			person.setJob(job.getTitle());

			final String employerName = jPerson.get("employer").textValue();
			Employer employer;
			try {
				employer = this.employerEntityController.getEmployer(employerName);
				
			}
			catch (ResourceNotFoundException e) {
				employer = (Employer) this.employerEntityController.createEmployer(employerName);
			}
			
			person.setEmployer(employer.getName());
			
			person.setOpinion(jPerson.get("opinion").textValue());
			
			this.personRepository.save(person);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

}
