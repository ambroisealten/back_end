package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * @author Kylian Gehier
 *
 */
@Document(collection = "mobileDoc")
public class MobileDoc implements Serializable{

	private static final long serialVersionUID = -4615039971268324638L;

	@Id
	private transient ObjectId _id;
	
}
