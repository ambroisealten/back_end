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

import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.AgencyRepository;
import fr.alten.ambroiseJEE.model.dao.CityRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Integration test class for AgencyRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AgencyRestIT {

	private static User userAdmin = new User();
	private static Agency agency = new Agency();
	private static City city = new City();

	private static Gson gson;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AgencyRepository agencyRepository;
	@Autowired
	private CityRepository cityRepository;

	@BeforeClass
	public static void beforeTests() {
		// Creating the dev folder and the dev file to ignore the token during tests.
		TokenIgnore.createDir();
		initAdminUser();
		initAgencyAndCity();
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
	 * Setting the {@link Agency} and {@link City} that will be inserted in base for
	 * tests
	 *
	 * @author Camille Schnell
	 */
	private static void initAgencyAndCity() {
		agency.setName("agency");
		agency.setPlace("Strasbourg");
		agency.setPlaceType("city");

		city.setNom("Strasbourg");
		city.setCode("00000");
		city.setCodeDepartement("codeDepartement");
		city.setCodePostaux("codePostaux");
		city.setCodeRegion("codeRegion");
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
	 * agency collection in base with its unique index "code" if its not existing in
	 * base already. Indeed, when the collection is dropped after each test, all the
	 * indexes are drop also and not recreated. So we need to do it manually and
	 * test index uniagency in another test @see {@link BeansTest}.
	 *
	 * @author Kylian Gehier
	 */
	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);
	}

	/**
	 * Droping 3 collections user, agency and city after each test.
	 *
	 * @author Camille Schnell
	 */
	@After
	public void afterEachTest() {
		userRepository.deleteAll();
		agencyRepository.deleteAll();
		cityRepository.deleteAll();
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
	 * @test creating a new {@link Agency}
	 * @context The {@link Agency} doesn't already exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link CreatedException} and the
	 *           {@link Agency} in base has the same field's values than the JSON
	 *           newAgency.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_with_success() throws Exception {

		// setup
		String newAgency = "{" + "\"name\":\"newAgency\"," + "\"place\":\"Strasbourg\"," + "\"placeType\":\"city\""
				+ "}";

		// Pre-inserting the City corresponding to this.agency's place
		cityRepository.insert(city);

		MvcResult result = this.mockMvc
				.perform(post("/agency").contentType(MediaType.APPLICATION_JSON).content(newAgency)).andReturn();

		// Checking that the ResponseBody contains a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new agency in base and its fields's value
		Optional<Agency> agencyOptional = this.agencyRepository.findByName("newAgency");
		assertTrue(agencyOptional.isPresent());
	}

	/**
	 * @test creating a new {@link Agency}
	 * @context The {@link Agency} already exist in base. The Json is correctly set.
	 * @expected the response contains a {@link ConflictException} and the
	 *           {@link Agency} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_with_conflict() throws Exception {

		// setup
		String newAgency = "{" + "\"name\":\"agency\"," + "\"place\":\"Strasbourg\"," + "\"placeType\":\"city\"" + "}";

		// Pre-inserting the City corresponding to this.agency's place
		cityRepository.insert(city);
		// Pre-inserting a Agency with name code as this.agency to create a
		// ConflictException
		agencyRepository.insert(agency);
		// Checking pre-insertion
		assertTrue(this.agencyRepository.findByName("agency").isPresent());

		MvcResult result = this.mockMvc
				.perform(post("/agency").contentType(MediaType.APPLICATION_JSON).content(newAgency)).andReturn();

		// Checking that the ResponseBody contains a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.agency is in base
		assertThat(this.agencyRepository.count()).isEqualTo(1);
	}

	/**
	 * @test creating a new {@link Agency}
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException} and
	 *           the {@link Agency} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_with_missingRequiredFields() throws Exception {

		// setup
		String newAgency = "{}";

		MvcResult result = this.mockMvc
				.perform(post("/agency").contentType(MediaType.APPLICATION_JSON).content(newAgency)).andReturn();

		// Checking that the ResponseBody contains a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no agency in base
		assertThat(this.agencyRepository.findAll()).isEmpty();
	}

	/**
	 * @test creating a new {@link Agency}
	 * @context The agency's {@link City} doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException} and the
	 *           {@link Agency} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_with_cityNotExisting() throws Exception {

		// setup
		String newAgency = "{" + "\"name\":\"agency\"," + "\"place\":\"Strasbourg\"," + "\"placeType\":\"city\"" + "}";

		MvcResult result = this.mockMvc
				.perform(post("/agency").contentType(MediaType.APPLICATION_JSON).content(newAgency)).andReturn();

		// Checking that the ResponseBody contains a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));

		// Checking there is no agency in base
		assertThat(this.agencyRepository.findAll()).isEmpty();
	}

	/**
	 * @test deleting a {@link Agency} in base.
	 * @context The {@link Agency} to delete exist in base. The Json is correctly
	 *          set.
	 * @expected the response contains a {@link OkException} and the
	 *           {@link Agency}'s name to delete in base has been set to
	 *           "deactivated..."
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_with_success() throws Exception {

		// setup
		String agencyToDelete = "{" + "\"name\":\"agency\"" + "}";
		// Pre-inserting a Agency with code as this.agency to have a agency to delete
		// with success
		agencyRepository.insert(agency);
		// Checking pre-insertion
		Optional<Agency> agencyOptional = this.agencyRepository.findByName("agency");
		String agencyId = agencyOptional.get().get_id().toString();
		assertTrue(agencyOptional.isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/agency").contentType(MediaType.APPLICATION_JSON).content(agencyToDelete)).andReturn();

		// Checking that the ResponseBody contains a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the agencyToDelete had been deactivated
		assertThat(this.agencyRepository.findBy_id(agencyId).get().getName()).startsWith("deactivated");
	}

	/**
	 * @test deleting a {@link Agency} in base.
	 * @context The {@link Agency} to delete doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_with_resourceNotFound() throws Exception {

		// setup
		String agencyToDelete = "{" + "\"name\":\"agency\"" + "}";
		// Checking if there is not already a agency in base with the code : 00000
		assertFalse(this.agencyRepository.findByName("agency").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/agency").contentType(MediaType.APPLICATION_JSON).content(agencyToDelete)).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test deleting a {@link Agency} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_with_missingRequiredFields() throws Exception {

		// setup
		String agencyToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/agency").contentType(MediaType.APPLICATION_JSON).content(agencyToDelete)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	/**
	 * @test Getting the {@link List} of all {@link Agency} present in base.
	 * @context pré-inserting 20 {@link Agency}
	 * @expected The {@link List} of {@link Agency} contains all of the pre-inserted
	 *           {@link Agency}
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void getAgencies() throws Exception {

		// Pre-inserting the City corresponding to agencies' place
		cityRepository.insert(city);

		// Pre-insertion of 20 agencies for test
		for (int i = 0; i < 20; i++) {
			Agency agencyForGet = new Agency();
			agencyForGet.setName("Agency" + i);
			agencyForGet.setPlace("Strasbourg");
			agencyForGet.setPlaceType("city");
			this.agencyRepository.insert(agencyForGet);
			Optional<Agency> agencyOptional = this.agencyRepository.findByName("Agency" + i);
			assertTrue(agencyOptional.isPresent());
			assertThat(agencyOptional.get().getName()).isEqualTo("Agency" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/agencies").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (JsonElement jagency : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonAgency = jagency.getAsJsonObject();
			assertThat(jsonAgency.get("name").getAsString()).isEqualTo("Agency" + count);
			assertThat(jsonAgency.get("place").getAsString()).isEqualTo("Strasbourg");
			assertThat(jsonAgency.get("placeType").getAsString()).isEqualTo("city");
			count++;
		}
	}

	/**
	 * @test updating a {@link Agency} in base.
	 * @context The {@link Agency} to update exist in base. The Json is correctly
	 *          set.
	 * @expected the response contains a {@link OkException} and the
	 *           {@link Agency}'s name to update in base has been set with the new
	 *           name.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_with_success() throws Exception {

		// setup
		String updatedAgency = "{" + "\"oldName\":\"agency\"," + "\"name\":\"newAgency\"," + "\"place\":\"Strasbourg\"," + "\"placeType\":\"city\"" + "}";
		// Pre-inserting the City corresponding to agency's place
		cityRepository.insert(city);
		// Pre-inserting a agency to update
		agencyRepository.insert(agency);
		// Checking pre-insertion
		assertTrue(this.agencyRepository.findByName("agency").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/agency").contentType(MediaType.APPLICATION_JSON).content(updatedAgency)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated agency in base
		Optional<Agency> agencyOptional = this.agencyRepository.findByName("newAgency");
		assertTrue(agencyOptional.isPresent());
	}

	/**
	 * @test updating a {@link Agency} in base.
	 * @context The {@link Agency} to update doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_with_resourceNotFound() throws Exception {

		// setup
		String updatedAgency = "{" + "\"oldName\":\"falseAgency\"," + "\"name\":\"newAgency\"," + "\"place\":\"Strasbourg\"," + "\"placeType\":\"city\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/agency").contentType(MediaType.APPLICATION_JSON).content(updatedAgency)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test updating a {@link Agency} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_with_missingRequiredFields() throws Exception {

		// setup
		String agencyToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/agency").contentType(MediaType.APPLICATION_JSON).content(agencyToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void y_testIndex() {
		// asserting the collection agency exist
		assertTrue(mongoTemplate.collectionExists("agency"));

		// asserting all unique index are present

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("name", false);

		// getting all indexed field of the collection "agency"
		List<IndexInfo> indexList = mongoTemplate.indexOps("agency").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					// checking the uniagency of unique indexed fields - except for _id_ because
					// mongoDB consider his uniagency as false
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
