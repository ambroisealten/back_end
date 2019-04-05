package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.SoftSkill;
import fr.alten.ambroiseJEE.model.dao.SoftSkillRepository;

/**
 * Soft skill controller for entity gestion rules
 * 
 * @author Lucas Royackkers
 *
 */
@Service
public class SoftSkillEntityController {
	@Autowired
	private SoftSkillRepository softSkillRepository;

	/**
	 * Try to fetch a soft skill by its name
	 * 
	 * @param name the soft skill's name to fetch
	 * @return An Optional with the corresponding soft skill or not.
	 * @author Lucas Royackkers
	 */
	public Optional<SoftSkill> getSoftSkill(String name) {
		return softSkillRepository.findSoftSkillByName(name);
	}
	

}
