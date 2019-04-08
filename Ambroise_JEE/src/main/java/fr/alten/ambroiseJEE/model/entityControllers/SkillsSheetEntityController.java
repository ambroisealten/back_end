package fr.alten.ambroiseJEE.model.entityControllers;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.SoftSkill;
import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
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
	 * Method to create an skills sheet.
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
		String personName = jSkillsSheet.get("person").textValue();
		switch(status) {
			case "consultant":
				personAttachedTo = personEntityController.getConsultantByName(personName);
				break;
			default:
				personAttachedTo = personEntityController.getApplicantByName(personName);
				break;
		}
		if(personAttachedTo.isPresent()) {
			newSkillsSheet.setPersonAttachedTo(personAttachedTo.get());
		}
		
		newSkillsSheet.setSoftSkillsList(this.getAllSoftSkills(jSkillsSheet.get("softskills").asText()));
		newSkillsSheet.setTechSkillsList(this.getAllTechSkills(jSkillsSheet.get("techskills").asText()));
		
		newSkillsSheet.setVersionNumber(1);
		newSkillsSheet.set_id(new ObjectId());
		
		String authorMail = jSkillsSheet.get("authorMail").textValue();
		Optional<User> userAuthor = userEntityController.getUserByMail(authorMail);
		if(userAuthor.isPresent()) {
			newSkillsSheet.setVersionAuthor(userAuthor.get());
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
	 * Get a List of SoftSkills object given a String (the original JsonNode containing a List of SoftSkills object as a String)
	 * 
	 * @param softSkillsString the String containing all soft skills for this skill sheet
	 * @return A List of SoftSkill
	 * @author Lucas Royackkers
	 */
	public List<SoftSkill> getAllSoftSkills(String softSkillsString) {
		//Cast the type of the list that the Gson function will give
		Type listType = new TypeToken<List<JsonNode>>() {}.getType();
		//Get a list of JsonNode (a JsonNode will contain a Soft Skill) from the parent node (given as a string in this function)
		List<JsonNode> softSkillsList = new Gson().fromJson(softSkillsString, listType);
		List<SoftSkill> allSoftSkills = new ArrayList<SoftSkill>();
		
		for(int i = 0; i < softSkillsList.size(); i++) {
			//Get a specific soft skill by its name in the JsonNode
			Optional<SoftSkill> softSkill = softSkillEntityController.getSoftSkill(softSkillsList.get(i).get("name").textValue());
			if(softSkill.isPresent()) {
				//If the soft skill exists, we associate it with a grade
				SoftSkill newSoftSkill = softSkill.get();
				int softSkillGrade = Integer.parseInt(softSkillsList.get(i).get("grade").textValue());
				if(softSkillGrade >= 1 && softSkillGrade <= 4) {
					newSoftSkill.setGrade(softSkillGrade);
				}
				allSoftSkills.add(newSoftSkill);
			}
		}
		
		return allSoftSkills;
	}
	
	/**
	 * Get a List of TechSkills object given a String (the original JsonNode containing a List of TechSkills object as a String)
	 * 
	 * @param techSkillsString the String containing all tech skills for this skill sheet
	 * @author Lucas Royackkers
	 */
	public List<TechSkill> getAllTechSkills(String techSkillsString) {
		//Cast the type of the list that the Gson function will give
		Type listType = new TypeToken<List<JsonNode>>() {}.getType();
		//Get a list of JsonNode (a JsonNode will contain a Tech Skill) from the parent node (given as a string in this function)
		List<JsonNode> techSkillsList = new Gson().fromJson(techSkillsString, listType);
		List<TechSkill> allTechSkills = new ArrayList<TechSkill>();
		
		for(int i = 0; i < allTechSkills.size(); i++) {
			Optional<TechSkill> techSkill = techSkillEntityController.getTechSkill(techSkillsList.get(i).get("name").textValue());
			if(techSkill.isPresent()) {
				TechSkill newTechSkill = techSkill.get();
				int techSkillGrade = Integer.parseInt(techSkillsList.get(i).get("grade").textValue());
				if(techSkillGrade >= 1 && techSkillGrade <= 4) {
					newTechSkill.setGrade(techSkillGrade);
				}
				allTechSkills.add(newTechSkill);
			}
		}
		return allTechSkills;
	}

	/**
	 * 
	 * @param jSkillsSheet JsonNode with all skills sheet parameters, including its name (which cannot be changed) to perform an update on the database
	 * 
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the skills sheet is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateSkillsSheet(JsonNode jSkillsSheet) {
		long oldVersionNumber = Long.parseLong(jSkillsSheet.get("versionNumber").textValue());
		Optional<SkillsSheet> skillsSheetOptional = this.getSkillsSheetByNameAndVersion(jSkillsSheet.get("name").textValue(),oldVersionNumber);
		if(skillsSheetOptional.isPresent()) {
			SkillsSheet skillsSheet = skillsSheetOptional.get();
			
			Optional<Person> personAttachedTo;
			String status = jSkillsSheet.get("role").textValue();
			String personName = jSkillsSheet.get("person").textValue();
			switch(status) {
				case "consultant":
					personAttachedTo = personEntityController.getConsultantByName(personName);
					break;
				default:
					personAttachedTo = personEntityController.getApplicantByName(personName);
					break;
			}
			if(personAttachedTo.isPresent()) {
				skillsSheet.setPersonAttachedTo(personAttachedTo.get());
			}
			
			skillsSheet.setSoftSkillsList(this.getAllSoftSkills(jSkillsSheet.get("softskills").asText()));
			skillsSheet.setTechSkillsList(this.getAllTechSkills(jSkillsSheet.get("techskills").asText()));

			skillsSheet.setVersionNumber(oldVersionNumber+1);
			skillsSheet.set_id(new ObjectId());
			
			String authorMail = jSkillsSheet.get("authorMail").textValue();
			Optional<User> userAuthor = userEntityController.getUserByMail(authorMail);
			if(userAuthor.isPresent()) {
				skillsSheet.setVersionAuthor(userAuthor.get());
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
