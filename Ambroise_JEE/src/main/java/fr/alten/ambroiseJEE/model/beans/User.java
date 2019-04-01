/**
 * 
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User bean object role and password fields are transient so that they don't
 * appear in the serialized object (json)
 * 
 * @author Andy Chabalier
 *
 */
@Document(collection = "user")
public class User implements Serializable {

	private static final long serialVersionUID = -964604045021431143L;
	
	@Indexed(unique = true)
	private String mail;
	private transient String pswd;
	private String name;
	private String forname;
	private transient int role;

	public User() {
		super();
	}

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getForname() {
		return forname;
	}

	public void setForname(String forname) {
		this.forname = forname;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
}

