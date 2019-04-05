package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.model.dao.TechSkillRepository;

/**
 * Tech skill controller for entity gestion rules
 * 
 * @author Lucas Royackkers
 *
 */
@Service
public class TechSkillEntityController {
	@Autowired
	private TechSkillRepository techSkillRepository;
	
	/**
	 * Try to fetch a tech skill by its name
	 * 
	 * @param name the tech skill's name to fetch
	 * @return An Optional with the corresponding tech skill or not.
	 * @author Lucas Royackkers
	 */
	public Optional<TechSkill> getTechSkill(String name) {
		return techSkillRepository.findTechSkillByName(name);
	}

}
