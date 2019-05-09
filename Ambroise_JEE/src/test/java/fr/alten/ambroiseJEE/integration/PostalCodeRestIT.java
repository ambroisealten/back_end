package fr.alten.ambroiseJEE.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
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

import fr.alten.ambroiseJEE.model.beans.BeansTest;
import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.PostalCodeRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Integration test class for PostalCodeRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PostalCodeRestIT {
	private static User userAdmin = new User();
	private static PostalCode postalCode = new PostalCode();

	private static Gson gson;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PostalCodeRepository postalCodeRepository;

	@BeforeClass
	public static void beforeTests() {
		// Creating the dev folder and the dev file to ignore the token during tests.
		TokenIgnore.createDir();
		initAdminUser();
		initPostalCode();
		initGson();
	}

	/**
	 * Setting the {@link User} that will be insered in base for tests
	 *
	 * @author Kylian Gehier
	 */
	private static void initAdminUser() {
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
	}

	/**
	 * Setting the {@link PostalCode} that will be insered in base for tests
	 *
	 * @author Camille Schnell
	 */
	private static void initPostalCode() {
		postalCode.setName("postalCode");
	}

	/**
	 * Setting the {@link Gson} use for inserting JSON file in request's body
	 *
	 * @author Kylian Gehier
	 */
	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	/**
	 * Inserting an admin {@link User} in base for incoming tests Creating the
	 * postalCode collection in base with its unique index "code" if its not
	 * existing in base already. Indeed, when the collection is dropped after each
	 * test, all the indexes are drop also and not recreated. So we need to do it
	 * manually and test index unipostalCode in another test @see {@link BeansTest}.
	 *
	 * @author Kylian Gehier
	 */
	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);
	}

	/**
	 * Droping both collections user and postalCode after each test.
	 *
	 * @author Kylian Gehier
	 */
	@After
	public void afterEachTest() {
		userRepository.deleteAll();
		postalCodeRepository.deleteAll();
	}

	/**
	 * Deleting the txt file and the dev folder once all of the class's test are
	 * done.
	 *
	 * @throws FileNotFoundException
	 * @author Kylian Gehier
	 */
	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

	/**
	 * @test creating a new {@link PostalCode}
	 * @context The {@link PostalCode} doesn't already exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link CreatedException} and the
	 *           {@link PostalCode} in base has the same field's values than the
	 *           JSON newPostalCode.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_with_success() throws Exception {

		// setup
		String newPostalCode = "{" + "\"name\":\"newPostalCode\"" + "}";

		MvcResult result = this.mockMvc
				.perform(post("/postalCode").contentType(MediaType.APPLICATION_JSON).content(newPostalCode))
				.andReturn();

		// Checking that the ResponseBody contains a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new postalCode in base and its fields's value
		Optional<PostalCode> postalCodeOptional = this.postalCodeRepository.findByName("newPostalCode");
		assertTrue(postalCodeOptional.isPresent());
	}

	/**
	 * @test creating a new {@link PostalCode}
	 * @context The {@link PostalCode} already exist in base. The Json is correctly
	 *          set.
	 * @expected the response contains a {@link ConflictException} and the
	 *           {@link PostalCode} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_with_conflict() throws Exception {

		// setup
		String newPostalCode = "{" + "\"name\":\"postalCode\"" + "}";

		// Pre-inserting a PostalCode with name code as this.postalCode to create a
		// ConflictException
		postalCodeRepository.insert(postalCode);
		// Checking pre-insertion
		assertTrue(this.postalCodeRepository.findByName("postalCode").isPresent());

		MvcResult result = this.mockMvc
				.perform(post("/postalCode").contentType(MediaType.APPLICATION_JSON).content(newPostalCode))
				.andReturn();

		// Checking that the ResponseBody contains a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.postalCode is in base
		assertThat(this.postalCodeRepository.count()).isEqualTo(1);
	}

	/**
	 * @test creating a new {@link PostalCode}
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException} and
	 *           the {@link PostalCode} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_with_missingRequiredFields() throws Exception {

		// setup
		String newPostalCode = "{}";

		MvcResult result = this.mockMvc
				.perform(post("/postalCode").contentType(MediaType.APPLICATION_JSON).content(newPostalCode))
				.andReturn();

		// Checking that the ResponseBody contains a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no postalCode in base
		assertThat(this.postalCodeRepository.findAll()).isEmpty();
	}

	/**
	 * @test deleting a {@link PostalCode} in base.
	 * @context The {@link PostalCode} to delete exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link OkException} and the
	 *           {@link PostalCode}'s name to delete in base has been set to
	 *           "deactivated..."
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_with_success() throws Exception {

		// setup
		String postalCodeToDelete = "{" + "\"name\":\"postalCode\"" + "}";
		// Pre-inserting a PostalCode with code as this.postalCode to have a postalCode
		// to delete
		// with success
		postalCodeRepository.insert(postalCode);
		// Checking pre-insertion
		Optional<PostalCode> postalCodeOptional = this.postalCodeRepository.findByName("postalCode");
		String postalCodeId = postalCodeOptional.get().get_id().toString();
		assertTrue(postalCodeOptional.isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/postalCode").contentType(MediaType.APPLICATION_JSON).content(postalCodeToDelete))
				.andReturn();

		// Checking that the ResponseBody contains a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the postalCodeToDelete had been deactivated
		assertThat(this.postalCodeRepository.findBy_id(postalCodeId).get().getName()).startsWith("deactivated");
	}

	/**
	 * @test deleting a {@link PostalCode} in base.
	 * @context The {@link PostalCode} to delete doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_with_resourceNotFound() throws Exception {

		// setup
		String postalCodeToDelete = "{" + "\"name\":\"postalCode\"" + "}";
		// Checking if there is not already a postalCode in base with the code : 00000
		assertFalse(this.postalCodeRepository.findByName("postalCode").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/postalCode").contentType(MediaType.APPLICATION_JSON).content(postalCodeToDelete))
				.andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test deleting a {@link PostalCode} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_with_missingRequiredFields() throws Exception {

		// setup
		String postalCodeToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/postalCode").contentType(MediaType.APPLICATION_JSON).content(postalCodeToDelete))
				.andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	/**
	 * @test Getting the {@link List} of all {@link PostalCode} present in base.
	 * @context pré-inserting 20 {@link PostalCode}
	 * @expected The {@link List} of {@link PostalCode} contains all of the
	 *           pre-inserted {@link PostalCode}
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void getPostalCodes() throws Exception {

		// Pre-insertion of 20 postalCodes for test
		for (int i = 0; i < 20; i++) {
			PostalCode postalCodeForGet = new PostalCode();
			postalCodeForGet.setName("PostalCode" + i);
			this.postalCodeRepository.insert(postalCodeForGet);
			Optional<PostalCode> postalCodeOptional = this.postalCodeRepository.findByName("PostalCode" + i);
			assertTrue(postalCodeOptional.isPresent());
		}

		MvcResult result = this.mockMvc.perform(get("/postalCodes").contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (JsonElement jpostalCode : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonPostalCode = jpostalCode.getAsJsonObject();
			assertThat(jsonPostalCode.get("name").getAsString()).isEqualTo("PostalCode" + count);
			count++;
		}
	}

	/**
	 * @test updating a {@link PostalCode} in base.
	 * @context The {@link PostalCode} to update exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link OkException} and the
	 *           {@link PostalCode}'s name to update in base has been set with the
	 *           new name.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_with_success() throws Exception {

		// setup
		String updatedPostalCode = "{" + "\"oldName\":\"postalCode\"," + "\"name\":\"newPostalCode\"" + "}";
		// Pre-inserting a postalCode to update
		postalCodeRepository.insert(postalCode);
		// Checking pre-insertion
		assertTrue(this.postalCodeRepository.findByName("postalCode").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/postalCode").contentType(MediaType.APPLICATION_JSON).content(updatedPostalCode))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated postalCode in base
		Optional<PostalCode> postalCodeOptional = this.postalCodeRepository.findByName("newPostalCode");
		assertTrue(postalCodeOptional.isPresent());
	}

	/**
	 * @test updating a {@link PostalCode} in base.
	 * @context The {@link PostalCode} to update doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_with_resourceNotFound() throws Exception {

		// setup
		String updatedPostalCode = "{" + "\"oldName\":\"falsePostalCode\"," + "\"name\":\"newPostalCode\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/postalCode").contentType(MediaType.APPLICATION_JSON).content(updatedPostalCode))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test updating a {@link PostalCode} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_with_missingRequiredFields() throws Exception {

		// setup
		String postalCodeToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/postalCode").contentType(MediaType.APPLICATION_JSON).content(postalCodeToDelete))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void y_testIndex() {
		// asserting the collection postalCode exist
		assertTrue(mongoTemplate.collectionExists("postalCode"));

		// asserting all unique index are present

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("name", false);

		// getting all indexed field of the collection "postalCode"
		List<IndexInfo> indexList = mongoTemplate.indexOps("postalCode").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					// checking the unipostalCode of unique indexed fields - except for _id_ because
					// mongoDB consider his unipostalCode as false
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
			;
		}
	}
}
