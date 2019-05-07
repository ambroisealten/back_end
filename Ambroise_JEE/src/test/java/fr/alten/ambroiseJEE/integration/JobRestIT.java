package fr.alten.ambroiseJEE.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.model.beans.BeansTest;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.JobRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;

/**
 * Integration tests class for JobRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobRestIT {

	private static User userAdmin = new User();
	private static Job job = new Job();

	private static Gson gson;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JobRepository jobRepository;

	@BeforeClass
	public static void beforeTests() {
		// Creating the dev folder and the dev file to ignore the token during tests.
		TokenIgnore.createDir();
		initAdminUser();
		initJob();
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
	 * Setting the {@link Job} that will be inserted in base for tests
	 *
	 * @author Camille Schnell
	 */
	private static void initJob() {
		job.setTitle("job");
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
	 * Inserting an admin {@link User} in base for incoming tests Creating the job
	 * collection in base with its unique index "code" if its not existing in base
	 * already. Indeed, when the collection is dropped after each test, all the
	 * indexes are drop also and not recreated. So we need to do it manually and
	 * test index unijob in another test @see {@link BeansTest}.
	 *
	 * @author Kylian Gehier
	 */
	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);
	}

	/**
	 * Droping both collections user and job after each test.
	 *
	 * @author Kylian Gehier
	 */
	@After
	public void afterEachTest() {
		userRepository.deleteAll();
		jobRepository.deleteAll();
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

	@Test
	public void createJob_with_success() throws Exception {

		// setup
		String newJob = "{" + "\"title\":\"newJob\"" + "}";

		MvcResult result = this.mockMvc.perform(post("/job").contentType(MediaType.APPLICATION_JSON).content(newJob))
				.andReturn();

		// Checking that the ResponseBody contain a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new job in base and its fields's value
		Optional<Job> jobOptional = this.jobRepository.findByTitle("newJob");
		assertTrue(jobOptional.isPresent());
		assertThat(jobOptional.get().getTitle()).isEqualTo("newJob");
	}

	@Test
	public void createJob_with_conflict() throws Exception {

		// setup
		String newJob = "{" + "\"title\":\"job\"" + "}";

		// Pre-inserting a Job with title as this.job to create a
		// ConflictException
		jobRepository.insert(job);
		// Checking pre-insertion
		assertTrue(this.jobRepository.findByTitle("job").isPresent());

		MvcResult result = this.mockMvc.perform(post("/job").contentType(MediaType.APPLICATION_JSON).content(newJob))
				.andReturn();

		// Checking that the ResponseBody contain a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.job is in base
		assertThat(this.jobRepository.findAll().size()).isEqualTo(1);
	}
}
