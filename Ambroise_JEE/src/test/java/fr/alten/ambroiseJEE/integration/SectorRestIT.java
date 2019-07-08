/**
 *
 */
package fr.alten.ambroiseJEE.integration;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.alten.ambroiseJEE.model.beans.Sector;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.SectorRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;

/**
 *
 * @author Andy Chabalier
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SectorRestIT {

	private static User userAdmin = new User();
	private static Sector sector = new Sector();

	private static Gson gson;

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

	@BeforeClass
	public static void beforeTests() {
		TokenIgnore.createDir();
		SectorRestIT.initAdminUser();
		SectorRestIT.initSector();
		SectorRestIT.initGson();
	}

	private static void initAdminUser() {
		SectorRestIT.userAdmin.setForname("tempUserAdmin");
		SectorRestIT.userAdmin.setMail("tempUserAdmin@mail.com");
		SectorRestIT.userAdmin.setName("tempUserAdminName");
	}

	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		SectorRestIT.gson = builder.create();
	}

	private static void initSector() {
		SectorRestIT.sector.setName("newSector");
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SectorRepository sectorRepository;

	@After
	public void afterEachTest() {
		this.userRepository.deleteAll();
		this.sectorRepository.deleteAll();

	}

	@Before
	public void beforeEachTest() {
		this.userRepository.insert(SectorRestIT.userAdmin);
		this.userRepository.deleteAll();
		this.sectorRepository.deleteAll();
	}

	@Test
	public void createSector_with_conflict() throws Exception {

		// setup
		final String newSector = "{" + "\"name\":\"newSector\"" + "}";

		// Pre-inserting a Sector with name code as this.sector to create a
		// ConflictException
		this.sectorRepository.insert(SectorRestIT.sector);
		// Checking pre-insertion
		Assert.assertTrue(this.sectorRepository.findByName("newSector").isPresent());

		final MvcResult result = this.mockMvc.perform(
				MockMvcRequestBuilders.post("/sector").contentType(MediaType.APPLICATION_JSON).content(newSector))
				.andReturn();

		// Checking that the ResponseBody contain a ConflictException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.sector is in base
		Assertions.assertThat(this.sectorRepository.count()).isEqualTo(1);
	}

	@Test
	public void createSector_with_missingRequiredFields() throws Exception {

		// setup
		final String newSector = "{}";

		final MvcResult result = this.mockMvc.perform(
				MockMvcRequestBuilders.post("/sector").contentType(MediaType.APPLICATION_JSON).content(newSector))
				.andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no sector in base
		Assertions.assertThat(this.sectorRepository.findAll()).isEmpty();
	}

	@Test
	public void createSector_with_success() throws Exception {

		// setup
		final String newSector = "{" + "\"name\":\"newSector\"" + "}";

		final MvcResult result = this.mockMvc.perform(
				MockMvcRequestBuilders.post("/sector").contentType(MediaType.APPLICATION_JSON).content(newSector))
				.andReturn();

		// Checking that the ResponseBody contain a CreatedException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new sector in base and its fields's value
		final Optional<Sector> sectorOptional = this.sectorRepository.findByName("newSector");
		Assert.assertTrue(sectorOptional.isPresent());
		Assertions.assertThat(sectorOptional.get().getName()).isEqualTo("newSector");
	}

	@Test
	public void deleteSector_with_missingRequiredFields() throws Exception {

		// setup
		final String sectorToDelete = "{}";

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/sector")
				.contentType(MediaType.APPLICATION_JSON).content(sectorToDelete)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void deleteSector_with_resourceNotFound() throws Exception {

		// setup
		final String sectorToDelete = "{" + "\"name\":\"sectorFalse\"" + "}";
		// Checking if there is not already a sector in base with the code : 00000
		Assert.assertFalse(this.sectorRepository.findByName("sectorFalse").isPresent());

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/sector")
				.contentType(MediaType.APPLICATION_JSON).content(sectorToDelete)).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void deleteSector_with_success() throws Exception {

		// setup
		final String sectorToDelete = "{" + "\"name\":\"newSector\"" + "}";
		// Pre-inserting a Sector with name code as this.sector for having a sector to
		// delete
		// with success
		this.sectorRepository.insert(SectorRestIT.sector);
		// Checking pre-insertion
		final Optional<Sector> sectorOptional = this.sectorRepository.findByName("newSector");
		Assert.assertTrue(sectorOptional.isPresent());

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/sector")
				.contentType(MediaType.APPLICATION_JSON).content(sectorToDelete)).andReturn();

		// Checking that the ResponseBody contain a OkException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("OkException"));

	}

	@Test
	public void getSectors() throws Exception {

		// Pre-insertion of 20 sectors for test
		for (int i = 0; i < 20; i++) {
			final Sector sectorForGet = new Sector();
			sectorForGet.setName("Sector" + i);
			this.sectorRepository.insert(sectorForGet);
			final Optional<Sector> sectorOptional = this.sectorRepository.findByName("Sector" + i);
			Assert.assertTrue(sectorOptional.isPresent());
			Assertions.assertThat(sectorOptional.get().getName()).isEqualTo("Sector" + i);
		}

		final MvcResult result = this.mockMvc
				.perform(MockMvcRequestBuilders.get("/sectors").contentType(MediaType.APPLICATION_JSON)).andReturn();

		final String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (final JsonElement jsector : SectorRestIT.gson.fromJson(jsonResult, JsonArray.class)) {
			final JsonObject jsonSector = jsector.getAsJsonObject();
			Assertions.assertThat(jsonSector.get("name").getAsString()).isEqualTo("Sector" + count);
			count++;
		}
	}

	@Test
	public void updateSector_with_missingRequiredFields() throws Exception {

		// setup
		final String sectorToDelete = "{}";

		final MvcResult result = this.mockMvc.perform(
				MockMvcRequestBuilders.put("/sector").contentType(MediaType.APPLICATION_JSON).content(sectorToDelete))
				.andReturn();

		Assert.assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void updateSector_with_resourceNotFound() throws Exception {

		// setup
		final String updatedSector = "{" + "\"name\":\"newSectorFalse\"," + "\"oldName\":\"sectorNotFound\"" + "}";

		final MvcResult result = this.mockMvc.perform(
				MockMvcRequestBuilders.put("/sector").contentType(MediaType.APPLICATION_JSON).content(updatedSector))
				.andReturn();

		Assert.assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void updateSector_with_success() throws Exception {

		// setup
		final String updatedSector = "{" + "\"name\":\"updateSector\"," + "\"oldName\":\"newSector\"" + "}";
		// Pre-inserting a sector to update
		this.sectorRepository.insert(SectorRestIT.sector);
		// Checking pre-insertion
		Assert.assertTrue(this.sectorRepository.findByName("newSector").isPresent());

		final MvcResult result = this.mockMvc.perform(
				MockMvcRequestBuilders.put("/sector").contentType(MediaType.APPLICATION_JSON).content(updatedSector))
				.andReturn();

		Assert.assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated sector in base
		final Optional<Sector> sectorOptional = this.sectorRepository.findByName("updateSector");
		Assert.assertTrue(sectorOptional.isPresent());
		Assertions.assertThat(sectorOptional.get().getName()).isEqualTo("updateSector");
	}

	@Test
	public void y_testIndex() {
		// asserting the collection city exist
		Assert.assertTrue(this.mongoTemplate.collectionExists("sector"));

		// asserting all unique index are present

		final HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("name", false);

		// getting all indexed field of the collection "sector"
		final List<IndexInfo> indexList = this.mongoTemplate.indexOps("sector").getIndexInfo();

		for (final IndexInfo index : indexList) {
			for (final Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					// checking the unicity of unique indexed fields - except for _id_ because
					// mongoDB consider his unicity as false
					if (!index.getName().equals("_id_")) {
						Assert.assertTrue(index.isUnique());
					}
					indexInMap.setValue(true);
				}
			}
		}

		// Checking the presence of all indexes
		for (final Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
			Assert.assertTrue(indexInMap.getValue());
		}
	}
}
