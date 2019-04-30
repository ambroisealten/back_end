package fr.alten.ambroiseJEE.integration;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import fr.alten.ambroiseJEE.AmbroiseJeeApplication;
import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.CityRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.DirAndFileCreator;

/**
 *
 * @author Kylian Gehier
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AmbroiseJeeApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CityRestIT {

	private static User userAdmin = new User();
	private static City city = new City();

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
		DirAndFileCreator.createDir();
		initAdminUser();
		initCity();
	}

	private static void initAdminUser() {
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
	}

	private static void initCity() {
		city.setNom("city");
		city.setCode("code");
		city.setCodeDepartement("codeDepartement");
		city.setCodePostaux("codePostaux");
		city.setCodeRegion("codeRegion");
	}

	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);
	}

	@After
	public void afterEachTest() {
		mongoTemplate.dropCollection("user");
		mongoTemplate.dropCollection("city");
	}

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		DirAndFileCreator.deleteDir();
	}

	//@Test
	public void createCity_with_success() throws Exception {

		// setup
		String newCity = "{" + "\"nom\":\"city\"," + "\"code\":\"code2\"," + "\"codeRegion\":\"code\","
				+ "\"codeDepartement\":\"code\"," + "\"codesPostaux\":\"code\"" + "}";

		System.out.println("\n\n" + newCity + "\n\n");
		MvcResult result;

		result = this.mockMvc.perform(post("/city").contentType(MediaType.APPLICATION_JSON).content(newCity))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));
	}

	//@Test
	public void createCity_with_conflict() throws Exception {

		// setup
		String newCity = "{" + "\"nom\":\"city\"," + "\"code\":\"code\"," + "\"codeRegion\":\"code\","
				+ "\"codeDepartement\":\"code\"," + "\"codesPostaux\":\"code\"" + "}";
		cityRepository.insert(city); // Pre-inserting a City with name city to create a ConflictException

		MvcResult result = this.mockMvc.perform(post("/city").contentType(MediaType.APPLICATION_JSON).content(newCity))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));
	}

	//@Test
	public void createCity_with_missingRequiredFields() throws Exception {
		
		// setup
		String newCity = "{}";

		MvcResult result = this.mockMvc.perform(post("/city").contentType(MediaType.APPLICATION_JSON).content(newCity))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	
	@Test
	public void deleteCity_with_success() throws Exception {
		// setup
		String cityToDelete = "{"+"\"nom\":\"city\""+"}";
		cityRepository.insert(city);
		
		MvcResult result = this.mockMvc.perform(delete("/city").contentType(MediaType.APPLICATION_JSON).content(cityToDelete))
				.andReturn();
		
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));
	}
	
	@Test
	public void deleteCity_with_resourceNotFound() throws Exception {
		
	}
	
	@Test
	public void deleteCity_with_missingRequiredFields() throws Exception {
		
	}
}
