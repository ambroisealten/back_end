/**
 * 
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

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

	@Indexed(unique = true)
	private String code;
	private String codeDepartement;
	private String codeRegion;
	private String codePostaux;
	private String name;

	public City() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeDepartement() {
		return codeDepartement;
	}

	public void setCodeDepartement(String codeDepartement) {
		this.codeDepartement = codeDepartement;
	}

	public String getCodeRegion() {
		return codeRegion;
	}

	public void setCodeRegion(String codeRegion) {
		this.codeRegion = codeRegion;
	}

	public String getCodePostaux() {
		return codePostaux;
	}

	public void setCodePostaux(String codePostaux) {
		this.codePostaux = codePostaux;
	}

	@Override
	public String getIdentifier() {
		return this.getCode();
	}
}
