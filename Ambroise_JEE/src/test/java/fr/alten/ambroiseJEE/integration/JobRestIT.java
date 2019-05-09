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

import org.bson.types.ObjectId;
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
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.JobRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

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

	/**
	 * @test creating a new {@link Job}
	 * @context The {@link Job} doesn't already exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link CreatedException} and the
	 *           {@link Job} in base has the same field's values than the JSON
	 *           newJob.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createJob_with_success() throws Exception {

		// setup
		String newJob = "{" + "\"title\":\"newJob\"" + "}";

		MvcResult result = this.mockMvc.perform(post("/job").contentType(MediaType.APPLICATION_JSON).content(newJob))
				.andReturn();

		// Checking that the ResponseBody contains a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new job in base and its fields's value
		Optional<Job> jobOptional = this.jobRepository.findByTitle("newJob");
		assertTrue(jobOptional.isPresent());
		assertThat(jobOptional.get().getTitle()).isEqualTo("newJob");
	}

	/**
	 * @test creating a new {@link Job}
	 * @context The {@link Job} already exist in base. The Json is correctly set.
	 * @expected the response contains a {@link ConflictException} and the
	 *           {@link Job} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
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

		// Checking that the ResponseBody contains a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.job is in base
		assertThat(this.jobRepository.count()).isEqualTo(1);
	}

	/**
	 * @test creating a new {@link Job}
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException} and
	 *           the {@link Job} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createJob_with_missingRequiredFields() throws Exception {

		// setup
		String newJob = "{}";

		MvcResult result = this.mockMvc.perform(post("/job").contentType(MediaType.APPLICATION_JSON).content(newJob))
				.andReturn();

		// Checking that the ResponseBody contains a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no job in base
		assertThat(this.jobRepository.findAll()).isEmpty();
	}

	/**
	 * @test deleting a {@link Job} in base.
	 * @context The {@link Job} to delete exist in base. The Json is correctly set.
	 * @expected the response contains a {@link OkException} and the {@link Job}'s
	 *           name to delete in base has been set to "deactivated..."
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteJob_with_success() throws Exception {

		// setup
		String jobToDelete = "{" + "\"title\":\"job\"" + "}";
		// Pre-inserting a Job with title code as this.job for having a job to delete
		// with success
		jobRepository.insert(job);
		// Checking pre-insertion
		Optional<Job> jobOptional = this.jobRepository.findByTitle("job");
		ObjectId jobOptionalID = jobOptional.get().get_id();
		assertTrue(jobOptional.isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/job").contentType(MediaType.APPLICATION_JSON).content(jobToDelete)).andReturn();

		// Checking that the ResponseBody contains a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the jobToDelete had been deactivated
		assertThat(this.jobRepository.findBy_id(jobOptionalID).get().getTitle()).startsWith("deactivated");
	}

	/**
	 * @test deleting a {@link Job} in base.
	 * @context The {@link Job} to delete doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteJob_with_resourceNotFound() throws Exception {

		// setup
		String jobToDelete = "{" + "\"title\":\"jobFalse\"" + "}";
		// Checking if there is not already a job in base with the code : 00000
		assertFalse(this.jobRepository.findByTitle("jobFalse").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/job").contentType(MediaType.APPLICATION_JSON).content(jobToDelete)).andReturn();

		// Checking that the ResponseBody contains a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test deleting a {@link Job} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteJob_with_missingRequiredFields() throws Exception {

		// setup
		String jobToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/job").contentType(MediaType.APPLICATION_JSON).content(jobToDelete)).andReturn();

		// Checking that the ResponseBody contains a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	/**
	 * @test Getting the {@link List} of all {@link Job} present in base.
	 * @context pré-inserting 20 {@link Job}
	 * @expected The {@link List} of {@link Job} contains all of the pre-inserted
	 *           {@link Job}
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void getJobs() throws Exception {

		// Pre-insertion of 20 jobs for test
		for (int i = 0; i < 20; i++) {
			Job jobForGet = new Job();
			jobForGet.setTitle("Job" + i);
			this.jobRepository.insert(jobForGet);
			Optional<Job> jobOptional = this.jobRepository.findByTitle("Job" + i);
			assertTrue(jobOptional.isPresent());
			assertThat(jobOptional.get().getTitle()).isEqualTo("Job" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/jobs").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (JsonElement jjob : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonJob = jjob.getAsJsonObject();
			assertThat(jsonJob.get("title").getAsString()).isEqualTo("Job" + count);
			count++;
		}
	}

	/**
	 * @test updating a {@link Job} in base.
	 * @context The {@link Job} to update exist in base. The Json is correctly set.
	 * @expected the response contains a {@link OkException} and the {@link Job}'s
	 *           name to update in base has been set with the new name.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateJob_with_success() throws Exception {

		// setup
		String updatedJob = "{" + "\"title\":\"updateJob\"," + "\"oldTitle\":\"job\"" + "}";
		// Pre-inserting a job to update
		jobRepository.insert(job);
		// Checking pre-insertion
		assertTrue(this.jobRepository.findByTitle("job").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/job").contentType(MediaType.APPLICATION_JSON).content(updatedJob)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated job in base
		Optional<Job> jobOptional = this.jobRepository.findByTitle("updateJob");
		assertTrue(jobOptional.isPresent());
		assertThat(jobOptional.get().getTitle()).isEqualTo("updateJob");
	}

	/**
	 * @test updating a {@link Job} in base.
	 * @context The {@link Job} to update doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateJob_with_resourceNotFound() throws Exception {

		// setup
		String updatedJob = "{" + "\"title\":\"jobFalse\"," + "\"oldTitle\":\"jobNotFound\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/job").contentType(MediaType.APPLICATION_JSON).content(updatedJob)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test updating a {@link Job} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateJob_with_missingRequiredFields() throws Exception {

		// setup
		String jobToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/job").contentType(MediaType.APPLICATION_JSON).content(jobToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void y_testIndex() {
		// asserting the collection job exist
		assertTrue(mongoTemplate.collectionExists("job"));

		// asserting all unique index are present

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("title", false);

		// getting all indexed field of the collection "job"
		List<IndexInfo> indexList = mongoTemplate.indexOps("job").getIndexInfo();

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

//	/**
//	 * This is not a Test ! This method has been set in a {@link Test} because its
//	 * not possible to drop the database in {@link AfterClass}. Indeed,
//	 * {@link Autowired} doesn't work with static access required by
//	 * {@link AfterClass}.
//	 *
//	 * @author Kylian Gehier
//	 */
//	@Test
//	public void z_DroppingDatabase() {
//		// Last test run to drop the database for next test classes.
//		mongoTemplate.getDb().drop();
//	}
}
