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
	 * Fetch skills sheets by name
	 *
	 * @param name
	 * @return a list that contains all skills sheets given a name (can be empty)
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
	
	
	/**
	 * Fetch skills sheets by mail (of the person attached to, i.e an applicant or a consultant)
	 * @param mail
	 * @return a list that contains all skills sheets given a mail (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> findByMailPersonAttachedTo(String mail);
}
