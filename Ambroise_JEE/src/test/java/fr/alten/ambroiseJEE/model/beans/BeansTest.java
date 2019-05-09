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
	public void check_EmployerBean() {

		assertTrue(mongoTemplate.collectionExists("employer"));

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("name", false);

		List<IndexInfo> indexList = mongoTemplate.indexOps("employer").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					if (!index.getName().equals("_id_")) {
						assertTrue(index.isUnique());
					}
					indexInMap.setValue(true);
				}
			}
		}

		// Checking the presence of all indexes
		for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
			assertTrue(indexInMap.getValue());
		}
	}

	/**
	 * @test Testing file collection presence in base and index with their unicity
	 *
	 * @author Andy Chabalier
	 */
	@Test
	public void check_FileBean() {

		// asserting the collection files exist
		assertTrue(mongoTemplate.collectionExists("files"));

		// asserting all unique index are present

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);

		// getting all indexed field of the collection "sector"
		List<IndexInfo> indexList = mongoTemplate.indexOps("files").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					// checking the unicity of unique indexed fields - except for _id_ because
					// mongoDB consider his unicity as false
					if (!index.getName().equals("_id_")) {
						assertTrue(index.isUnique());
					}
					indexInMap.setValue(true);
				}
			}
		}

		// Checking the presence of all indexes
		for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
			assertTrue(indexInMap.getValue());
		}
	}

	@Test
	public void check_PersonBean() {

		assertTrue(mongoTemplate.collectionExists("person"));

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("mail", false);

		List<IndexInfo> indexList = mongoTemplate.indexOps("person").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {
					if (!index.getName().equals("_id_")) {
						assertTrue(index.isUnique());
					}
				}
			}
		}
	}

	/**
	 * @test Testing job collection presence in base and index with their unicity
	 *
	 * @author Camille Schnell
	 */
	@Test
	public void check_JobBean() {

		assertTrue(mongoTemplate.collectionExists("job"));

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("title", false);

		List<IndexInfo> indexList = mongoTemplate.indexOps("job").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					if (!index.getName().equals("_id_")) {
						assertTrue(index.isUnique());
					}
					indexInMap.setValue(true);
				}
			}
		}

		// Checking the presence of all indexes
		for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
			assertTrue(indexInMap.getValue());
		}
	}

	/**
	 * @test Testing region collection presence in base and index with their unicity
	 *
	 * @author Camille Schnell
	 */
	@Test
	public void check_RegionBean() {

		assertTrue(mongoTemplate.collectionExists("region"));

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("code", false);

		List<IndexInfo> indexList = mongoTemplate.indexOps("region").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					if (!index.getName().equals("_id_")) {
						assertTrue(index.isUnique());
					}
					indexInMap.setValue(true);
				}
			}
		}

		// Checking the presence of all indexes
		for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
			assertTrue(indexInMap.getValue());
		}
	}

	/**
	 * @test Testing departement collection presence in base and index with their unicity
	 *
	 * @author Camille Schnell
	 */
	@Test
	public void check_DepartementBean() {

		assertTrue(mongoTemplate.collectionExists("departement"));

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("code", false);

		List<IndexInfo> indexList = mongoTemplate.indexOps("departement").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					if (!index.getName().equals("_id_")) {
						assertTrue(index.isUnique());
					}
					indexInMap.setValue(true);
				}
			}
		}

		// Checking the presence of all indexes
		for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
			assertTrue(indexInMap.getValue());
		}
	}

}
