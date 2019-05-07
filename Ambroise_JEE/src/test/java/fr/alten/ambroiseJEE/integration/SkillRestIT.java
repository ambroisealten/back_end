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

import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.SkillRepository;
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
public class SkillRestIT {

    private static User userAdmin = new User();
    private static Skill skill = new Skill();

    private static Gson gson;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SkillRepository skillRepository;

    
	@BeforeClass
	public static void beforeTests() {
		TokenIgnore.createDir();
		initAdminUser();
		initSkill();
		initGson();
	}

	private static void initAdminUser() {
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
	}

	private static void initSkill() {
		skill.setName("skill");
	}

	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);

		if (!mongoTemplate.collectionExists("skill")) {
			// Recreate the collection with the unique index "code" -> index beeing dropped
			// when the collection is dropped.
			mongoTemplate.createCollection(Skill.class);
			mongoTemplate.indexOps("skill").ensureIndex(new Index().on("code", Direction.ASC).unique());
		}
	}

	@After
	public void afterEachTest() {
		mongoTemplate.getDb().getCollection("user").drop();
		mongoTemplate.getDb().getCollection("skill").drop();

	}

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

	@Test
	public void createSkill_with_success() throws Exception {

		// setup
		String newSkill = "{" + "\"name\":\"newSkill\"" + "}";

		MvcResult result = this.mockMvc.perform(post("/skill").contentType(MediaType.APPLICATION_JSON).content(newSkill))
				.andReturn();

		// Checking that the ResponseBody contain a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new city in base and its fields's value
		Optional<Skill> skillOptional = this.skillRepository.findByNameIgnoreCase("00001");
		assertTrue(skillOptional.isPresent());
		assertThat(skillOptional.get().getName()).isEqualTo("newSkill");
	}

	@Test
	public void createSkill_with_conflict() throws Exception {

		// setup
		String newSkill = "{" + "\"name\":\"newSkill\"" + "}";

		// Pre-inserting a City with name code as this.city to create a
		// ConflictException
		skillRepository.insert(skill);
		// Checking pre-insertion
		assertTrue(this.skillRepository.findByNameIgnoreCase("00000").isPresent());

		MvcResult result = this.mockMvc.perform(post("/skill").contentType(MediaType.APPLICATION_JSON).content(newSkill))
				.andReturn();

		// Checking that the ResponseBody contain a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.city is in base
		assertThat(this.skillRepository.findAll().size()).isEqualTo(1);
	}

	@Test
	public void createSkill_with_missingRequiredFields() throws Exception {

		// setup
		String newSkill = "{}";

		MvcResult result = this.mockMvc.perform(post("/skill").contentType(MediaType.APPLICATION_JSON).content(newSkill))
				.andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no city in base
		assertThat(this.skillRepository.findAll()).isEmpty();
	}

	@Test
	public void deleteSkill_with_success() throws Exception {

		// setup
		String skillToDelete = "{" + "\"code\":\"00000\"" + "}";
		// Pre-inserting a City with name code as this.city for having a city to delete
		// with success
		skillRepository.insert(skill);
		// Checking pre-insertion
		assertTrue(this.skillRepository.findByNameIgnoreCase("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/skill").contentType(MediaType.APPLICATION_JSON).content(skillToDelete)).andReturn();

		// Checking that the ResponseBody contain a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the skillToDelete had been deactivated
		assertThat(this.skillRepository.findByNameIgnoreCase("00000").get().getName()).startsWith("deactivated");
	}

	@Test
	public void deleteSkill_with_resourceNotFound() throws Exception {

		// setup
		String skillToDelete = "{" + "\"code\":\"00000\"" + "}";
		// Checking if there is not already a city in base with the code : 00000
		assertFalse(this.skillRepository.findByNameIgnoreCase("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/skill").contentType(MediaType.APPLICATION_JSON).content(skillToDelete)).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void deleteSkill_with_missingRequiredFields() throws Exception {

		// setup
		String skillToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/skill").contentType(MediaType.APPLICATION_JSON).content(skillToDelete)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	// TODO : Reecrire la pré-insertion en créant des Jsons de retour et vérifiant
	// bien que
	// le retour est EGAL à ces Jsons de retour.
	@Test
	public void getCities() throws Exception {

		// Pre-insertion of 20 cities for test
		for (int i = 0; i < 20; i++) {
			City cityForGet = new City();
			cityForGet.setName("skill" + i);
			cityForGet.setCode("Code" + i);
			this.skillRepository.insert(cityForGet);
			Optional<Skill> skillOptional = this.skillRepository.findByNameIgnoreCase("Code" + i);
			assertTrue(skillOptional.isPresent());
			assertThat(skillOptional.get().getName()).isEqualTo("skill" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/cities").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (JsonElement jcity : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonCity = jSkill.getAsJsonObject();
			assertThat(jsonSkill.get("code").getAsString()).isEqualTo("Code" + count);
			assertThat(jsonSkill.get("name").getAsString()).isEqualTo("skill" + count);
			count++;
		}
	}

	@Test
	public void updateSkill_with_success() throws Exception {

		// setup
		String updatedCity = "{" + "\"name\":\"newSkill\"," + "\"code\":\"00000\"" + "}";
		// Pre-inserting a city to update
		skillRepository.insert(skill);
		// Checking pre-insertion
		assertTrue(this.skillRepository.findByNameIgnoreCase("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/skill").contentType(MediaType.APPLICATION_JSON).content(updatedCity)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated city in base
		Optional<Skill> skillOptional = this.skillRepository.findByNameIgnoreCase("00000");
		assertTrue(skillOptional.isPresent());
		assertThat(skillOptional.get().getName()).isEqualTo("newSkill");
	}

	@Test
	public void updateSkill_with_resourceNotFound() throws Exception {

		// setup
		String updatedCity = "{" + "\"name\":\"newSkill\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/skill").contentType(MediaType.APPLICATION_JSON).content(updatedCity)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void updateSkill_with_missingRequiredFields() throws Exception {

		// setup
		String skillToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/skill").contentType(MediaType.APPLICATION_JSON).content(skillToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void z_DroppingDatabase() {
		// Last test run to drop the database for next test classes.
		mongoTemplate.getDb().drop();
	}
}
