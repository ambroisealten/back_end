package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * 
 * 
 * @author Lucas Royackkers
 *
 */
public class Employer implements Serializable{

	private static final long serialVersionUID = 8056737534510849309L;
	@Indexed(unique = true)
	private String name;
	
	public Employer() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
