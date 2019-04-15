package fr.alten.ambroiseJEE.model.entityControllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.SoftSkill;
import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.SoftSkillGrade;
import fr.alten.ambroiseJEE.utils.TechSkillGrade;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

public class SkillsSheetEntityController {
	@Autowired
	private SkillsSheetRepository skillsSheetRepository;
	
	@Autowired 
	private PersonEntityController personEntityController;
	
	@Autowired
	private SoftSkillEntityController softSkillEntityController;
	
	@Autowired
	private TechSkillEntityController techSkillEntityController;
	
	@Autowired
	private UserEntityController userEntityController;

	/**
	 * Try to fetch all skills sheets
	 * 
	 * @return A List with all skills sheets
	 * @author Lucas Royackkers
	 */
	public List<SkillsSheet> getSkillsSheets() {
		return skillsSheetRepository.findAll();
	}
	
	/**
	 * Try to fetch a skills sheet (or several) by a name
	 * 
	 * @param name the skills sheet's name to fetch
	 * @return An Optional with the corresponding skills sheet or not.
	 * @author Lucas Royackkers
	 */
	public Optional<List<SkillsSheet>> getSkillsSheetsByName(String name) {
		return skillsSheetRepository.findSkillsSheetsByName(name);
	}
	
	/**
	 * Try to fetch an skills sheet by its name and its versionNumber
	 * 
	 * @param name the skills sheet's name to fetch
	 * @param versionNumber
	 * @return An Optional with the corresponding skills sheet or not.
	 * @author Lucas Royackkers
	 */
	public Optional<SkillsSheet> getSkillsSheetByNameAndVersion(String name, long versionNumber){
		return skillsSheetRepository.findSkillsSheetsByNameAndVersion(name, versionNumber);
	}
	
	
	/**
	 * Method to create a skills sheet.
	 * 
	 * @param jUser JsonNode with all skills sheet parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skills sheet is created
	 * @author Lucas Royackkers
	 */
	public HttpException createSkillsSheet(JsonNode jSkillsSheet) {
		SkillsSheet newSkillsSheet = new SkillsSheet();
		String skillsSheetName = jSkillsSheet.get("name").textValue();
		newSkillsSheet.setName(skillsSheetName);
		
		Optional<Person> personAttachedTo;
		String status = jSkillsSheet.get("role").textValue();
		String personMail = jSkillsSheet.get("personMail").textValue();
		//Given the created person status
		switch(status) {
			case "consultant":
				personAttachedTo = personEntityController.getPersonByMailAndType(personMail,PersonRole.CONSULTANT);
				break;
			default:
				personAttachedTo = personEntityController.getPersonByMailAndType(personMail,PersonRole.APPLICANT);
				break;
		}
		if(personAttachedTo.isPresent()) {
			newSkillsSheet.setMailPersonAttachedTo(personAttachedTo.get().getMail());
		}
		//Get all skills given several lists of skills (tech and soft)
		newSkillsSheet.setSoftSkillsList(this.getAllSoftSkills(jSkillsSheet.get("softskills")));
		newSkillsSheet.setTechSkillsList(this.getAllTechSkills(jSkillsSheet.get("techskills")));
		
		//Set an Id and a Version Number on this skills sheet
		newSkillsSheet.setVersionNumber(1);
		newSkillsSheet.set_id(new ObjectId());
		
		String authorMail = jSkillsSheet.get("authorMail").textValue();
		Optional<User> userAuthor = userEntityController.getUserByMail(authorMail);
		if(userAuthor.isPresent()) {
			newSkillsSheet.setMailVersionAuthor(userAuthor.get().getMail());
		}
		
		newSkillsSheet.setVersionDate(LocalDateTime.now());
		
		try {
			skillsSheetRepository.save(newSkillsSheet);
		}
		catch(Exception e) {
			return new ConflictException();
		}
		
		return new CreatedException();
	}
	
