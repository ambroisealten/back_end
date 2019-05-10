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
		skill.setName("newSkill");
	}

	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);

		if (!mongoTemplate.collectionExists("skill")) {
			// Recreate the collection with the unique index "name" -> index beeing dropped
			// when the collection is dropped.
			mongoTemplate.createCollection(Skill.class);
			mongoTemplate.indexOps("skill").ensureIndex(new Index().on("name", Direction.ASC).unique());
		}
	}

	@After
	public void afterEachTest() {
		userRepository.deleteAll();
		skillRepository.deleteAll();

	}

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

	@Test
	public void createSkill_with_success() throws Exception {

		// setup
		String newSkill = "{" + "\"mail\":\"\"," + "\"name\":\"newSkill\"" + "}";

		MvcResult result = this.mockMvc
				.perform(post("/skill").contentType(MediaType.APPLICATION_JSON).content(newSkill)).andReturn();

		// Checking that the ResponseBody contain a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new skill in base and its fields's value
		Optional<Skill> skillOptional = this.skillRepository.findByNameIgnoreCase("newSkill");
		assertTrue(skillOptional.isPresent());
		assertThat(skillOptional.get().getName()).isEqualTo("newSkill");
	}

	@Test
	public void createSkill_with_conflict() throws Exception {

		// setup
		String newSkill = "{" + "\"mail\":\"\"," + "\"name\":\"newSkill\"" + "}";

		// Pre-inserting a Skill with name name as this.skill to create a
		// ConflictException
		skillRepository.insert(skill);
		// Checking pre-insertion
		assertTrue(this.skillRepository.findByNameIgnoreCase("newSkill").isPresent());

		MvcResult result = this.mockMvc
				.perform(post("/skill").contentType(MediaType.APPLICATION_JSON).content(newSkill)).andReturn();

		// Checking that the ResponseBody contain a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.skill is in base
		assertThat(this.skillRepository.count()).isEqualTo(1);
	}

	@Test
	public void createSkill_with_missingRequiredFields() throws Exception {

		// setup
		String newSkill = "{}";

		MvcResult result = this.mockMvc
				.perform(post("/skill").contentType(MediaType.APPLICATION_JSON).content(newSkill)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no skill in base
		assertThat(this.skillRepository.findAll()).isEmpty();
	}

	@Test
	public void deleteSkill_with_success() throws Exception {

		// setup
		String skillToDelete = "{" + "\"mail\":\"\"," + "\"name\":\"newSkill\"" + "}";
		// Pre-inserting a Skill with name name as this.skill for having a skill to delete
		// delete
		// with success
		skillRepository.insert(skill);
		// Checking pre-insertion
		Optional<Skill> skillOptional = this.skillRepository.findByNameIgnoreCase("newSkill");
		String skillId = skillOptional.get().get_id().toString();
		assertTrue(skillOptional.isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/skill").contentType(MediaType.APPLICATION_JSON).content(skillToDelete)).andReturn();

		// Checking that the ResponseBody contain a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the skillToDelete had been deactivated
		assertThat(this.skillRepository.findBy_id(skillId).get().getName()).startsWith("deactivated");
	}

	@Test
	public void deleteSkill_with_resourceNotFound() throws Exception {

		// setup
		String skillToDelete = "{" + "\"mail\":\"\"," + "\"name\":\"newSkill\"" + "}";
		// Checking if there is not already a skill in base with the name : newSkill
		assertFalse(this.skillRepository.findByNameIgnoreCase("newSkill").isPresent());

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
	public void getSkills() throws Exception {

		// Pre-insertion of 20 skills for test
		for (int i = 0; i < 20; i++) {
			Skill skillForGet = new Skill();
			skillForGet.setName("skill" + i);
			this.skillRepository.insert(skillForGet);
			Optional<Skill> skillOptional = this.skillRepository.findByNameIgnoreCase("skill" + i);
			assertTrue(skillOptional.isPresent());
			assertThat(skillOptional.get().getName()).isEqualTo("skill" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/skills").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same skills as inserted before
		int count = 0;
		for (JsonElement jskill : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonSkill = jskill.getAsJsonObject();
			assertThat(jsonSkill.get("name").getAsString()).isEqualTo("skill" + count);
			count++;
		}
	}

	@Test
	public void updateSkill_with_success() throws Exception {

		// setup
		String updatedSkill = "{" + "\"mail\":\"\"," + "\"name\":\"updateSkill\"," +  "\"oldName\":\"newSkill\"" + "}";
		// Pre-inserting a skill to update
		skillRepository.insert(skill);
		// Checking pre-insertion
		assertTrue(this.skillRepository.findByNameIgnoreCase("newSkill").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/skill").contentType(MediaType.APPLICATION_JSON).content(updatedSkill)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated skill in base
		Optional<Skill> skillOptional = this.skillRepository.findByNameIgnoreCase("updateSkill");
		assertTrue(skillOptional.isPresent());
		assertThat(skillOptional.get().getName()).isEqualTo("updateSkill");
	}

	@Test
	public void updateSkill_with_resourceNotFound() throws Exception {

		// setup
		String updatedSkill = "{" + "\"mail\":\"\"," + "\"name\":\"newSkillFalse\"," +  "\"oldName\":\"skillNotFound\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/skill").contentType(MediaType.APPLICATION_JSON).content(updatedSkill)).andReturn();

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

	//@Test
//	public void z_DroppingDatabase() {
//		// Last test run to drop the database for next test classes.
//		mongoTemplate.getDb().drop();
//	}
}
