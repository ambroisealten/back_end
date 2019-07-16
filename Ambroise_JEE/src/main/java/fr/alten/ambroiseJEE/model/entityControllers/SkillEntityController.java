/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;
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
import fr.alten.ambroiseJEE.model.beans.SkillGraduated;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.dao.SkillRepository;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
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

	@Autowired
	private SkillsSheetRepository skillsSheetRepository;

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

			if (this.skillRepository.findByNameIgnoreCase(newSkill.getName()).isPresent()) {
				return new ConflictException();
			}
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

					deleteSkillOnCascade(jSkill, skill);
					return (HttpException) new OkException();
				})
				// optional isn't present
				.orElse(new ResourceNotFoundException());
	}

	/**
	 * @param jSkill
	 * @param skill
	 * @author Andy Chabalier
	 */
	public void deleteSkillOnCascade(final JsonNode jSkill, Skill skill) {
		final String skillName = jSkill.get("name").textValue();
		final List<SkillsSheet> skillSheets = skillsSheetRepository.findAll();
		Iterator<SkillsSheet> it = skillSheets.iterator();
		while (it.hasNext()) {
			SkillsSheet skillsSheet = it.next();
			Iterator<SkillGraduated> it2 = skillsSheet.getSkillsList().iterator();
			List<SkillGraduated> tempList = new ArrayList<SkillGraduated>();
			while (it2.hasNext()) {
				SkillGraduated skillGraduated = it2.next();
				if (!skillGraduated.getSkill().getName().equals(skillName))
					tempList.add(skillGraduated);
			}
			skillsSheet.setSkillsList(tempList);
		}
		this.skillsSheetRepository.saveAll(skillSheets);
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
						// TODO Make update on cascade
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
		List<SkillsSheet> skillSheets = skillsSheetRepository.findAll();
		for (JsonNode jSkill : jSkills) {
			try {
				final String skillName = jSkill.get("name").textValue();
				final Skill skill = this.skillRepository.findByNameIgnoreCase(skillName).orElse(new Skill());
				skill.setName(skillName);
				if (jSkill.hasNonNull("isSoft")) {
					skill.setIsSoft(jSkill.get("isSoft").textValue());
				} else {
					skill.setIsSoft("isSoft");
				}
				skill.setOrder(jSkill.get("order").asInt());
				updateSkillListOnCascade(skillSheets, skillName, skill);
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

	/**
	 * @param skillSheets
	 * @param skillName
	 * @param skill
	 * @author Andy Chabalier
	 */
	public void updateSkillListOnCascade(List<SkillsSheet> skillSheets, final String skillName, final Skill skill) {
		for (SkillsSheet ss : skillSheets) {
			for (SkillGraduated skillGraduated : ss.getSkillsList()) {
				if (skillGraduated.getSkill().getName().equals(skillName)) {
					skillGraduated.setSkill(skill);
				}
			}
		}
		this.skillsSheetRepository.saveAll(skillSheets);
	}

	/**
	 * Checks if a Soft Skill with a specific name already exists in our database
	 * 
	 * @param name the name of the Soft Skill
	 * @return true if the Soft Skill exists, otherwise false
	 * @author Lucas Royackkers
	 */
	public boolean checkIfSoftSkillsExists(String name) {
		Optional<Skill> testSoft = this.skillRepository.findByNameIgnoreCase(name);
		return (testSoft.isPresent() && testSoft.get().isSoft());
	}

	/**
	 *
	 * @param name
	 * @return
	 * @author Thomas Decamp
	 */
	public List<Skill> getSynonymousList() {
		final List<Skill> firstList = this.skillRepository.findAll();
		return firstList.parallelStream().filter(skill -> !skill.getSynonymous().isEmpty() || !skill.getReplaceWith().isEmpty())
				.filter(skill -> !skill.getName().contains("deactivated")).collect(Collectors.toList());
	}

	/**
	 * 
	 *
	 * @param jSkill
	 * @return
	 * @author Thomas Decamp
	 */
	public HttpException updateSynonymousList(final JsonNode jSkill) {
		return this.skillRepository.findByNameIgnoreCase(jSkill.get("name").textValue())
		// optional is present
		.map(skill -> {
			if (jSkill.hasNonNull("synonymous")) {
				final String synonymous = jSkill.get("synonymous").textValue();
				final List<String> synonymousList = Arrays.asList(synonymous.split("\\,"));
				skill.setReplaceWith("");
				skill.setSynonymous(synonymousList);
				for (final String tmpSynonymous : synonymousList) {
					Skill tmp = this.skillRepository.findByNameIgnoreCase(tmpSynonymous).orElse(new Skill());
					tmp.setName(tmpSynonymous);
					tmp.setReplaceWith(skill.getName());
					tmp.clearSynonymousList();
					this.skillRepository.save(tmp);
					List<SkillsSheet> skillSheets = skillsSheetRepository.findAll();
					updateSkillListOnCascade(skillSheets, skill.getName(), tmp);
				}
			} else if (jSkill.hasNonNull("replaceWith")) {
				skill.clearSynonymousList();
				Skill tmp = this.skillRepository.findByNameIgnoreCase(jSkill.get("replaceWith").textValue()).orElse(new Skill());
				tmp.setName(jSkill.get("replaceWith").textValue());
				tmp.setReplaceWith("");
				skill.setReplaceWith(tmp.getName());
				List<String> synonymousList = tmp.getSynonymous();
				synonymousList.add(skill.getName());
				tmp.setSynonymous(synonymousList);
				this.skillRepository.save(tmp);
				List<SkillsSheet> skillSheets = skillsSheetRepository.findAll();
				updateSkillListOnCascade(skillSheets, skill.getName(), tmp);
			}
			try {
				// TODO Make update on cascade
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
	 * 
	 *
	 * @param jSkill
	 * @return
	 * @author Thomas Decamp
	 */
	public HttpException deleteSynonymous(final JsonNode jSkill) {
		return this.skillRepository.findByNameIgnoreCase(jSkill.get("name").textValue())
				// optional is present
				.map(skill -> {
					skill.clearSynonymousList();
					skill.setReplaceWith("");
					this.skillRepository.save(skill);
					return (HttpException) new OkException();
				})
				// optional isn't present
				.orElse(new ResourceNotFoundException());
	}
}
