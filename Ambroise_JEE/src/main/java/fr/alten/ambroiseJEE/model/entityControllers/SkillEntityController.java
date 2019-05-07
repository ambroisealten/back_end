/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DuplicateKeyException;

import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.dao.SkillRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author Thomas Decamp
 *
 */

@Service
public class SkillEntityController {

	@Autowired
	private SkillRepository skillRepository;

	/**
	 * Method to create a skill.
	 *
	 * @param jSkill JsonNode with all skill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skill is created
	 * @author Thomas Decamp
	 */
	public HttpException createSkill(final JsonNode jSkill) {

		final Skill newSkill = new Skill();
		newSkill.setName(jSkill.get("name").textValue());
		if (jSkill.hasNonNull("isSoft")) {
			newSkill.setIsSoft(jSkill.get("isSoft").textValue());
		}
		try {
			this.skillRepository.save(newSkill);
		} catch (final DuplicateKeyException dke) {
			return new ConflictException();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return new CreatedException();

	}

	/**
	 * Supplier to create a Skill with its name and a String (can be null)
	 * 
	 * @param name the name of the Skill
	 * @param isSoft a String (can be null)
	 * @return the Skill created
	 * @throws {@link ConflictException} if there is a conflict in the
	 *         database or {@link InternalServerErrorException} if there is another problem
	 * @author Lucas Royackkers, Andy Chabalier
	 */
	public Supplier<? extends Skill> createSkill(final String name, @Nullable final String isSoft) {
		return () -> {
			Skill newSkill = new Skill();
			newSkill.setName(name);
			newSkill.setIsSoft(isSoft);
			try {
				newSkill = this.skillRepository.save(newSkill);
			} catch (final DuplicateKeyException dke) {
				throw new ConflictException();
			} catch (final Exception e) {
				throw new InternalServerErrorException();
			}
			return newSkill;
		};
	}

	/**
	 * Method to delete a Skill
	 *
	 * @param name the skill name to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the skill is deactivated
	 * @author Thomas Decamp
	 */
	public HttpException deleteSkill(final JsonNode jSkill) {
		return this.skillRepository.findByNameIgnoreCase(jSkill.get("name").textValue())
				// optional is present
				.map(skill -> {
					skill.setName("deactivated" + System.currentTimeMillis());
					skill.setIsSoft(null);
					this.skillRepository.save(skill);
					return (HttpException) new OkException();
				})
				// optional isn't present
				.orElse(new ResourceNotFoundException());
	}

	/**
	 * Try to fetch a Skill by its name
	 *
	 * @param name the name of the Skill
	 * @return an Optional containing the Skill (or not)
	 * @author Thomas Decamp
	 */
	public Optional<Skill> getSkill(final String name) {
		return this.skillRepository.findByNameIgnoreCase(name);
	}

	/**
	 * Method to get all Skills within the database
	 * 
	 * @return the list of all skills
	 * @author Thomas Decamp
	 */
	public List<Skill> getSkills() {
		return this.skillRepository.findAll();
	}

	/**
	 * Method to update a Skill
	 *
	 * @param jSkill JsonNode with all skill parameters and the old name to perform
	 *               the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the skill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateSkill(final JsonNode jSkill) {
		return this.skillRepository.findByNameIgnoreCase(jSkill.get("oldName").textValue())
				// optional is present
				.map(skill -> {
					skill.setName(jSkill.get("name").textValue());
					if (jSkill.hasNonNull("isSoft")) {
						skill.setIsSoft(jSkill.get("isSoft").textValue());
					} else {
						skill.setIsSoft(null);
					}
					try {
						this.skillRepository.save(skill);
					} catch (final DuplicateKeyException dke) {
						return new ConflictException();
					} catch (final Exception e) {
						e.printStackTrace();
					}
					return (HttpException) new OkException();
				})
				// optional isn't present
				.orElse(new ResourceNotFoundException());
	}

}
