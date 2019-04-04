package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.SkillsSheet;

public interface SkillsSheetRepository extends MongoRepository<SkillsSheet, Long>{

	public Optional<SkillsSheet> getSkillsSheetByName(String name);
}
