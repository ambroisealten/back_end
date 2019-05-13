/**
 *
 */
package fr.alten.ambroiseJEE.integration;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.security.JWTokenUtility;
import fr.alten.ambroiseJEE.security.Token;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.TokenIgnore;

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
public class LoginRestIT {

	private static User userAdmin = new User();

	private static Gson gson;

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

	@BeforeClass
	public static void beforeTests() {
		// Creating the dev folder and the dev file to ignore the token during tests.
		TokenIgnore.createDir();
		LoginRestIT.initAdminUser();
		LoginRestIT.initGson();
	}

	/**
	 * Setting the {@link User} that will be insered in base for tests
	 *
	 * @author Andy Chabalier
	 */
	private static void initAdminUser() {
		LoginRestIT.userAdmin.setForname("tempUserAdmin");
		LoginRestIT.userAdmin.setRole(UserRole.MANAGER_ADMIN);
		LoginRestIT.userAdmin.setMail("tempUserAdmin@mail.com");
		LoginRestIT.userAdmin.setName("tempUserAdminName");
		LoginRestIT.userAdmin.setPswd("pass");
	}

	/**
	 * Setting the {@link Gson} use for inserting JSON file in request's body
	 *
	 * @author Andy Chabalier
	 */
	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		LoginRestIT.gson = builder.create();
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Droping both collections user and file after each test.
	 *
	 * @author Andy Chabalier
	 */
	@After
	public void afterEachTest() {
		this.userRepository.deleteAll();
	}

	/**
	 *
	 * @author Andy Chabalier
	 */
	@Before
	public void beforeEachTest() {
		this.userRepository.insert(LoginRestIT.userAdmin);
	}

	@Test
	public void login_sucess() throws Exception {
		final String json = "{" + "\"mail\":\"" + LoginRestIT.userAdmin.getMail() + "\"," + "\"pswd\":\""
				+ LoginRestIT.userAdmin.getPswd() + "\"" + "}";

		// assert that we get a 200 (ok) http status
		final MvcResult result = this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
		final Token token = LoginRestIT.gson.fromJson(result.getResponse().getContentAsString(), Token.class);

		final String[] tokenInfo = JWTokenUtility.validate(token.getToken()).split("\\|");
		final String subject = tokenInfo[0];
		final UserRole role = UserRole.valueOf(tokenInfo[1]);
		// assert that the mail is the one containing in token
		Assert.assertTrue(subject.equals(LoginRestIT.userAdmin.getMail()));
		// assert that the role is the one containing in token
		Assert.assertTrue(role.equals(LoginRestIT.userAdmin.getRole()));
	}

	@Test
	public void login_with_forbiddenException() throws Exception {
		final String json = "{" + "\"mail\":\"" + "unexistingMail@mail.com" + "\"," + "\"pswd\":\"" + "pass" + "\""
				+ "}";

		// assert that we get a 403 (forbidden) http status
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(MockMvcResultMatchers.status().is(403)).andReturn();
	}

	@Test
	public void login_with_nullPointerException_mail() throws Exception {
		final String json = "{" + "\"pswd\":\"" + "pass" + "\"" + "}";

		// assert that we get a 422 (Unprocessable) http status
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(MockMvcResultMatchers.status().is(422)).andReturn();
	}

	@Test
	public void login_with_nullPointerException_nothing() throws Exception {
		final String json = "{}";

		// assert that we get a 422 (Unprocessable) http status
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(MockMvcResultMatchers.status().is(422)).andReturn();
	}

	@Test
	public void login_with_nullPointerException_pswd() throws Exception {
		final String json = "{" + "\"mail\":\"" + "unexistingMail@mail.com" + "\"" + "}";

		// assert that we get a 422 (Unprocessable) http status
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(MockMvcResultMatchers.status().is(422)).andReturn();
	}

	@Test
	public void y_testIndex() {
		// asserting the collection file exist
		Assert.assertTrue(this.mongoTemplate.collectionExists("user"));

		// asserting all unique index are present

		final HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);

		// getting all indexed field of the collection "file"
		final List<IndexInfo> indexList = this.mongoTemplate.indexOps("user").getIndexInfo();

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
