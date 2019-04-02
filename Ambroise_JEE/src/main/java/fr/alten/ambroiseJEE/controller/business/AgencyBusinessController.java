/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.entityControllers.AgencyEntityController;

/**
 * Agency controller for business rules.
 * @author Andy Chabalier
 *
 */
@Service
public class AgencyBusinessController {

	@Autowired
	private AgencyEntityController agencyEntityController;
	

	public Optional<Agency> getAgency(String name) {
		return agencyEntityController.getAgency(name);
	}

}
