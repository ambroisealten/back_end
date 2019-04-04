package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import fr.alten.ambroiseJEE.model.PersonRole;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.entityControllers.PersonEntityController;
import fr.alten.ambroiseJEE.security.Roles;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Applicant controller for business rules.
 * @author Lucas Royackkers
 *
 */
@Service
public class ApplicantBusinessController {
	@Autowired
	private PersonEntityController personEntityController;
	
	public Optional<Person> getApplicant(String name){
		return personEntityController.getApplicantByName(name);
	}
	
	/**
	 * Method to delegate applicant creation
	 * @param jUser JsonNode with all applicant(person) parameters
	 * @param role the user's role 
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the person is created
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	public HttpException createApplicant(JsonNode jApplicant, int role) throws ParseException {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? personEntityController.createPerson(jApplicant,PersonRole.APPLICANT) : new ForbiddenException();
	}


	/**
	 * @param role the user's role
	 * @return the list of all applicants
	 * @author Lucas Royackkers
	 */
	public List<Person> getApplicants(int role) {
		if (Roles.ADMINISTRATOR_USER_ROLE.getValue()== role) {
			return personEntityController.getPersonsByRole(PersonRole.APPLICANT);
		}
		throw new ForbiddenException();	
	}
	
}
