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
		return this._id;
	}

	public String getAgency() {
		return this.agency;
	}

	public String getForname() {
		return this.forname;
	}

	public String getMail() {
		return this.mail;
	}

	public String getName() {
		return this.name;
	}

	public String getPswd() {
		return this.pswd;
	}

	public UserRole getRole() {
		return this.role;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setAgency(final String agency) {
		this.agency = agency;
	}

	public void setForname(final String forname) {
		this.forname = forname;
	}

	public void setMail(final String mail) {
		this.mail = mail;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPswd(final String pswd) {
		this.pswd = pswd;
	}

	public void setRole(final UserRole role) {
		this.role = role;
	}
}