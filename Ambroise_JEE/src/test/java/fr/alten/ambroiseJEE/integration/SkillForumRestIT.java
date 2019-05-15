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

import fr.alten.ambroiseJEE.model.beans.SkillForum;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.SkillForumRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;

/**
 * @author Andy Chabalier
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SkillForumRestIT {

	private static User userAdmin = new User();
	private static SkillForum skillForum = new SkillForum();

	private static Gson gson;

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

	@BeforeClass
	public static void beforeTests() {
		// Creating the dev folder and the dev file to ignore the token during tests.
		TokenIgnore.createDir();
		SkillForumRestIT.initAdminUser();
		SkillForumRestIT.initSkillForum();
		SkillForumRestIT.initGson();
	}

	private static void initAdminUser() {
		SkillForumRestIT.userAdmin.setForname("tempUserAdmin");
		SkillForumRestIT.userAdmin.setMail("tempUserAdmin@mail.com");
		SkillForumRestIT.userAdmin.setName("tempUserAdminName");
	}

	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		SkillForumRestIT.gson = builder.create();
	}

	private static void initSkillForum() {
		SkillForumRestIT.skillForum.setName("skillForum");
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SkillForumRepository skillForumRepository;

	@After
	public void afterEachTest() {
		this.userRepository.deleteAll();
		this.skillForumRepository.deleteAll();
	}

	@Before
	public void beforeEachTest() {
		this.userRepository.insert(SkillForumRestIT.userAdmin);
	}

	@Test
	public void createSkillForum_with_conflict() throws Exception {

		// setup
		final String newSkillForum = "{" + "\"name\":\"skillForum\"" + "}";

		// Pre-inserting a SkillForum with name code as this.skillForum to create a
		// ConflictException
		this.skillForumRepository.insert(SkillForumRestIT.skillForum);
		// Checking pre-insertion
		Assert.assertTrue(this.skillForumRepository.findByName("skillForum").isPresent());

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/forum/skill")
				.contentType(MediaType.APPLICATION_JSON).content(newSkillForum)).andReturn();

		// Checking that the ResponseBody contains a ConflictException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.skillForum is in base
		Assertions.assertThat(this.skillForumRepository.count()).isEqualTo(1);
	}

	@Test
	public void createSkillForum_with_missingRequiredFields() throws Exception {

		// setup
		final String newSkillForum = "{}";

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/forum/skill")
				.contentType(MediaType.APPLICATION_JSON).content(newSkillForum)).andReturn();

		// Checking that the ResponseBody contains a UnprocessableEntityException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no skillForum in base
		Assertions.assertThat(this.skillForumRepository.findAll()).isEmpty();
	}

	@Test
	public void createSkillForum_with_success() throws Exception {

		// setup
		final String newSkillForum = "{" + "\"name\":\"newSkillForum\"" + "}";

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/forum/skill")
				.contentType(MediaType.APPLICATION_JSON).content(newSkillForum)).andReturn();

		// Checking that the ResponseBody contains a CreatedException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new skillForum in base and its fields's value
		final Optional<SkillForum> askillForumOptional = this.skillForumRepository.findByName("newSkillForum");
		Assert.assertTrue(askillForumOptional.isPresent());
	}

	@Test
	public void deleteSkillForum_with_missingRequiredFields() throws Exception {

		// setup
		final String askillForumToDelete = "{}";

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/forum/skill")
				.contentType(MediaType.APPLICATION_JSON).content(askillForumToDelete)).andReturn();

		// Checking that the ResponseBody contains a UnprocessableEntityException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void deleteSkillForum_with_resourceNotFound() throws Exception {

		// setup
		final String askillForumToDelete = "{" + "\"name\":\"skillForum\"" + "}";
		// Checking if there is not already a skillForum in base with the code : 00000
		Assert.assertFalse(this.skillForumRepository.findByName("skillForum").isPresent());

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/forum/skill")
				.contentType(MediaType.APPLICATION_JSON).content(askillForumToDelete)).andReturn();

		// Checking that the ResponseBody contains a ResourceNotFoundException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void deleteSkillForum_with_success() throws Exception {

		// setup
		final String skillForumToDelete = "{" + "\"name\":\"skillForum\"" + "}";
		// Pre-inserting a SkillForum with code as this.skillForum to have a skillForum
		// to
		// delete
		// with success
		this.skillForumRepository.insert(SkillForumRestIT.skillForum);
		// Checking pre-insertion
		final Optional<SkillForum> skillForumOptional = this.skillForumRepository.findByName("skillForum");
		final String skillForumId = skillForumOptional.get().get_id().toHexString();
		Assert.assertTrue(skillForumOptional.isPresent());

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/forum/skill")
				.contentType(MediaType.APPLICATION_JSON).content(skillForumToDelete)).andReturn();

		// Checking that the ResponseBody contains a OkException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the askillForumToDelete had been deactivated
		Assertions.assertThat(this.skillForumRepository.findBy_id(skillForumId).get().getName())
				.startsWith("deactivated");
	}

	@Test
	public void getSkillsForum() throws Exception {

		// Pre-insertion of 20 agencies for test
		for (int i = 0; i < 20; i++) {
			final SkillForum askillForumForGet = new SkillForum();
			askillForumForGet.setName("SkillForum" + i);
			this.skillForumRepository.insert(askillForumForGet);
			final Optional<SkillForum> skillForumOptional = this.skillForumRepository.findByName("SkillForum" + i);
			Assert.assertTrue(skillForumOptional.isPresent());
			Assertions.assertThat(skillForumOptional.get().getName()).isEqualTo("SkillForum" + i);
		}

		final MvcResult result = this.mockMvc
				.perform(MockMvcRequestBuilders.get("/forum/skills").contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		final String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (final JsonElement jSkillForum : SkillForumRestIT.gson.fromJson(jsonResult, JsonArray.class)) {
			final JsonObject jsonSkillForum = jSkillForum.getAsJsonObject();
			Assertions.assertThat(jsonSkillForum.get("name").getAsString()).isEqualTo("SkillForum" + count);
			count++;
		}
	}

	@Test
	public void updateSkillForum_with_missingRequiredFields() throws Exception {

		// setup
		final String askillForumToDelete = "{}";

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put("/forum/skill")
				.contentType(MediaType.APPLICATION_JSON).content(askillForumToDelete)).andReturn();

		// Checking that the ResponseBody contains a UnprocessableEntityException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void updateSkillForum_with_resourceNotFound() throws Exception {

		// setup
		final String updatedSkillForum = "{" + "\"oldName\":\"falseSkillForum\"," + "\"name\":\"newSkillForum\"" + "}";

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put("/forum/skill")
				.contentType(MediaType.APPLICATION_JSON).content(updatedSkillForum)).andReturn();

		// Checking that the ResponseBody contains a ResourceNotFoundException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void updateSkillForum_with_success() throws Exception {

		// setup
		final String updatedSkillForum = "{" + "\"oldName\":\"skillForum\"," + "\"name\":\"newSkillForum\"" + "}";
		// Pre-inserting a skillForum to update
		this.skillForumRepository.insert(SkillForumRestIT.skillForum);
		// Checking pre-insertion
		Assert.assertTrue(this.skillForumRepository.findByName("skillForum").isPresent());

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put("/forum/skill")
				.contentType(MediaType.APPLICATION_JSON).content(updatedSkillForum)).andReturn();

		// Checking that the ResponseBody contains a OkException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated skillForum in base
		final Optional<SkillForum> askillForumOptional = this.skillForumRepository.findByName("newSkillForum");
		Assert.assertTrue(askillForumOptional.isPresent());
	}

	@Test
	public void y_testIndex() {
		// asserting the collection skillForum exist
		Assert.assertTrue(this.mongoTemplate.collectionExists("skillforum"));

		// asserting all unique index are present

		final HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("name", false);

		// getting all indexed field of the collection "skillForum"
		final List<IndexInfo> indexList = this.mongoTemplate.indexOps("skillforum").getIndexInfo();

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
