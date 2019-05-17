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

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.DiplomaRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;

/**
 * 
 * @author Thomas Decamp
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DiplomaRestIT {

	private static User userAdmin = new User();
	private static Diploma diploma = new Diploma();

	private static Gson gson;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DiplomaRepository diplomaRepository;

	@BeforeClass
	public static void beforeTests() {
		TokenIgnore.createDir();
		initAdminUser();
		initDiploma();
		initGson();
	}

	private static void initAdminUser() {
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
	}

	private static void initDiploma() {
		diploma.setName("newDiploma");
	}

	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);

		if (!mongoTemplate.collectionExists("diploma")) {
			// Recreate the collection with the unique index "name" -> index beeing dropped
			// when the collection is dropped.
			mongoTemplate.createCollection(Diploma.class);
			mongoTemplate.indexOps("diploma").ensureIndex(new Index().on("name", Direction.ASC).unique());
		}
	}

	@After
	public void afterEachTest() {
		userRepository.deleteAll();
		diplomaRepository.deleteAll();

	}

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

	@Test
	public void createDiploma_with_success() throws Exception {

		// setup
		String newDiploma = "{" + "\"name\":\"newDiploma\"" + "\"yearOfResult\":\"2010\"" + "}";

		MvcResult result = this.mockMvc
				.perform(post("/diploma").contentType(MediaType.APPLICATION_JSON).content(newDiploma)).andReturn();

		// Checking that the ResponseBody contain a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new diploma in base and its fields's value
		Optional<Diploma> diplomaOptional = this.diplomaRepository.findByNameAndYearOfResult("newDiploma", "2010");
		assertTrue(diplomaOptional.isPresent());
		assertThat(diplomaOptional.get().getName()).isEqualTo("newDiploma");
	}

	@Test
	public void createDiploma_with_conflict() throws Exception {

		// setup
		String newDiploma = "{" + "\"name\":\"newDiploma\"," + "\"yearOfResult\":\"2010\"" + "}";

		// Pre-inserting a Diploma with name name as this.diploma to create a
		// ConflictException
		diplomaRepository.insert(diploma);
		// Checking pre-insertion
		assertTrue(this.diplomaRepository.findByNameAndYearOfResult("newDiploma", "2010").isPresent());

		MvcResult result = this.mockMvc
				.perform(post("/diploma").contentType(MediaType.APPLICATION_JSON).content(newDiploma)).andReturn();

		// Checking that the ResponseBody contain a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.diploma is in base
		assertThat(this.diplomaRepository.count()).isEqualTo(1);
	}

	@Test
	public void createDiploma_with_missingRequiredFields() throws Exception {

		// setup
		String newDiploma = "{}";

		MvcResult result = this.mockMvc
				.perform(post("/diploma").contentType(MediaType.APPLICATION_JSON).content(newDiploma)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no diploma in base
		assertThat(this.diplomaRepository.findAll()).isEmpty();
	}

	@Test
	public void deleteDiploma_with_success() throws Exception {

		// setup
		String diplomaToDelete = "{" + "\"name\":\"newDiploma\"," + "\"yearOfResult\":\"2010\"" + "}";
		// Pre-inserting a Diploma with name name as this.diploma for having a diploma to delete
		// delete
		// with success
		diplomaRepository.insert(diploma);
		// Checking pre-insertion
		Optional<Diploma> diplomaOptional = this.diplomaRepository.findByNameAndYearOfResult("newDiploma", "2010");
		String diplomaId = diplomaOptional.get().get_id().toString();
		assertTrue(diplomaOptional.isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/diploma").contentType(MediaType.APPLICATION_JSON).content(diplomaToDelete)).andReturn();

		// Checking that the ResponseBody contain a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the diplomaToDelete had been deactivated
//		assertThat(this.diplomaRepository.findBy_id(diplomaId).get().getName()).startsWith("deactivated");
	}

	@Test
	public void deleteDiploma_with_resourceNotFound() throws Exception {

		// setup
		String diplomaToDelete = "{" + "\"name\":\"newDiploma\"," + "\"yearOfResult\":\"2010\"" + "}";
		// Checking if there is not already a diploma in base with the name : newDiploma
		assertFalse(this.diplomaRepository.findByNameAndYearOfResult("newDiploma", "2010").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/diploma").contentType(MediaType.APPLICATION_JSON).content(diplomaToDelete)).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void deleteDiploma_with_missingRequiredFields() throws Exception {

		// setup
		String diplomaToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/diploma").contentType(MediaType.APPLICATION_JSON).content(diplomaToDelete)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	// TODO : Reecrire la pré-insertion en créant des Jsons de retour et vérifiant
	// bien que
	// le retour est EGAL à ces Jsons de retour.
	@Test
	public void getDiplomas() throws Exception {

		// Pre-insertion of 20 diplomas for test
		for (int i = 0; i < 20; i++) {
			Diploma diplomaForGet = new Diploma();
			diplomaForGet.setName("diploma" + i);
			diplomaForGet.setYearOfResult("year" + i);
			this.diplomaRepository.insert(diplomaForGet);
			Optional<Diploma> diplomaOptional = this.diplomaRepository.findByNameAndYearOfResult("diploma" + i, "year" + i);
			assertTrue(diplomaOptional.isPresent());
			assertThat(diplomaOptional.get().getName()).isEqualTo("diploma" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/diplomas").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same diplomas as inserted before
		int count = 0;
		for (JsonElement jdiploma : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonDiploma = jdiploma.getAsJsonObject();
			assertThat(jsonDiploma.get("name").getAsString()).isEqualTo("diploma" + count);
			count++;
		}
	}

	@Test
	public void updateDiploma_with_success() throws Exception {

		// setup
		String updatedDiploma = "{" + "\"name\":\"updateDiploma\"," +  "\"oldName\":\"newDiploma\"," + "\"yearOfResult\":\"2010\"," + "\"oldYearOfResult\":\"2020\"" + "}";
		// Pre-inserting a diploma to update
		diplomaRepository.insert(diploma);
		// Checking pre-insertion
		assertTrue(this.diplomaRepository.findByNameAndYearOfResult("newDiploma", "2010").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/diploma").contentType(MediaType.APPLICATION_JSON).content(updatedDiploma)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated diploma in base
		Optional<Diploma> diplomaOptional = this.diplomaRepository.findByNameAndYearOfResult("updateDiploma", "2010");
		assertTrue(diplomaOptional.isPresent());
		assertThat(diplomaOptional.get().getName()).isEqualTo("updateDiploma");
	}

	@Test
	public void updateDiploma_with_resourceNotFound() throws Exception {

		// setup
		String updatedDiploma = "{" + "\"name\":\"newDiplomaFalse\"," +  "\"oldName\":\"diplomaNotFound\"," + "\"yearOfResult\":\"2010\"," + "\"oldYearOfResult\":\"2020\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/diploma").contentType(MediaType.APPLICATION_JSON).content(updatedDiploma)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void updateDiploma_with_missingRequiredFields() throws Exception {

		// setup
		String diplomaToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/diploma").contentType(MediaType.APPLICATION_JSON).content(diplomaToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	//@Test
//	public void z_DroppingDatabase() {
//		// Last test run to drop the database for next test classes.
//		mongoTemplate.getDb().drop();
//	}
}
