/**
 * 
 */
package fr.alten.ambroiseJEE.security;

/**
 * @author Andy Chabalier
 *
 */
public enum Roles {

	DEFAULT_USER_ROLE(-1);
	
	private int value;
	
	Roles(int role){
		this.value = role;
	}

	public int getValue() {
		return value;
	}
}
