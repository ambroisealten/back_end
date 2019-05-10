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

import fr.alten.ambroiseJEE.model.beans.Departement;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.DepartementRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Integration test class for DepartementRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DepartementRestIT {

	private static User userAdmin = new User();
	private static Departement departement = new Departement();

	private static Gson gson;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DepartementRepository departementRepository;

	@BeforeClass
	public static void beforeTests() {
		// Creating the dev folder and the dev file to ignore the token during tests.
		TokenIgnore.createDir();
		initAdminUser();
		initDepartement();
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
	 * Setting the {@link Departement} that will be insered in base for tests
	 *
	 * @author Camille Schnell
	 */
	private static void initDepartement() {
		departement.setNom("departement");
		departement.setCode("00000");
		departement.setCodeRegion("00001");
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
	 * departement collection in base with its unique index "code" if its not existing in
	 * base already. Indeed, when the collection is dropped after each test, all the
	 * indexes are drop also and not recreated. So we need to do it manually and
	 * test index unidepartement in another test @see {@link BeansTest}.
	 *
	 * @author Kylian Gehier
	 */
	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);
	}

	/**
	 * Droping both collections user and departement after each test.
	 *
	 * @author Kylian Gehier
	 */
	@After
	public void afterEachTest() {
		userRepository.deleteAll();
		departementRepository.deleteAll();
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
	 * @test creating a new {@link Departement}
	 * @context The {@link Departement} doesn't already exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link CreatedException} and the
	 *           {@link Departement} in base has the same field's values than the JSON
	 *           newDepartement.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_with_success() throws Exception {

		// setup
		String newDepartement = "{" + "\"nom\":\"newDepartement\"," + "\"code\":\"00001\"," + "\"codeRegion\":\"00001\"" + "}";

		MvcResult result = this.mockMvc
				.perform(post("/departement").contentType(MediaType.APPLICATION_JSON).content(newDepartement)).andReturn();

		// Checking that the ResponseBody contains a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new departement in base and its fields's value
		Optional<Departement> departementOptional = this.departementRepository.findByCode("00001");
		assertTrue(departementOptional.isPresent());
		assertThat(departementOptional.get().getNom()).isEqualTo("newDepartement");
	}

	/**
	 * @test creating a new {@link Departement}
	 * @context The {@link Departement} already exist in base. The Json is correctly set.
	 * @expected the response contains a {@link ConflictException} and the
	 *           {@link Departement} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_with_conflict() throws Exception {

		// setup
		String newDepartement = "{" + "\"nom\":\"newDepartement\"," + "\"code\":\"00000\"," + "\"codeRegion\":\"00001\"" + "}";

		// Pre-inserting a Departement with name code as this.departement to create a
		// ConflictException
		departementRepository.insert(departement);
		// Checking pre-insertion
		assertTrue(this.departementRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(post("/departement").contentType(MediaType.APPLICATION_JSON).content(newDepartement)).andReturn();

		// Checking that the ResponseBody contains a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.departement is in base
		assertThat(this.departementRepository.count()).isEqualTo(1);
	}

	/**
	 * @test creating a new {@link Departement}
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException} and
	 *           the {@link Departement} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_with_missingRequiredFields() throws Exception {

		// setup
		String newDepartement = "{}";

		MvcResult result = this.mockMvc
				.perform(post("/departement").contentType(MediaType.APPLICATION_JSON).content(newDepartement)).andReturn();

		// Checking that the ResponseBody contains a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no departement in base
		assertThat(this.departementRepository.findAll()).isEmpty();
	}

	/**
	 * @test deleting a {@link Departement} in base.
	 * @context The {@link Departement} to delete exist in base. The Json is correctly
	 *          set.
	 * @expected the response contains a {@link OkException} and the
	 *           {@link Departement}'s name to delete in base has been set to
	 *           "deactivated..."
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_with_success() throws Exception {

		// setup
		String departementToDelete = "{" + "\"code\":\"00000\"" + "}";
		// Pre-inserting a Departement with code as this.departement to have a departement to delete
		// with success
		departementRepository.insert(departement);
		// Checking pre-insertion
		assertTrue(this.departementRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/departement").contentType(MediaType.APPLICATION_JSON).content(departementToDelete)).andReturn();

		// Checking that the ResponseBody contains a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the departementToDelete had been deactivated
		assertThat(this.departementRepository.findByCode("00000").get().getNom()).startsWith("deactivated");
	}

	/**
	 * @test deleting a {@link Departement} in base.
	 * @context The {@link Departement} to delete doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_with_resourceNotFound() throws Exception {

		// setup
		String departementToDelete = "{" + "\"code\":\"00000\"" + "}";
		// Checking if there is not already a departement in base with the code : 00000
		assertFalse(this.departementRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/departement").contentType(MediaType.APPLICATION_JSON).content(departementToDelete)).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test deleting a {@link Departement} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_with_missingRequiredFields() throws Exception {

		// setup
		String departementToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/departement").contentType(MediaType.APPLICATION_JSON).content(departementToDelete)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	/**
	 * @test Getting the {@link List} of all {@link Departement} present in base.
	 * @context pré-inserting 20 {@link Departement}
	 * @expected The {@link List} of {@link Departement} contains all of the pre-inserted
	 *           {@link Departement}
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void getDepartements() throws Exception {

		// Pre-insertion of 20 departements for test
		for (int i = 0; i < 20; i++) {
			Departement departementForGet = new Departement();
			departementForGet.setNom("Departement" + i);
			departementForGet.setCode("Code" + i);
			departementForGet.setCodeRegion("CodeRegion" + i);
			this.departementRepository.insert(departementForGet);
			Optional<Departement> departementOptional = this.departementRepository.findByCode("Code" + i);
			assertTrue(departementOptional.isPresent());
			assertThat(departementOptional.get().getNom()).isEqualTo("Departement" + i);
			assertThat(departementOptional.get().getCodeRegion()).isEqualTo("CodeRegion" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/departements").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (JsonElement jdepartement : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonDepartement = jdepartement.getAsJsonObject();
			assertThat(jsonDepartement.get("code").getAsString()).isEqualTo("Code" + count);
			assertThat(jsonDepartement.get("nom").getAsString()).isEqualTo("Departement" + count);
			assertThat(jsonDepartement.get("codeRegion").getAsString()).isEqualTo("CodeRegion" + count);
			count++;
		}
	}

	/**
	 * @test updating a {@link Departement} in base.
	 * @context The {@link Departement} to update exist in base. The Json is correctly
	 *          set.
	 * @expected the response contains a {@link OkException} and the
	 *           {@link Departement}'s name to update in base has been set with the new
	 *           name.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_with_success() throws Exception {

		// setup
		String updatedDepartement = "{" + "\"nom\":\"newDepartement2\"," + "\"code\":\"00000\"," + "\"codeRegion\":\"00001\"" + "}";
		// Pre-inserting a departement to update
		departementRepository.insert(departement);
		// Checking pre-insertion
		assertTrue(this.departementRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/departement").contentType(MediaType.APPLICATION_JSON).content(updatedDepartement)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated departement in base
		Optional<Departement> departementOptional = this.departementRepository.findByCode("00000");
		assertTrue(departementOptional.isPresent());
		assertThat(departementOptional.get().getNom()).isEqualTo("newDepartement2");
	}

	/**
	 * @test updating a {@link Departement} in base.
	 * @context The {@link Departement} to update doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_with_resourceNotFound() throws Exception {

		// setup
		String updatedDepartement = "{" + "\"nom\":\"newDepartement\"," + "\"code\":\"00000\"," + "\"codeRegion\":\"00001\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/departement").contentType(MediaType.APPLICATION_JSON).content(updatedDepartement)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test updating a {@link Departement} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_with_missingRequiredFields() throws Exception {

		// setup
		String departementToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/departement").contentType(MediaType.APPLICATION_JSON).content(departementToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void y_testIndex() {
		// asserting the collection departement exist
		assertTrue(mongoTemplate.collectionExists("departement"));

		// asserting all unique index are present

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("code", false);

		// getting all indexed field of the collection "departement"
		List<IndexInfo> indexList = mongoTemplate.indexOps("departement").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					// checking the unidepartement of unique indexed fields - except for _id_ because
					// mongoDB consider his unidepartement as false
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
