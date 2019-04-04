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

	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);
	
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
			newPerson.setHighestDiploma(highestDiploma.get());
		}
		
		newPerson.setMobility(this.getAllMobilities(jPerson.get("mobilities").textValue()));
		
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
	 * Try to fetch an applicant by its name
	 * 
	 * @param name the person's name to fetch
	 * @return An Optional with the corresponding person (of type applicant) or not.
	 * @author Lucas Royackkers
	 */
	public Optional<Person> getApplicantByName(String name){
		return personRepository.findByNameAndRole(name, PersonRole.APPLICANT);
	}
	
	/**
	 * Try to fetch a consultant by its name
	 * 
	 * @param name the person's name to fetch
	 * @return An Optional with the corresponding person (of type consultant) or not.
	 * @author Lucas Royackkers
	 */
	public Optional<Person> getConsultantByName(String name){
		return personRepository.findByNameAndRole(name, PersonRole.CONSULTANT);
	}
	
	
	/**
	 * Get a List of Moblity object given a String
	 * 
	 * @param mobilitiesString the String containing all possible mobilities for the person,
	 * declared in this format : "place1//radius1//unit1 place2//radius2//unit2"
	 * @return A List of Mobility
	 * @author Lucas Royackkers
	 */
	private List<Mobility> getAllMobilities(String mobilitiesString) {
		List<Mobility> allMobilities = new ArrayList<Mobility>();
		String[] mobilitiesStringSplitted = mobilitiesString.split(" ");
		for(int i = 0; i < mobilitiesStringSplitted.length; i++) {
			String[] mobilityParams = mobilitiesStringSplitted[i].split("//");
			Mobility newMobility = new Mobility();
			newMobility.setPlace(mobilityParams[0]);
			newMobility.setRadius(Integer.parseInt(mobilityParams[1]));
			newMobility.setUnit(mobilityParams[2]);
			allMobilities.add(newMobility);
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
