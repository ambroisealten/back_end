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
 *
 * @author Andy Chabalier
 *
 */
@Document(collection = "city")
public class City extends Geographic implements Serializable {

	private static final long serialVersionUID = 7473150851064346366L;

	@Id
	private transient ObjectId _id;

	@Indexed(unique = true)
	private String code;
	private String codeDepartement;
	private String codeRegion;
	private String codePostaux;
	private String nom;

	public City() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getCode() {
		return this.code;
	}

	public String getCodeDepartement() {
		return this.codeDepartement;
	}

	public String getCodePostaux() {
		return this.codePostaux;
	}

	public String getCodeRegion() {
		return this.codeRegion;
	}

	@Override
	public String getIdentifier() {
		return getCode();
	}

	public String getNom() {
		return this.nom;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setCodeDepartement(final String codeDepartement) {
		this.codeDepartement = codeDepartement;
	}

	public void setCodePostaux(final String codePostaux) {
		this.codePostaux = codePostaux;
	}

	public void setCodeRegion(final String codeRegion) {
		this.codeRegion = codeRegion;
	}

	public void setNom(final String name) {
		this.nom = name;
	}
}
