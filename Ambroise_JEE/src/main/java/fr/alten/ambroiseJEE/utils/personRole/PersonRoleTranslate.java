package fr.alten.ambroiseJEE.utils.personRole;

/**
 * 
 * @author Thomas Decamp
 *
 */
public class PersonRoleTranslate {
	/**
	 * 
	 * @param String
	 * @return PersonRole
	 * @author Thomas Decamp
	 */
	public static PersonRole translateRole(String role) {
		switch (role) {
			case ("DEMISSIONNAIRE") :
				return PersonRole.DEMISSIONNAIRE;
			case ("APPLICANT") :
				return PersonRole.APPLICANT;
			case ("CONSULTANT") :
				return PersonRole.CONSULTANT;
		}
		return null;
	}
}
