package fr.alten.ambroiseJEE.controller.business;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.entityControllers.PersonEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Person controller for business rules.
 *
 * @author Camille Schnell
 *
 */
@Service
public class PersonBusinessController {
	
	private UserRoleLists roles = UserRoleLists.getInstance();
	
	@Autowired
	private PersonEntityController personEntityController;
	
	/**
	 * Try to fetch a person given its mail
	 *
	 * @param mail the person's mail
	 * @param role the user's role
	 * @return an Optional with the corresponding person (or not)
	 * @author Camille Schnell
	 * @throws ForbiddenException (if the user hasn't the right to do so)
	 */
	public Person getPerson(String mail, UserRole role) {
		if (roles.isAdmin(role) || roles.isManagerOrCdr(role)) {
			try {
				return personEntityController.getPersonByMail(mail);
			}
			catch(ResourceNotFoundException e) {
				return new Person();
			}
		}
		throw new ForbiddenException();
	}

}
