package fr.alten.ambroiseJEE.model.beans;

/**
 * Defines the relation between a Skill and a grade. Wrap a Skill with a grade
 * (double)
 *
 * @author Lucas Royackkers
 *
 */
public class SkillGraduated {

	private final Skill skill;
	private double grade;

	public SkillGraduated(final Skill skill, final double grade) {
		super();
		this.skill = skill;
		this.grade = grade;
	}

	/**
	 * Defines if a skill is the same as an other one (with its name and 'isSoft'
	 * parameters)
	 *
	 * @param other an other SkillGraduated that we want to compare
	 * @return true if the objects are the same, false if not
	 * @author Lucas Royackkers
	 */
	public boolean compareSkill(final SkillGraduated other) {
		return getName().equals(other.getName()) && this.skill.getIsSoft() == other.getSkill().getIsSoft();
	}

	public double getGrade() {
		return this.grade;
	}

	public String getName() {
		return this.skill.getName();
	}

	public Skill getSkill() {
		return this.skill;
	}

	public void setGrade(final double grade) {
		this.grade = grade;
	}
	
	public boolean isSoft() {
		return this.getSkill().isSoft();
	}

}
