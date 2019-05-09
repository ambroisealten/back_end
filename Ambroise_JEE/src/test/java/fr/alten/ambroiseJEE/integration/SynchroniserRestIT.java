package fr.alten.ambroiseJEE.integration;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.FileNotFoundException;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import fr.alten.ambroiseJEE.model.beans.BeansTest;
import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;

/**
 *
 * @author Andy Chabalier
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SynchroniserRestIT {

	private static User userAdmin = new User();

	@Autowired
	private MockMvc mockMvc;
	@SuppressWarnings("unused")
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;

	@BeforeClass
	public static void beforeTests() {
		// Creating the dev folder and the dev file to ignore the token during tests.
		TokenIgnore.createDir();
		initAdminUser();
	}

	/**
	 * Setting the {@link User} that will be insered in base for tests
	 * 
	 * @author Andy Chabalier
	 */
	private static void initAdminUser() {
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
	}

	/**
	 * Inserting an admin {@link User} in base for incoming tests Creating the city
	 * collection in base with its unique index "code" if its not existing in base
	 * already. Indeed, when the collection is dropped after each test, all the
	 * indexes are drop also and not recreated. So we need to do it manually and
	 * test index unicity in another test @see {@link BeansTest}.
	 * 
	 * @author Andy Chabalier
	 */
	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);
	}

	/**
	 * Droping collections user and geographics after each test.
	 * 
	 * @author Andy Chabalier
	 */
	@After
	public void afterEachTest() {
		userRepository.deleteAll();

	}

	/**
	 * Deleting the txt file and the dev folder once all of the class's test are
	 * done.
	 * 
	 * @throws FileNotFoundException
	 * @author Andy Chabalier
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
	 * @author Andy Chabalier
	 */
	@Test
	public void synchronize_with_success() throws Exception {

		MvcResult result = this.mockMvc.perform(post("/admin/synchronize/geographics")).andReturn();

		// Checking that the ResponseBody contain a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));
	}
}
