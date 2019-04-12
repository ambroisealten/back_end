package fr.alten.ambroiseJEE.utils;

/**
 * 
 * 
 * @author Thomas Decamp
 *
 */
public enum SoftSkillsGrades {
	ONE ("1"),
	ONEANDAHALF ("1.5"),
	TWO ("2"),
	THREE ("3"),
	FOUR ("4");
	
	private String grade = "";
	
	SoftSkillsGrades(String grade) {
		this.grade = grade;
	}
	
	public String toString() {
		return grade;
	}
}