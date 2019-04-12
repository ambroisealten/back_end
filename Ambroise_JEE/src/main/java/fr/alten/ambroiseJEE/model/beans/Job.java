package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * 
 * 
 * @author Lucas Royackkers
 *
 */
public class Job implements Serializable{

	private static final long serialVersionUID = -8259196238254390780L;
	@Indexed(unique = true)
	private String title;
	
	public Job() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
