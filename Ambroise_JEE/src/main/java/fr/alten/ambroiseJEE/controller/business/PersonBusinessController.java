package fr.alten.ambroiseJEE.controller.business;

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

	private final UserRoleLists roles = UserRoleLists.getInstance();

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
	public Person getPerson(final String mail, final UserRole role) {
		if (this.roles.isAdmin(role) || this.roles.isManagerOrCdr(role)) {
			try {
				return this.personEntityController.getPersonByMail(mail);
			} catch (final ResourceNotFoundException e) {
				return new Person();
			}
		}
		throw new ForbiddenException();
	}

}
