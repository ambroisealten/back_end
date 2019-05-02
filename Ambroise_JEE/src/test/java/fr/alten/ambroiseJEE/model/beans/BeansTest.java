package fr.alten.ambroiseJEE.model.beans;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * @author Kylian Gehier s
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class BeansTest {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void check_CityBean() {
		
		// asserting the collection city exist
		assertTrue(mongoTemplate.collectionExists("city"));

		// asserting all unique index are present
		
		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("code", false);
		
		// getting all indexed field of the collection "city"
		List<IndexInfo> indexList = mongoTemplate.indexOps("city").getIndexInfo();

		for(IndexInfo index : indexList) {
			for(Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if(index.getName().equals(indexInMap.getKey())) {
					
					// checking the unicity of unique indexed fields - except for _id_ because mongoDB consider his unicity as false
					if(!index.getName().equals("_id_")) {assertTrue(index.isUnique());}
					indexInMap.setValue(true);
				}
			}
		}
		
		// Checking the presence of all indexes
		for(Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {			
			assertTrue(indexInMap.getValue());;	
		}

	}

}
