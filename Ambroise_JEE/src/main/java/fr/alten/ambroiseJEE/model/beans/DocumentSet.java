/**
 * 
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.Pair;

import fr.alten.ambroiseJEE.model.beans.mobileDoc.MobileDoc;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Document(collection = "documentSet")
public class DocumentSet implements Serializable {

	private static final long serialVersionUID = 8130484628750269655L;
	
	@Id
	private transient ObjectId _id;
	
	@Indexed(unique= true)
	private String name;
	private List<MobileDoc> mobileDoc;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<MobileDoc> getMobileDocs() {
		return mobileDoc;
	}
	
	public void setMobileDocs(List<MobileDoc> mobileDoc) {
		this.mobileDoc = mobileDoc;
	}
	

	
}
