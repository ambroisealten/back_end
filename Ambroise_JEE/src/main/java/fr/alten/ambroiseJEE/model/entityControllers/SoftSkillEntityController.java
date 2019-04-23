package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.SoftSkill;
import fr.alten.ambroiseJEE.model.dao.SoftSkillRepository;
import fr.alten.ambroiseJEE.utils.SoftSkillGrade;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

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
	 * Method to create a softSkill.
	 *
	 * @param jSoftSkill JsonNode with all softSkill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the softSkill is created
	 * @author Lucas Royackkers, Thomas Decamp
	 */
	public HttpException createSoftSkillAndGrade(JsonNode jSoftSkill) {
		List<SoftSkill> softSkillOptional = softSkillRepository.findByName(jSoftSkill.get("name").textValue());
		if (softSkillOptional.size() > 0) {
			return new ConflictException();
		}

		for (SoftSkillGrade softGrade : SoftSkillGrade.values()) {
			SoftSkill newSoftSkill = new SoftSkill();
			newSoftSkill.setName(jSoftSkill.get("name").textValue());
			newSoftSkill.setGrade(softGrade);
			try {
				softSkillRepository.save(newSoftSkill);
			} catch (Exception e) {
				return new ConflictException();
			}
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param name the SoftSkill name to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link RessourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the SoftSkill is deactivated
	 * @author Thomas Decamp
	 */
	public HttpException deleteSoftSkill(JsonNode jSoftSkill) {
		List<SoftSkill> softSkills = softSkillRepository.findByName(jSoftSkill.get("name").textValue());
		if (softSkills.isEmpty()) {
			return new RessourceNotFoundException();
		}
		for (SoftSkill softSkill : softSkills) {
			softSkill.setName("deactivated" + System.currentTimeMillis());
			softSkillRepository.save(softSkill);
		}
		return new OkException();
	}

	/**
	 * Try to fetch a soft skill by its name
	 *
	 * @param name the soft skill's name to fetch
	 * @return the list of all softSkills given a name (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<SoftSkill> getSoftSkillByName(String name) {
		return softSkillRepository.findByName(name);
	}

	/**
	 * Try to fetch a soft skill by its name and grade
	 *
	 * @param name  the soft skill's name to fetch
	 * @param grade the soft skill's grade to fetch
	 * @return An Optional with the corresponding soft skill or not.
	 * @author Lucas Royackkers
	 */
	public Optional<SoftSkill> getSoftSkillByNameAndGrade(String name, SoftSkillGrade grade) {
		return softSkillRepository.findByNameAndGrade(name, grade);
	}

	/**
	 * @return the list of all softSkills
	 * @author Thomas Decamp
	 */
	public List<SoftSkill> getSoftSkills() {
		return softSkillRepository.findAll();
	}

	/**
	 *
	 * @param jSoftSkill JsonNode with all SoftSkill parameters and the old name to
	 *                   perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the SoftSkill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateSoftSkill(JsonNode jSoftSkill) {
		List<SoftSkill> softSkills = softSkillRepository.findByName(jSoftSkill.get("oldName").textValue());
		if (softSkills.isEmpty()) {
			return new RessourceNotFoundException();
		}
		String newName = jSoftSkill.get("name").textValue();
		List<SoftSkill> newSoftSkills = softSkillRepository.findByName(newName);
		if (newSoftSkills.size() > 0) {
			return new ConflictException();
		}
		for (SoftSkill softSkill : softSkills) {
			softSkill.setName(newName);
			softSkillRepository.save(softSkill);
		}
		return new OkException();
	}

}
