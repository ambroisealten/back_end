/**
 * 
 */
package fr.alten.ambroiseJEE.security;

/**
 * @author Andy Chabalier
 *
 */
public enum Roles {

	DESACTIVATED_USER_ROLE(-1),
	DEFAULT_USER_ROLE(0),
	ADMINISTRATOR_USER_ROLE(0);
		
	
	private int value;
	
	Roles(int role){
		this.value = role;
	}

	public int getValue() {
		return value;
	}
}
