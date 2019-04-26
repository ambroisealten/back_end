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
@Document(collection = "region")
public class Region extends Geographic implements Serializable {

	private static final long serialVersionUID = 3259704748445397040L;

	@Indexed(unique = true)
	private String code;

	private String name;

	public Region() {
		super();
	}

	public String getCode() {
		return this.code;
	}

	@Override
	public String getIdentifier() {
		return getCode();
	}

	public String getName() {
		return this.name;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setName(final String name) {
		this.name = name;
	}
}