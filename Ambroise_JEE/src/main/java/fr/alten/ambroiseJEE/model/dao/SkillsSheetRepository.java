package fr.alten.ambroiseJEE.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.SkillsSheet;

/**
 * @author Lucas Royackkers
 *
 */
public interface SkillsSheetRepository extends MongoRepository<SkillsSheet, Long> {

	/**
	 * Fetch skills sheet by name
	 *
	 * @param name
	 * @return An Optional with the corresponding skills sheet or not.
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> findByName(String name);

	/**
	 * Fetch skills sheet by name and version number
	 *
	 * @param name
	 * @param versionNumber the version number of the searched skills sheet
	 * @return An Optional with the corresponding skills sheet or not.
	 * @author Lucas Royackkers
	 */
	public Optional<SkillsSheet> findByNameAndVersionNumber(String name, long versionNumber);
}
