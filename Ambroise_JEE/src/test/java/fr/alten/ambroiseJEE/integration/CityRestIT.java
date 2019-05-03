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

import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.CityRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;

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
		TokenIgnore.createDir();
		initAdminUser();
		initCity();
		initGson();
	}

	private static void initAdminUser() {
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
	}

	private static void initCity() {
		city.setNom("city");
		city.setCode("00000");
		city.setCodeDepartement("codeDepartement");
		city.setCodePostaux("codePostaux");
		city.setCodeRegion("codeRegion");
	}

	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);

		if (!mongoTemplate.collectionExists("city")) {
			// Recreate the collection with the unique index "code" -> index beeing dropped
			// when the collection is dropped.
			mongoTemplate.createCollection(City.class);
			mongoTemplate.indexOps("city").ensureIndex(new Index().on("code", Direction.ASC).unique());
		}
	}

	@After
	public void afterEachTest() {
		mongoTemplate.getDb().getCollection("user").drop();
		mongoTemplate.getDb().getCollection("city").drop();

	}

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

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

	@Test
	public void deleteCity_with_missingRequiredFields() throws Exception {

		// setup
		String cityToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/city").contentType(MediaType.APPLICATION_JSON).content(cityToDelete)).andReturn();

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

	@Test
	public void updateCity_with_resourceNotFound() throws Exception {

		// setup
		String updatedCity = "{" + "\"nom\":\"newCity\"," + "\"code\":\"00000\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/city").contentType(MediaType.APPLICATION_JSON).content(updatedCity)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void updateCity_with_missingRequiredFields() throws Exception {

		// setup
		String cityToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/city").contentType(MediaType.APPLICATION_JSON).content(cityToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void z_DroppingDatabase() {
		// Last test run to drop the database for next test classes.
		mongoTemplate.getDb().drop();
	}

}
