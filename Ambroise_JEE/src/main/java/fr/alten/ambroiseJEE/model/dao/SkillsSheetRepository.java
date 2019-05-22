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
	 * Checks if a Skills Sheet with the given parameters exists
	 *
	 * @param name                 the name of the Skills Sheet
	 * @param mailPersonAttachedTo the mail of the person attached to the Skills
	 *                             Sheet
	 * @param l                    the version number of the Skills Sheet
	 * @return true if this specific Skills Sheet exists, otherwise false
	 * @author Lucas Royackkers
	 */
	public boolean existsByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(String name,
			String mailPersonAttachedTo, long l);

	/**
	 * Fetch skills sheets by mail (of the person attached to, i.e an applicant or a
	 * consultant)
	 *
	 * @param mail
	 * @return a list that contains all skills sheets given a mail (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> findByMailPersonAttachedToIgnoreCase(String mail);

	/**
	 * Fetch skills sheets by author
	 *
	 * @param mailAuthor the author mail to fetch
	 * @return the corresponding list of skillsSheets
	 * @author Andy Chabalier
	 */
	public List<SkillsSheet> findByMailVersionAuthorIgnoreCase(String mailAuthor);

	/**
	 * Fetch skills sheets by name
	 *
	 * @param name
	 * @return a list that contains all skills sheets given a name (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> findByNameIgnoreCase(String name);

	/**
	 * Get a Skills Sheet given a name, a person attached to it and a version number
	 *
	 * @param skillsSheetName the name of the skills sheet
	 * @param personMail      the mail of the person attached to it
	 * @param i               the number (long type) of the skills sheet
	 * @return An Optional, if this specific Skills Sheet exists or not
	 * @author Lucas Royackkers
	 */
	public Optional<SkillsSheet> findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(
			String skillsSheetName, String personMail, long i);

	/**
	 * Fetch skills sheet versions by name and mail of the person attached to it
	 *
	 * @param name the name of the Skills Sheet
	 * @param mail the mail of the person attached to the Skills Sheet
	 * @return a List of Skills Sheet that matches the given parameters (can be
	 *         empty)
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseOrderByVersionNumberDesc(String name,
			String mail);

	/**
	 * Get a List of Skills Sheet given a name, and with the latest in first
	 * position
	 *
	 * @param skillsSheetName the name of the Skills Sheet
	 * @return A List of Skills Sheets
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> findByNameIgnoreCaseOrderByVersionNumberDesc(String skillsSheetName);
}
