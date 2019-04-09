package fr.alten.ambroiseJEE.model.entityControllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.Mobility;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.PersonRepository;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;


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
	private MobilityEntityController mobilityEntityController;

	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);
	
	
	/**
	 * Method to delete a Person. Person type will be defined by business controllers
	 * ahead of this object.
	 * 
	 * @param jPerson contains at least the person's name
	 * @param role the role of person
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in the
	 *         database and {@link OkException} if the person is deleted
	 * @author Lucas Royackkers
	 */
	public HttpException deletePerson(JsonNode jPerson,PersonRole role) {
		Optional<Person> optionalPerson = personRepository.findByMailAndRole(jPerson.get("mail").textValue(), role);
		if(optionalPerson.isPresent()) {
			Person person = optionalPerson.get();
			switch(role) {
			case APPLICANT:
				person.setName("Desactivated");
				break;
			default:
				person.setName("Demissionaire");
				break;
			}
			person.setMail("desactivated" + System.currentTimeMillis()+"@desactivated.com");
			person.setEmployer(null);
			person.setCommentary(null);
			person.setGrade(null);
			person.setCanStartsAt(null);
			person.setUrlDocs(null);
			person.setRole(null);
			person.setFromForum(false);
			person.setMonthlyWage(0);
			person.setJob(null);
			
			personRepository.save(person);
		}
		else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}
	
	/**
	 * Method to update a Person. Person type will be defined by business controllers
	 * ahead of this object.
	 * 
	 * @param jPerson JsonNode containing all parameters
	 * @param role the role of the concerned person (if it's an applicant or a consultant)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in the
	 *         database and {@link OkException} if the person is updated
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	public HttpException updatePerson(JsonNode jPerson,PersonRole role) throws ParseException {
		Optional<Person> optionalPerson = personRepository.findByMailAndRole(jPerson.get("oldMail").textValue(), role);
		if(optionalPerson.isPresent()) {
			Person person = optionalPerson.get();
			person.setName(jPerson.get("name").textValue());
			person.setMonthlyWage(Integer.parseInt(jPerson.get("wage").textValue()));
			person.setCanStartsAt(new SimpleDateFormat("dd/MM/yyyy").parse(jPerson.get("dateStarts").textValue()));
			PersonRole oldRole = person.getRole();
			if(role != oldRole) {
				person.setRole(role);
			}
			person.setMail(jPerson.get("mail").textValue());
			person.setGrade(jPerson.get("grade").textValue());
			person.setCommentary(jPerson.get("commentary").textValue());
			
			Optional<User> managerInCharge = userEntityController.getUserByMail(jPerson.get("managerMail").textValue());
			if(managerInCharge.isPresent()) {
				person.setManagerInCharge(managerInCharge.get().getMail());
			}
			
			Optional<Diploma> highestDiploma = diplomaEntityController.getDiplomaByNameAndYearOfResult(jPerson.get("diplomaName").textValue(),jPerson.get("diplomaYear").textValue());
			if(highestDiploma.isPresent()) {
				Diploma newDiploma = highestDiploma.get();
				person.setHighestDiploma(newDiploma.getName());
			}
			
			person.setMobilities(this.getAllMobilities(jPerson.get("mobilities")));
			
			Optional<Job> job = jobEntityController.getJob(jPerson.get("job").textValue());
			if(job.isPresent()){
				person.setJob(job.get().getTitle());
			}
			
			Optional<Employer> employer = employerEntityController.getEmployer(jPerson.get("employer").textValue());
			if(employer.isPresent()) {
				person.setEmployer(employer.get().getName());
			}
			
			personRepository.save(person);
		}
		else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}
	
	/**
	 * Method to create a Person. Person type will be defined by business controllers
	 * ahead of this object.
	 * 
	 * @param jPerson JsonNode with all Person parameters, except its type (name, mail, job, monthlyWage, startDate)
	 * @param type PersonEnum the type of the created Person
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the person is created
	 * @throws Exception @see ParseException if the date submitted hasn't a good format
	 * @author Lucas Royackkers
	 */
	public HttpException createPerson(JsonNode jPerson,PersonRole type) throws ParseException {
		//if the mail don't match with the mail pattern
		if(!validateMail(jPerson.get("mail").textValue())) {
			return new UnprocessableEntityException();
		}
		
		Person newPerson = new Person();
		newPerson.setName(jPerson.get("name").textValue());
		newPerson.setMonthlyWage(Integer.parseInt(jPerson.get("wage").textValue()));
		newPerson.setCanStartsAt(new SimpleDateFormat("dd/MM/yyyy").parse(jPerson.get("dateStarts").textValue()));
		newPerson.setRole(type);
		newPerson.setMail(jPerson.get("mail").textValue());
		newPerson.setGrade(jPerson.get("grade").textValue());
		newPerson.setCommentary(jPerson.get("commentary").textValue());
		newPerson.setFromForum(Boolean.getBoolean(jPerson.get("fromForum").textValue()));
		List<String> docList= new ArrayList<String>();
		JsonNode docNode = jPerson.get("docs");
		for(JsonNode doc : docNode) {
			docList.add(doc.get("url").textValue());
		}
		newPerson.setUrlDocs(docList);
		
		Optional<User> managerInCharge = userEntityController.getUserByMail(jPerson.get("managerMail").textValue());
		if(managerInCharge.isPresent()) {
			newPerson.setManagerInCharge(managerInCharge.get().getMail());
		}
		
		Optional<Diploma> highestDiploma = diplomaEntityController.getDiplomaByNameAndYearOfResult(jPerson.get("diplomaName").textValue(),jPerson.get("diplomaYear").textValue());
		if(highestDiploma.isPresent()) {
			Diploma newDiploma = highestDiploma.get();
			newPerson.setHighestDiploma(newDiploma.getName());
		}
		
		JsonNode test = jPerson.get("mobilities");
		newPerson.setMobilities(this.getAllMobilities(test));
		
		Optional<Job> job = jobEntityController.getJob(jPerson.get("job").textValue());
		if(job.isPresent()){
			newPerson.setJob(job.get().getTitle());
		}
		
		Optional<Employer> employer = employerEntityController.getEmployer(jPerson.get("employer").textValue());
		if(employer.isPresent()) {
			newPerson.setEmployer(employer.get().getName());
		}	
	
		try {
			personRepository.save(newPerson);
		}
		catch(Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
		
	}
	
	/**
	 * Try to fetch a person by its name
	 * 
	 * @param name the person's name to fetch
	 * @return An Optional with the corresponding person or not.
	 * @author Lucas Royackkers
	 */
	public Optional<Person> getPersonByName(String name) {
		return personRepository.findByMail(name);
	}

	/**
	 * Try to fetch a person by its name and type
	 * 
	 * @param name the person's name to fetch
	 * @return An Optional with the corresponding person (of type consultant) or not.
	 * @author Lucas Royackkers
	 */
	public Optional<Person> getPersonByNameAndType(String name, PersonRole type){
		return personRepository.findByMailAndRole(name, type);
	}
	
	/**
	 * Get a List of Mobility object given a list of JsonNode(represented as a String)
	 * 
	 * @param jMobility the String corresponding to a JsonNode corresponding to the mobility part of a Person
	 * @return A List of Mobility
	 * @author Lucas Royackkers
	 */
	private List<String> getAllMobilities(JsonNode jMobilities) {
		List<String> allMobilities = new ArrayList<String>();
		for(JsonNode mobility : jMobilities) {
			Mobility mobilityToFind = new Mobility();
			mobilityToFind.setPlaceName(mobility.get("placeName").textValue());
			mobilityToFind.setPlaceType(mobility.get("placeType").textValue());
			mobilityToFind.setRadius(Integer.parseInt(mobility.get("radius").textValue()));
			mobilityToFind.setUnit(mobility.get("unit").textValue());
			
			Optional<Mobility> optionalMobility = mobilityEntityController.getMobility(mobilityToFind);
			if(optionalMobility.isPresent()) {
				allMobilities.add(optionalMobility.get().get_id().toString());
			}
		}
		return allMobilities;
	}
	
	/**
	 * @param role the type of persons that are searched
	 * @return the list of all persons
	 * @author Lucas Royackkers
	 */
	public List<Person> getPersonsByRole(PersonRole role) {
		return personRepository.findAllByRole(role);
	}
	
	/**
	 * Method to validate if the mail math with the mail pattern
	 * @param emailStr the string to validate
	 * @return true if the string match with the mail pattern
	 * @author Lucas Royackkers
	 */
	private static boolean validateMail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	
	

}
