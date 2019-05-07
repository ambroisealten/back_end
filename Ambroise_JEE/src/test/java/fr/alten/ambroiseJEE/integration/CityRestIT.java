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
import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.CityRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 *
 * @author Kylian Gehier
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CityRestIT {

	private static User userAdmin = new User();
	private static City city = new City();

	private static Gson gson;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CityRepository cityRepository;

	@BeforeClass
	public static void beforeTests() {
		// Creating the dev folder and the dev file to ignore the token during tests.
		TokenIgnore.createDir();
		initAdminUser();
		initCity();
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
	 * Setting the {@link City} that will be insered in base for tests
	 * 
	 * @author Kylian Gehier
	 */
	private static void initCity() {
		city.setNom("city");
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
	 * Inserting an admin {@link User} in base for incoming tests Creating the city
	 * collection in base with its unique index "code" if its not existing in base
	 * already. Indeed, when the collection is dropped after each test, all the
	 * indexes are drop also and not recreated. So we need to do it manually and
	 * test index unicity in another test @see {@link BeansTest}.
	 * 
	 * @author Kylian Gehier
	 */
	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);
	}

	/**
	 * Droping both collections user and city after each test.
	 * 
	 * @author Kylian Gehier
	 */
	@After
	public void afterEachTest() {
		userRepository.deleteAll();
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
	 * @test creating a new {@link City}
	 * @context The {@link City} doesn't already exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link CreatedException} and the
	 *           {@link City} in base has the same field's values than the JSON
	 *           newCity.
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_success() throws Exception {

		// setup
		String newCity = "{" + "\"nom\":\"newCity\"," + "\"code\":\"00001\"," + "\"codeRegion\":\"000\","
				+ "\"codeDepartement\":\"00\"," + "\"codesPostaux\":\"0000\"" + "}";

		MvcResult result = this.mockMvc.perform(post("/city").contentType(MediaType.APPLICATION_JSON).content(newCity))
				.andReturn();

		// Checking that the ResponseBody contain a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new city in base and its fields's value
		Optional<City> cityOptional = this.cityRepository.findByCode("00001");
		assertTrue(cityOptional.isPresent());
		assertThat(cityOptional.get().getCodeDepartement()).isEqualTo("00");
		assertThat(cityOptional.get().getCodeRegion()).isEqualTo("000");
		assertThat(cityOptional.get().getCodePostaux()).isEqualTo("0000");
		assertThat(cityOptional.get().getNom()).isEqualTo("newCity");
	}

	/**
	 * @test creating a new {@link City}
	 * @context The {@link City} already exist in base. The Json is correctly set.
	 * @expected the response contains a {@link ConflictException} and the
	 *           {@link City} has not been inserted in base.
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_conflict() throws Exception {

		// setup
		String newCity = "{" + "\"nom\":\"newCity\"," + "\"code\":\"00000\"," + "\"codeRegion\":\"000\","
				+ "\"codeDepartement\":\"00\"," + "\"codesPostaux\":\"0000\"" + "}";

		// Pre-inserting a City with name code as this.city to create a
		// ConflictException
		cityRepository.insert(city);
		// Checking pre-insertion
		assertTrue(this.cityRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc.perform(post("/city").contentType(MediaType.APPLICATION_JSON).content(newCity))
				.andReturn();

		// Checking that the ResponseBody contain a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.city is in base
		assertThat(this.cityRepository.findAll().size()).isEqualTo(1);
	}

	/**
	 * @test creating a new {@link City}
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException} and
	 *           the {@link City} has not been inserted in base.
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_missingRequiredFields() throws Exception {

		// setup
		String newCity = "{}";

		MvcResult result = this.mockMvc.perform(post("/city").contentType(MediaType.APPLICATION_JSON).content(newCity))
				.andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no city in base
		assertThat(this.cityRepository.findAll()).isEmpty();
	}

	/**
	 * @test deleting a {@link City} in base.
	 * @context The {@link City} to delete exist in base. The Json is correctly set.
	 * @expected the response contains a {@link OkException} and the {@link City}'s
	 *           name to delete in base has been set to "deactivated..."
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_success() throws Exception {

		// setup
		String cityToDelete = "{" + "\"code\":\"00000\"" + "}";
		// Pre-inserting a City with name code as this.city for having a city to delete
		// with success
		cityRepository.insert(city);
		// Checking pre-insertion
		assertTrue(this.cityRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/city").contentType(MediaType.APPLICATION_JSON).content(cityToDelete)).andReturn();

		// Checking that the ResponseBody contain a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the cityToDelete had been deactivated
		assertThat(this.cityRepository.findByCode("00000").get().getNom()).startsWith("deactivated");
	}

	/**
	 * @test deleting a {@link City} in base.
	 * @context The {@link City} to delete doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_resourceNotFound() throws Exception {

		// setup
		String cityToDelete = "{" + "\"code\":\"00000\"" + "}";
		// Checking if there is not already a city in base with the code : 00000
		assertFalse(this.cityRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/city").contentType(MediaType.APPLICATION_JSON).content(cityToDelete)).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test deleting a {@link City} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_missingRequiredFields() throws Exception {

		// setup
		String cityToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/city").contentType(MediaType.APPLICATION_JSON).content(cityToDelete)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	/**
	 * @test Getting the {@link List} of all {@link City} present in base.
	 * @context pré-inserting 20 {@link City}
	 * @expected The {@link List} of {@link City} contains all of the pre-inserted
	 *           {@link City}
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void getCities() throws Exception {

		// Pre-insertion of 20 cities for test
		for (int i = 0; i < 20; i++) {
			City cityForGet = new City();
			cityForGet.setNom("City" + i);
			cityForGet.setCode("Code" + i);
			this.cityRepository.insert(cityForGet);
			Optional<City> cityOptional = this.cityRepository.findByCode("Code" + i);
			assertTrue(cityOptional.isPresent());
			assertThat(cityOptional.get().getNom()).isEqualTo("City" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/cities").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (JsonElement jcity : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonCity = jcity.getAsJsonObject();
			assertThat(jsonCity.get("code").getAsString()).isEqualTo("Code" + count);
			assertThat(jsonCity.get("nom").getAsString()).isEqualTo("City" + count);
			count++;
		}
	}

	/**
	 * @test updating a {@link City} in base.
	 * @context The {@link City} to update exist in base. The Json is correctly set.
	 * @expected the response contains a {@link OkException} and the {@link City}'s
	 *           name to update in base has been set with the new name.
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_success() throws Exception {

		// setup
		String updatedCity = "{" + "\"nom\":\"newCity\"," + "\"code\":\"00000\"" + "}";
		// Pre-inserting a city to update
		cityRepository.insert(city);
		// Checking pre-insertion
		assertTrue(this.cityRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/city").contentType(MediaType.APPLICATION_JSON).content(updatedCity)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated city in base
		Optional<City> cityOptional = this.cityRepository.findByCode("00000");
		assertTrue(cityOptional.isPresent());
		assertThat(cityOptional.get().getNom()).isEqualTo("newCity");
	}

	/**
	 * @test updating a {@link City} in base.
	 * @context The {@link City} to update doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_resourceNotFound() throws Exception {

		// setup
		String updatedCity = "{" + "\"nom\":\"newCity\"," + "\"code\":\"00000\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/city").contentType(MediaType.APPLICATION_JSON).content(updatedCity)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test updating a {@link City} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_missingRequiredFields() throws Exception {

		// setup
		String cityToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/city").contentType(MediaType.APPLICATION_JSON).content(cityToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void y_testIndex() {
		// asserting the collection city exist
		assertTrue(mongoTemplate.collectionExists("city"));

		// asserting all unique index are present

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("code", false);

		// getting all indexed field of the collection "city"
		List<IndexInfo> indexList = mongoTemplate.indexOps("city").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					// checking the unicity of unique indexed fields - except for _id_ because
					// mongoDB consider his unicity as false
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

	/**
	 * This is no a Test ! This method has been set in a {@link Test} because its
	 * not possible to drop the database in {@link AfterClass}. Indeed,
	 * {@link Autowired} doesn't work with static access required by
	 * {@link AfterClass}.
	 * 
	 * @author Kylian Gehier
	 */
	@Test
	public void z_DroppingDatabase() {
		// Last test run to drop the database for next test classes.
		mongoTemplate.getDb().drop();
	}

}
