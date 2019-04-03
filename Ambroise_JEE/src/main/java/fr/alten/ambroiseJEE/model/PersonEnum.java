package fr.alten.ambroiseJEE.model;


/**
 * 
 * 
 * @author Lucas Royackkers
 *
 */
public enum PersonEnum {

	DEMISSIONAIRE(-1),
	APPLICANT(0),
	CONSULTANT(1);
		
	
	private int value;
	
	PersonEnum(int role){
		this.value = role;
	}

	public int getValue() {
		return value;
	}
}
