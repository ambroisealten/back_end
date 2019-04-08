package fr.alten.ambroiseJEE.model.entityControllers;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
		Optional<Person> optionalPerson = personRepository.findByNameAndRole(jPerson.get("name").textValue(), role);
		if(optionalPerson.isPresent()) {
			Person person = optionalPerson.get();
			switch(role) {
			case APPLICANT:
				person.setName("Desactivated");
				break;
			default:
				person.setName("Démissionaire");
				break;
			}
			person.setMail("");
			person.setEmployer(null);
			person.setCommentary("");
			person.setGrade(null);
			person.setCanStartsAt(null);
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
		Optional<Person> optionalPerson = personRepository.findByNameAndRole(jPerson.get("name").textValue(), role);
		if(optionalPerson.isPresent()) {
			Person person = optionalPerson.get();
			person.setName(jPerson.get("name").textValue());
			person.setMonthlyWage(Integer.parseInt(jPerson.get("name").textValue()));
			person.setCanStartsAt(new SimpleDateFormat("dd/MM/yyyy").parse(jPerson.get("name").textValue()));
			String type = jPerson.get("type").textValue();
			if(role == PersonRole.APPLICANT) {
				switch(type) {
					case "consultant":
						person.setRole(PersonRole.CONSULTANT);
						break;
					default:
						break;
				}
			}
			
			person.setMail(jPerson.get("mail").textValue());
			person.setGrade(jPerson.get("grade").textValue());
			person.setCommentary(jPerson.get("commentary").textValue());
			
			Optional<User> managerInCharge = userEntityController.getUserByMail(jPerson.get("managerMail").textValue());
			if(managerInCharge.isPresent()) {
				person.setManagerInCharge(managerInCharge.get());
			}
			
			Optional<Diploma> highestDiploma = diplomaEntityController.getDiplomaByName(jPerson.get("diplomaName").textValue());
			if(highestDiploma.isPresent()) {
				Diploma newDiploma = highestDiploma.get();
				newDiploma.setYearOfResult(Year.of((Integer.parseInt(jPerson.get("diplomaYear").textValue()))));
				person.setHighestDiploma(highestDiploma.get());
			}
			
			person.setMobilities(this.getAllMobilities(jPerson.get("mobilities").asText()));
			
			Optional<Job> job = jobEntityController.getJob(jPerson.get("job").textValue());
			if(job.isPresent()){
				person.setJob(job.get());
			}
			
			Optional<Employer> employer = employerEntityController.getEmployer(jPerson.get("employer").textValue());
			if(employer.isPresent()) {
				person.setEmployer(employer.get());
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
		newPerson.setMonthlyWage(Integer.parseInt(jPerson.get("name").textValue()));
		newPerson.setCanStartsAt(new SimpleDateFormat("dd/MM/yyyy").parse(jPerson.get("name").textValue()));
		newPerson.setRole(type);
		newPerson.setMail(jPerson.get("mail").textValue());
		newPerson.setGrade(jPerson.get("grade").textValue());
		newPerson.setCommentary(jPerson.get("commentary").textValue());
		
		Optional<User> managerInCharge = userEntityController.getUserByMail(jPerson.get("managerMail").textValue());
		if(managerInCharge.isPresent()) {
			newPerson.setManagerInCharge(managerInCharge.get());
		}
		
		Optional<Diploma> highestDiploma = diplomaEntityController.getDiplomaByName(jPerson.get("diplomaName").textValue());
		if(highestDiploma.isPresent()) {
			Diploma newDiploma = highestDiploma.get();
			newDiploma.setYearOfResult(Year.of((Integer.parseInt(jPerson.get("diplomaYear").textValue()))));
			newPerson.setHighestDiploma(highestDiploma.get());
		}
		
		newPerson.setMobilities(this.getAllMobilities(jPerson.get("mobilities").asText()));
		
		Optional<Job> job = jobEntityController.getJob(jPerson.get("job").textValue());
		if(job.isPresent()){
			newPerson.setJob(job.get());
		}
		
		Optional<Employer> employer = employerEntityController.getEmployer(jPerson.get("employer").textValue());
		if(employer.isPresent()) {
			newPerson.setEmployer(employer.get());
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
		return personRepository.findByName(name);
	}

	/**
	 * Try to fetch a person by its name and type
	 * 
	 * @param name the person's name to fetch
	 * @return An Optional with the corresponding person (of type consultant) or not.
	 * @author Lucas Royackkers
	 */
	public Optional<Person> getPersonByNameAndType(String name, PersonRole type){
		return personRepository.findByNameAndRole(name, type);
	}
	
	/**
	 * Get a List of Mobility object given a list of JsonNode(represented as a String)
	 * 
	 * @param jMobility the String corresponding to a JsonNode corresponding to the mobility part of a Person
	 * @return A List of Mobility
	 * @author Lucas Royackkers
	 */
	private List<Mobility> getAllMobilities(String jMobility) {
		Type listType = new TypeToken<List<JsonNode>>() {}.getType();
		List<JsonNode> mobilitiesList = new Gson().fromJson(jMobility, listType);
		List<Mobility> allMobilities = new ArrayList<Mobility>();
		
		for(int i = 0; i < mobilitiesList.size() ;i++) {
			mobilityEntityController.createMobility(mobilitiesList.get(i));
			Optional<Mobility> mobility = mobilityEntityController.getMobility(mobilitiesList.get(i).get("place").textValue(),Integer.parseInt(mobilitiesList.get(i).get("radius").textValue()));
			if(mobility.isPresent()) {
				allMobilities.add(mobility.get());
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
