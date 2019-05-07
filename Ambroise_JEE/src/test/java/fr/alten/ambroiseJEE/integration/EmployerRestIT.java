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

import org.bson.types.ObjectId;
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

import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.EmployerRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;

/**
 *
 * @author Lucas Royackkers
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployerRestIT {

	private static User userAdmin = new User();
	private static Employer employer = new Employer();

	private static Gson gson;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EmployerRepository employerRepository;

	@BeforeClass
	public static void beforeTests() {
		TokenIgnore.createDir();
		initAdminUser();
		initEmployer();
		initGson();
	}

	private static void initAdminUser() {
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
	}

	private static void initEmployer() {
		employer.setName("newEmployer");
	}

	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);

		if (!mongoTemplate.collectionExists("employer")) {
			// Recreate the collection with the unique index "name" -> index beeing dropped
			// when the collection is dropped.
			mongoTemplate.createCollection(Employer.class);
			mongoTemplate.indexOps("employer").ensureIndex(new Index().on("name", Direction.ASC).unique());
		}
	}

	@After
	public void afterEachTest() {
		mongoTemplate.getDb().getCollection("user").drop();
		mongoTemplate.getDb().getCollection("employer").drop();

	}

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

	@Test
	public void createEmployer_with_success() throws Exception {

		// setup
		String newEmployer = "{" + "\"name\":\"newEmployer\"" + "}";

		MvcResult result = this.mockMvc.perform(post("/employer").contentType(MediaType.APPLICATION_JSON).content(newEmployer))
				.andReturn();

		// Checking that the ResponseBody contain a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new employer in base and its fields's value
		Optional<Employer> employerOptional = this.employerRepository.findByName("newEmployer");
		assertTrue(employerOptional.isPresent());
		assertThat(employerOptional.get().getName()).isEqualTo("newEmployer");
	}

	@Test
	public void createEmployer_with_conflict() throws Exception {

		// setup
		String newEmployer = "{" + "\"name\":\"newEmployer\"" + "}";

		// Pre-inserting a Employer with name code as this.employer to create a
		// ConflictException
		employerRepository.insert(employer);
		// Checking pre-insertion
		assertTrue(this.employerRepository.findByName("newEmployer").isPresent());

		MvcResult result = this.mockMvc.perform(post("/employer").contentType(MediaType.APPLICATION_JSON).content(newEmployer))
				.andReturn();

		// Checking that the ResponseBody contain a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.employer is in base
		assertThat(this.employerRepository.findAll().size()).isEqualTo(1);
	}

	@Test
	public void createEmployer_with_missingRequiredFields() throws Exception {

		// setup
		String newEmployer = "{}";

		MvcResult result = this.mockMvc.perform(post("/employer").contentType(MediaType.APPLICATION_JSON).content(newEmployer))
				.andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no employer in base
		assertThat(this.employerRepository.findAll()).isEmpty();
	}

	@Test
	public void deleteEmployer_with_success() throws Exception {

		// setup
		String employerToDelete = "{" + "\"name\":\"newEmployer\"" + "}";
		// Pre-inserting a Employer with name code as this.employer for having a employer to delete
		// with success
		employerRepository.insert(employer);
		// Checking pre-insertion
		Optional<Employer> employerOptional = this.employerRepository.findByName("newEmployer");
		ObjectId employerOptionalID = employerOptional.get().get_id();
		assertTrue(employerOptional.isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/employer").contentType(MediaType.APPLICATION_JSON).content(employerToDelete)).andReturn();

		// Checking that the ResponseBody contain a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the employerToDelete had been deactivated
		assertThat(this.employerRepository.findBy_id(employerOptionalID).get().getName()).startsWith("deactivated");
	}

	@Test
	public void deleteEmployer_with_resourceNotFound() throws Exception {

		// setup
		String employerToDelete = "{" + "\"name\":\"employerFalse\"" + "}";
		// Checking if there is not already a employer in base with the code : 00000
		assertFalse(this.employerRepository.findByName("employerFalse").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/employer").contentType(MediaType.APPLICATION_JSON).content(employerToDelete)).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void deleteEmployer_with_missingRequiredFields() throws Exception {

		// setup
		String employerToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/employer").contentType(MediaType.APPLICATION_JSON).content(employerToDelete)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	// TODO : Reecrire la pré-insertion en créant des Jsons de retour et vérifiant
	// bien que
	// le retour est EGAL à ces Jsons de retour.
	@Test
	public void getEmployers() throws Exception {

		// Pre-insertion of 20 cities for test
		for (int i = 0; i < 20; i++) {
			Employer employerForGet = new Employer();
			employerForGet.setName("Employer" + i);
			this.employerRepository.insert(employerForGet);
			Optional<Employer> employerOptional = this.employerRepository.findByName("Employer" + i);
			assertTrue(employerOptional.isPresent());
			assertThat(employerOptional.get().getName()).isEqualTo("Employer" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/cities").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (JsonElement jemployer : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonEmployer = jemployer.getAsJsonObject();
			assertThat(jsonEmployer.get("name").getAsString()).isEqualTo("Employer" + count);
			count++;
		}
	}

	@Test
	public void updateEmployer_with_success() throws Exception {

		// setup
		String updatedEmployer = "{" + "\"name\":\"updateEmployer\"," + "\"oldName\":\"newEmployer\"" + "}";
		// Pre-inserting a employer to update
		employerRepository.insert(employer);
		// Checking pre-insertion
		assertTrue(this.employerRepository.findByName("newEmployer").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/employer").contentType(MediaType.APPLICATION_JSON).content(updatedEmployer)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated employer in base
		Optional<Employer> employerOptional = this.employerRepository.findByName("updateEmployer");
		assertTrue(employerOptional.isPresent());
		assertThat(employerOptional.get().getName()).isEqualTo("updateEmployer");
	}

	@Test
	public void updateEmployer_with_resourceNotFound() throws Exception {

		// setup
		String updatedEmployer = "{" + "\"name\":\"newEmployerFalse\"," + "\"oldName\":\"employerNotFound\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/employer").contentType(MediaType.APPLICATION_JSON).content(updatedEmployer)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void updateEmployer_with_missingRequiredFields() throws Exception {

		// setup
		String employerToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/employer").contentType(MediaType.APPLICATION_JSON).content(employerToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void z_DroppingDatabase() {
		// Last test run to drop the database for next test classes.
		mongoTemplate.getDb().drop();
	}

}
