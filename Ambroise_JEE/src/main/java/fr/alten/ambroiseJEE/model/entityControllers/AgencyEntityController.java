/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.dao.AgencyRepository;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class AgencyEntityController {
	
	@Autowired
	private AgencyRepository agencyRepository;

	public Optional<Agency> getAgency(String name) {
		return agencyRepository.findByName(name);
	}

}
