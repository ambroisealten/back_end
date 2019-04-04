package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Mobility;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.SoftSkill;
import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;

public class SkillsSheetEntityController {
	@Autowired
	private SkillsSheetRepository skillsSheetRepository;
	
	@Autowired 
	private PersonEntityController personEntityController;

	public Optional<SkillsSheet> getSkillsSheetByName(String name) {
		return skillsSheetRepository.getSkillsSheetByName(name);
	}

	public ForbiddenException createSkillsSheet(JsonNode jSkillsSheet) {
		SkillsSheet newSkillsSheet = new SkillsSheet();
		newSkillsSheet.setName(jSkillsSheet.get("name").textValue());
		
		Optional<Person> personAttachedTo;
		String role = jSkillsSheet.get("role").textValue();
		switch(role) {
			case "consultant":
				personAttachedTo = personEntityController.getConsultantByName(jSkillsSheet.get("name").textValue());
				break;
			default:
				personAttachedTo = personEntityController.getApplicantByName(jSkillsSheet.get("name").textValue());
				break;
		}
		if(personAttachedTo.isPresent()) {
			newSkillsSheet.setPersonAttachedTo(personAttachedTo.get());
		}
		
		newSkillsSheet.setSoftSkillsList(this.getAllSoftSkills(jSkillsSheet.get("softskills").textValue()));
		
		
		
		return null;
	}
	
	/**
	 * Get a List of SoftSkills object given a String
	 * 
	 * @param mobilitiesString the String containing all soft skills for this skill sheet,
	 * declared in this format : "softskill1//grade1 softskill2//grade2"
	 * @return A List of SoftSkill
	 * @author Lucas Royackkers
	 */
	public List<SoftSkill> getAllSoftSkills(String softSkillsString) {
		List<SoftSkill> allSoftSkills = new ArrayList<SoftSkill>();
		String[] softSkillsStringSplitted = softSkillsString.split(" ");
		for(int i = 0; i < softSkillsStringSplitted.length; i++) {
			String[] softSkillParams = softSkillsStringSplitted[i].split("//");
			SoftSkill newSoftSkill = new SoftSkill();
			newSoftSkill.setName(softSkillParams[0]);
			newSoftSkill.setGrade(Integer.parseInt(softSkillParams[1]));
			allSoftSkills.add(newSoftSkill);
		}
		return allSoftSkills;
	}
	
	/**
	 * Get a List of TechSkills object given a String
	 * 
	 * @param mobilitiesString the String containing all soft skills for this skill sheet,
	 * declared in this format : "softskill1//grade1 softskill2//grade2"
	 * @return A List of TechSkill
	 * @author Lucas Royackkers
	 */
	public List<TechSkill> getAllTechSkills(String techSkillsString) {
		List<TechSkill> allTechSkills = new ArrayList<TechSkill>();
		String[] techSkillsStringSplitted = techSkillsString.split(" ");
		for(int i = 0; i < techSkillsStringSplitted.length; i++) {
			String[] techSkillParams = techSkillsStringSplitted[i].split("//");
			TechSkill newTechSkill = new TechSkill();
			newTechSkill.setName(techSkillParams[0]);
			newTechSkill.setGrade(Integer.parseInt(techSkillParams[1]));
			allTechSkills.add(newTechSkill);
		}
		return allTechSkills;
	}

	public List<SkillsSheet> getSkillsSheets() {
		// TODO Auto-generated method stub
		return null;
	}

}
