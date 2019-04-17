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

	public String getCode() {
		return code;
	}

	public String getCodeDepartement() {
		return codeDepartement;
	}

	public String getCodePostaux() {
		return codePostaux;
	}

	public String getCodeRegion() {
		return codeRegion;
	}

	@Override
	public String getIdentifier() {
		return this.getCode();
	}

	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCodeDepartement(String codeDepartement) {
		this.codeDepartement = codeDepartement;
	}

	public void setCodePostaux(String codePostaux) {
		this.codePostaux = codePostaux;
	}

	public void setCodeRegion(String codeRegion) {
		this.codeRegion = codeRegion;
	}

	public void setName(String name) {
		this.name = name;
	}
}
