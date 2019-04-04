package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.entityControllers.PersonEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Consultant controller for business rules.
 * @author Lucas Royackkers
 *
 */
@Service
public class ConsultantBusinessController {
	@Autowired
	private PersonEntityController personEntityController;
	
	public Optional<Person> getApplicant(String name){
		return personEntityController.getApplicantByName(name);
	}
	
	/**
	 * Method to delegate consultant creation
	 * @param jUser JsonNode with all consultant(person) parameters
	 * @param role the user's role 
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the person is created
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	public HttpException createConsultant(JsonNode jConsultant, UserRole role) throws ParseException {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) ? personEntityController.createPerson(jConsultant,PersonRole.CONSULTANT) : new ForbiddenException();
	}


	/**
	 * @param role the user's role
	 * @return the list of all consultants
	 * @author Lucas Royackkers
	 */
	public List<Person> getConsultants(UserRole role) {
		if ((UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role)) {
			return personEntityController.getPersonsByRole(PersonRole.CONSULTANT);
		}
		throw new ForbiddenException();	
	}
}
