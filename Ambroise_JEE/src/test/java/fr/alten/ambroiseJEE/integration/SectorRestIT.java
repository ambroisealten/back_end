/**
 * 
 */
package fr.alten.ambroiseJEE.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.FileNotFoundException;
import java.util.Optional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SectorRepository sectorRepository;

	@BeforeClass
	public static void beforeTests() {
		TokenIgnore.createDir();
		initAdminUser();
		initSector();
		initGson();
	}

	private static void initAdminUser() {
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
	}

	private static void initSector() {
		sector.setName("newSector");
	}

	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);

		if (!mongoTemplate.collectionExists("sector")) {
			// Recreate the collection with the unique index "name" -> index beeing dropped
			// when the collection is dropped.
			mongoTemplate.createCollection(Sector.class);
			mongoTemplate.indexOps("sector").ensureIndex(new Index().on("name", Direction.ASC).unique());
		}
	}

	@After
	public void afterEachTest() {
		mongoTemplate.getDb().getCollection("user").drop();
		mongoTemplate.getDb().getCollection("sector").drop();

	}

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

	@Test
	public void createSector_with_success() throws Exception {

		// setup
		String newSector = "{" + "\"name\":\"newSector\"" + "}";

		MvcResult result = this.mockMvc
				.perform(post("/sector").contentType(MediaType.APPLICATION_JSON).content(newSector)).andReturn();

		// Checking that the ResponseBody contain a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new sector in base and its fields's value
		Optional<Sector> sectorOptional = this.sectorRepository.findByName("newSector");
		assertTrue(sectorOptional.isPresent());
		assertThat(sectorOptional.get().getName()).isEqualTo("newSector");
	}

	@Test
	public void createSector_with_conflict() throws Exception {

		// setup
		String newSector = "{" + "\"name\":\"newSector\"" + "}";

		// Pre-inserting a Sector with name code as this.sector to create a
		// ConflictException
		sectorRepository.insert(sector);
		// Checking pre-insertion
		assertTrue(this.sectorRepository.findByName("newSector").isPresent());

		MvcResult result = this.mockMvc
				.perform(post("/sector").contentType(MediaType.APPLICATION_JSON).content(newSector)).andReturn();

		// Checking that the ResponseBody contain a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.sector is in base
		assertThat(this.sectorRepository.findAll().size()).isEqualTo(1);
	}

	@Test
	public void createSector_with_missingRequiredFields() throws Exception {

		// setup
		String newSector = "{}";

		MvcResult result = this.mockMvc
				.perform(post("/sector").contentType(MediaType.APPLICATION_JSON).content(newSector)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no sector in base
		assertThat(this.sectorRepository.findAll()).isEmpty();
	}

	@Test
	public void deleteSector_with_success() throws Exception {

		// setup
		String sectorToDelete = "{" + "\"name\":\"newSector\"" + "}";
		// Pre-inserting a Sector with name code as this.sector for having a sector to
		// delete
		// with success
		sectorRepository.insert(sector);
		// Checking pre-insertion
		Optional<Sector> sectorOptional = this.sectorRepository.findByName("newSector");
		assertTrue(sectorOptional.isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/sector").contentType(MediaType.APPLICATION_JSON).content(sectorToDelete)).andReturn();

		// Checking that the ResponseBody contain a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

	}

	@Test
	public void deleteSector_with_resourceNotFound() throws Exception {

		// setup
		String sectorToDelete = "{" + "\"name\":\"sectorFalse\"" + "}";
		// Checking if there is not already a sector in base with the code : 00000
		assertFalse(this.sectorRepository.findByName("sectorFalse").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/sector").contentType(MediaType.APPLICATION_JSON).content(sectorToDelete)).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void deleteSector_with_missingRequiredFields() throws Exception {

		// setup
		String sectorToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/sector").contentType(MediaType.APPLICATION_JSON).content(sectorToDelete)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void getSectors() throws Exception {

		// Pre-insertion of 20 cities for test
		for (int i = 0; i < 20; i++) {
			Sector sectorForGet = new Sector();
			sectorForGet.setName("Sector" + i);
			this.sectorRepository.insert(sectorForGet);
			Optional<Sector> sectorOptional = this.sectorRepository.findByName("Sector" + i);
			assertTrue(sectorOptional.isPresent());
			assertThat(sectorOptional.get().getName()).isEqualTo("Sector" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/cities").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (JsonElement jsector : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonSector = jsector.getAsJsonObject();
			assertThat(jsonSector.get("name").getAsString()).isEqualTo("Sector" + count);
			count++;
		}
	}

	@Test
	public void updateSector_with_success() throws Exception {

		// setup
		String updatedSector = "{" + "\"name\":\"updateSector\"," + "\"oldName\":\"newSector\"" + "}";
		// Pre-inserting a sector to update
		sectorRepository.insert(sector);
		// Checking pre-insertion
		assertTrue(this.sectorRepository.findByName("newSector").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/sector").contentType(MediaType.APPLICATION_JSON).content(updatedSector)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated sector in base
		Optional<Sector> sectorOptional = this.sectorRepository.findByName("updateSector");
		assertTrue(sectorOptional.isPresent());
		assertThat(sectorOptional.get().getName()).isEqualTo("updateSector");
	}

	@Test
	public void updateSector_with_resourceNotFound() throws Exception {

		// setup
		String updatedSector = "{" + "\"name\":\"newSectorFalse\"," + "\"oldName\":\"sectorNotFound\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/sector").contentType(MediaType.APPLICATION_JSON).content(updatedSector)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void updateSector_with_missingRequiredFields() throws Exception {

		// setup
		String sectorToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/sector").contentType(MediaType.APPLICATION_JSON).content(sectorToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void z_DroppingDatabase() {
		// Last test run to drop the database for next test classes.
		mongoTemplate.getDb().drop();
	}

}
