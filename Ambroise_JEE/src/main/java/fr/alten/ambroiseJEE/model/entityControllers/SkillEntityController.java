/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

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
			newSkill.setOrder(jSkill.get("order").asInt());
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
	 * Supplier to create a Skill with its name
	 *
	 * @param name the name of the Skill
	 * @return the Skill created
	 * @throws {@link ConflictException} if there is a conflict in the database or
	 *         {@link InternalServerErrorException} if there is another problem
	 * @author Lucas Royackkers, Andy Chabalier
	 */
	public Supplier<? extends Skill> createSkill(final String name) {
		return () -> {
			Skill newSkill = new Skill();
			newSkill.setName(name);
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
					skill.setOrder(Integer.MIN_VALUE);
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
	public Skill getSkill(final String name) {
		return this.skillRepository.findByNameIgnoreCase(name).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Method to get all Skills within the database
	 *
	 * @return the list of all skills
	 * @author Thomas Decamp
	 */
	public List<Skill> getSkills() {
		return this.skillRepository.findAll().parallelStream().filter(skill -> !skill.getName().contains("deactivated"))
				.collect(Collectors.toList());
	}

	/**
	 * fetch the soft skills
	 *
	 * @return return the list of all soft skills
	 * @author Andy Chabalier
	 */
	public List<Skill> getSoftSkills() {
		final Skill skillExample = new Skill();
		skillExample.setIsSoft(".*");

		// Create a matcher for this file Example. We want to focus only on path, then
		// we ignore null value and dateOfCreation which is a long value and can't be
		// null
		final ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("isSoft", GenericPropertyMatchers.regex())
				.withIgnoreNullValues().withIgnorePaths("order");

		return this.skillRepository.findAll(Example.of(skillExample, matcher)).parallelStream()
				.filter(skill -> !skill.getName().contains("deactivated")).collect(Collectors.toList());
	}

	public List<Skill> getTechSkills() {
		final List<Skill> firstList = this.skillRepository.findAll();
		return firstList.parallelStream().filter(skill -> !skill.isSoft())
				.filter(skill -> !skill.getName().contains("deactivated")).collect(Collectors.toList());
	}

	/**
	 * Method to update a Skill
	 *
	 * @param jSkill JsonNode with all skill parameters and the old name to perform
	 *               the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the skill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateSkill(final JsonNode jSkill) {

		return this.skillRepository.findByNameIgnoreCase(jSkill.get("oldName").textValue())
				// optional is present
				.map(skill -> {
					skill.setName(jSkill.get("name").textValue());
					if (jSkill.hasNonNull("isSoft")) {
						skill.setIsSoft(jSkill.get("isSoft").textValue());
						skill.setOrder(jSkill.get("order").asInt());
					} else {
						skill.setIsSoft(null);
						skill.setOrder(Integer.MIN_VALUE);
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

	/**
	 * Method to update a Skill
	 *
	 * @param jSkills JsonNode with all skill parameters and the old name to perform
	 *                the update even if the name is changed
	 * @return a list of {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the skill is updated
	 * @author Andy Chabalier
	 */
	public ArrayList<HttpException> updateSoftSkillsOrder(final JsonNode jSkills) {
		final ArrayList<HttpException> result = new ArrayList<HttpException>();
		for (JsonNode jSkill : jSkills) {
			try {
				final Skill skill = this.skillRepository.findByNameIgnoreCase(jSkill.get("name").textValue())
						.orElse(new Skill());
				skill.setName(jSkill.get("name").textValue());
				if (jSkill.hasNonNull("isSoft")) {
					skill.setIsSoft(jSkill.get("isSoft").textValue());
				} else {
					skill.setIsSoft("isSoft");
				}
				skill.setOrder(jSkill.get("order").asInt());
				this.skillRepository.save(skill);
			} catch (final DuplicateKeyException dke) {
				result.add(new ConflictException());
			} catch (final Exception e) {
				result.add(new InternalServerErrorException(e));
			}
			result.add(new OkException());
		}
		return result;
	}

}
