package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.MobileDoc;
import fr.alten.ambroiseJEE.model.dao.MobileDocRepository;

/**
 * 
 * @author Kylian Gehier
 *
 */
@Service
public class MobileDocEntityController {
	
	@Autowired
	private MobileDocRepository mobileDocRepository;
	
	public List<MobileDoc> getMobileDocs() {
		return mobileDocRepository.findAll();
	}
}
