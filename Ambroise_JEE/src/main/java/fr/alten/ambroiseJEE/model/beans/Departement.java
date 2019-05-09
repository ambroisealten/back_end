/**
 *
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Andy Chabalier
 *
 */
@Document(collection = "departement")
public class Departement extends Geographic implements Serializable {

	private static final long serialVersionUID = -9058008558311065461L;

	@Id
	private transient ObjectId _id;
	
	@Indexed(unique = true)
	private String code;

	private String nom;
	private String codeRegion;

	public Departement() {
		super();
	}

	@Override
	public String getIdentifier() {
		return getCode();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getCodeRegion() {
		return codeRegion;
	}

	public void setCodeRegion(String codeRegion) {
		this.codeRegion = codeRegion;
	}
}
