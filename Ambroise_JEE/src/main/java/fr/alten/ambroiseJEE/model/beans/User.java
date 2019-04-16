/**
 *
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.alten.ambroiseJEE.security.UserRole;

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

	@Id
	private transient ObjectId _id;
	@Indexed(unique = true)
	private String mail;
	private transient String pswd;
	private String name;
	private String forname;
	private transient UserRole role;
	private String agency;

	public User() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}

	public String getAgency() {
		return agency;
	}

	public String getForname() {
		return forname;
	}

	public String getMail() {
		return mail;
	}

	public String getName() {
		return name;
	}

	public String getPswd() {
		return pswd;
	}

	public UserRole getRole() {
		return role;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public void setForname(String forname) {
		this.forname = forname;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
}