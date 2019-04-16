package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.model.dao.TechSkillRepository;
import fr.alten.ambroiseJEE.utils.TechSkillGrade;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

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
	 * Method to create a techSkill.
	 *
	 * @param jTechSkill JsonNode with all techSkill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the techSkill is created
	 * @author Lucas Royackkers, Thomas Decamp
	 */
	public HttpException createTechSkillAndGrade(JsonNode jTechSkill) {
		List<TechSkill> techSkillOptional = techSkillRepository.findByName(jTechSkill.get("name").textValue());
		if (techSkillOptional.size() > 0) {
			return new ConflictException();
		}

		for (TechSkillGrade techGrade : TechSkillGrade.values()) {
			TechSkill newTechSkill = new TechSkill();
			newTechSkill.setName(jTechSkill.get("name").textValue());
			newTechSkill.setGrade(techGrade);
			try {
				techSkillRepository.save(newTechSkill);
			} catch (Exception e) {
				return new ConflictException();
			}
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param name the TechSkill name to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link RessourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the TechSkill is deactivated
	 * @author Thomas Decamp
	 */
	public HttpException deleteTechSkill(JsonNode jTechSkill) {
		List<TechSkill> techSkills = techSkillRepository.findByName(jTechSkill.get("name").textValue());
		if (techSkills.isEmpty()) {
			return new RessourceNotFoundException();
		}
		for (TechSkill techSkill : techSkills) {
			techSkill.setName("deactivated" + System.currentTimeMillis());
			techSkillRepository.save(techSkill);
		}

		return new OkException();
	}

	/**
	 * Try to fetch a tech skill by its name
	 *
	 * @param name the tech skill's name to fetch
	 * @return An Optional with the corresponding tech skill or not.
	 * @author Lucas Royackkers
	 */
	public List<TechSkill> getTechSkillByName(String name) {
		return techSkillRepository.findByName(name);
	}

	/**
	 * Try to fetch a tech skill by its name and grade
	 *
	 * @param name  the tech skill's name to fetch
	 * @param grade the tech skill's grade to fetch
	 * @return An Optional with the corresponding tech skill or not.
	 * @author Lucas Royackkers
	 */
	public Optional<TechSkill> getTechSkillByNameAndGrade(String name, TechSkillGrade grade) {
		return techSkillRepository.findByNameAndGrade(name, grade);
	}

	/**
	 * @return the list of all techSkills
	 * @author Thomas Decamp
	 */
	public List<TechSkill> getTechSkills() {
		return techSkillRepository.findAll();
	}

	/**
	 *
	 * @param jTechSkill JsonNode with all TechSkill parameters and the old name to
	 *                   perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the TechSkill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateTechSkill(JsonNode jTechSkill) {
		List<TechSkill> techSkills = techSkillRepository.findByName(jTechSkill.get("oldName").textValue());
		if (techSkills.isEmpty()) {
			return new RessourceNotFoundException();
		}
		String newName = jTechSkill.get("name").textValue();
		for (TechSkill techSkill : techSkills) {
			techSkill.setName(newName);
			techSkillRepository.save(techSkill);
		}
		return new OkException();
	}
}