	/**
	 * Get a List of SoftSkills object given a JsonNode containing a List of SoftSkills object 
	 * 
	 * @param jSoftSkills the JsonNode containing all soft skills for this skill sheet
	 * @return A List of SoftSkill
	 * @author Lucas Royackkers
	 */
	public List<SoftSkill> getAllSoftSkills(JsonNode jSoftSkills) {
		List<SoftSkill> allSoftSkills = new ArrayList<SoftSkill>();
		
		for(JsonNode softSkill : jSoftSkills) {
			Optional<SoftSkill> newSoftSkill = softSkillEntityController.getSoftSkillByNameAndGrade(softSkill.get("name").textValue(),SoftSkillGrade.valueOf(softSkill.get("grade").textValue()));
			//Get a specific soft skill by its name in the JsonNode
			if(newSoftSkill.isPresent()) {
				allSoftSkills.add(newSoftSkill.get());
			}
		}
		
		return allSoftSkills;
	}
	
	/**
	 * Get a List of TechSkills object given a JsonNode containing a List of TechSkills object 
	 * 
	 * @param jTechSkills the JsonNode containing all tech skills for this skill sheet
	 * @return A List of TechSkill
	 * @author Lucas Royackkers
	 */
	public List<TechSkill> getAllTechSkills(JsonNode jTechSkills) {
		List<TechSkill> allTechSkills = new ArrayList<TechSkill>();
		
		for(JsonNode techSkill : jTechSkills) {
			Optional<TechSkill> newTechSkill = techSkillEntityController.getTechSkillByNameAndGrade(techSkill.get("name").textValue(),TechSkillGrade.valueOf(techSkill.get("grade").textValue()));
			//Get a specific soft skill by its name in the JsonNode
			if(newTechSkill.isPresent()) {
				allTechSkills.add(newTechSkill.get());
			}
		}
		
		return allTechSkills;
	}

	/**
	 * Method to update a Skills Sheet, the update save a new version of the skills sheet
	 * 
	 * @param jSkillsSheet JsonNode with all skills sheet parameters, including its name (which cannot be changed) to perform an update on the database
	 * 
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the skills sheet is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateSkillsSheet(JsonNode jSkillsSheet) {
		//We retrieve the latest version number of the skills sheet, in order to increment it later
		long latestVersionNumber = Long.parseLong(jSkillsSheet.get("versionNumber").textValue());
		Optional<SkillsSheet> skillsSheetOptional = this.getSkillsSheetByNameAndVersion(jSkillsSheet.get("name").textValue(),latestVersionNumber);
		//If we found the skills sheet, with its name and its version (the Front part will have to send the latest version number)
		if(skillsSheetOptional.isPresent()) {
			SkillsSheet skillsSheet = skillsSheetOptional.get();
			
			Optional<Person> personAttachedTo;
			String status = jSkillsSheet.get("role").textValue();
			String personMail = jSkillsSheet.get("personMail").textValue();
			switch(status) {
				case "consultant":
					personAttachedTo = personEntityController.getPersonByMailAndType(personMail,PersonRole.CONSULTANT);
					break;
				default:
					personAttachedTo = personEntityController.getPersonByMailAndType(personMail,PersonRole.APPLICANT);
					break;
			}
			if(personAttachedTo.isPresent()) {
				skillsSheet.setMailPersonAttachedTo(personAttachedTo.get().getMail());
			}
			
			skillsSheet.setSoftSkillsList(this.getAllSoftSkills(jSkillsSheet.get("softskills")));
			skillsSheet.setTechSkillsList(this.getAllTechSkills(jSkillsSheet.get("techskills")));

			skillsSheet.setVersionNumber(latestVersionNumber+1);
			skillsSheet.set_id(new ObjectId());
			
			String authorMail = jSkillsSheet.get("authorMail").textValue();
			Optional<User> userAuthor = userEntityController.getUserByMail(authorMail);
			if(userAuthor.isPresent()) {
				skillsSheet.setMailVersionAuthor(userAuthor.get().getMail());
			}
			
			skillsSheet.setVersionDate(LocalDateTime.now());
			
			skillsSheetRepository.save(skillsSheet);
		}
		else {
			return new RessourceNotFoundException();
		}
		return new OkException();
	}

}
