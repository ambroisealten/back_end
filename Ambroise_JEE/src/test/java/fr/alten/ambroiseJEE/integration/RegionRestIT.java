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

import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.RegionRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.TokenIgnore;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Integration test class for RegionRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegionRestIT {

	private static User userAdmin = new User();
	private static Region region = new Region();

	private static Gson gson;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RegionRepository regionRepository;

	@BeforeClass
	public static void beforeTests() {
		// Creating the dev folder and the dev file to ignore the token during tests.
		TokenIgnore.createDir();
		initAdminUser();
		initRegion();
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
	 * Setting the {@link Region} that will be insered in base for tests
	 *
	 * @author Camille Schnell
	 */
	private static void initRegion() {
		region.setNom("region");
		region.setCode("00000");
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
	 * region collection in base with its unique index "code" if its not existing in
	 * base already. Indeed, when the collection is dropped after each test, all the
	 * indexes are drop also and not recreated. So we need to do it manually and
	 * test index uniregion in another test @see {@link BeansTest}.
	 *
	 * @author Kylian Gehier
	 */
	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);
	}

	/**
	 * Droping both collections user and region after each test.
	 *
	 * @author Kylian Gehier
	 */
	@After
	public void afterEachTest() {
		userRepository.deleteAll();
		regionRepository.deleteAll();
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
	 * @test creating a new {@link Region}
	 * @context The {@link Region} doesn't already exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link CreatedException} and the
	 *           {@link Region} in base has the same field's values than the JSON
	 *           newRegion.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_with_success() throws Exception {

		// setup
		String newRegion = "{" + "\"nom\":\"newRegion\"," + "\"code\":\"00001\"" + "}";

		MvcResult result = this.mockMvc
				.perform(post("/region").contentType(MediaType.APPLICATION_JSON).content(newRegion)).andReturn();

		// Checking that the ResponseBody contains a CreatedException
		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		// checking the new region in base and its fields's value
		Optional<Region> regionOptional = this.regionRepository.findByCode("00001");
		assertTrue(regionOptional.isPresent());
		assertThat(regionOptional.get().getNom()).isEqualTo("newRegion");
	}

	/**
	 * @test creating a new {@link Region}
	 * @context The {@link Region} already exist in base. The Json is correctly set.
	 * @expected the response contains a {@link ConflictException} and the
	 *           {@link Region} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_with_conflict() throws Exception {

		// setup
		String newRegion = "{" + "\"nom\":\"newRegion\"," + "\"code\":\"00000\"" + "}";

		// Pre-inserting a Region with name code as this.region to create a
		// ConflictException
		regionRepository.insert(region);
		// Checking pre-insertion
		assertTrue(this.regionRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(post("/region").contentType(MediaType.APPLICATION_JSON).content(newRegion)).andReturn();

		// Checking that the ResponseBody contains a ConflictException
		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));

		// Checking only this.region is in base
		assertThat(this.regionRepository.count()).isEqualTo(1);
	}

	/**
	 * @test creating a new {@link Region}
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException} and
	 *           the {@link Region} has not been inserted in base.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_with_missingRequiredFields() throws Exception {

		// setup
		String newRegion = "{}";

		MvcResult result = this.mockMvc
				.perform(post("/region").contentType(MediaType.APPLICATION_JSON).content(newRegion)).andReturn();

		// Checking that the ResponseBody contains a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no region in base
		assertThat(this.regionRepository.findAll()).isEmpty();
	}

	/**
	 * @test deleting a {@link Region} in base.
	 * @context The {@link Region} to delete exist in base. The Json is correctly
	 *          set.
	 * @expected the response contains a {@link OkException} and the
	 *           {@link Region}'s name to delete in base has been set to
	 *           "deactivated..."
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_with_success() throws Exception {

		// setup
		String regionToDelete = "{" + "\"code\":\"00000\"" + "}";
		// Pre-inserting a Region with code as this.region to have a region to delete
		// with success
		regionRepository.insert(region);
		// Checking pre-insertion
		assertTrue(this.regionRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/region").contentType(MediaType.APPLICATION_JSON).content(regionToDelete)).andReturn();

		// Checking that the ResponseBody contains a OkException
		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the regionToDelete had been deactivated
		assertThat(this.regionRepository.findByCode("00000").get().getNom()).startsWith("deactivated");
	}

	/**
	 * @test deleting a {@link Region} in base.
	 * @context The {@link Region} to delete doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_with_resourceNotFound() throws Exception {

		// setup
		String regionToDelete = "{" + "\"code\":\"00000\"" + "}";
		// Checking if there is not already a region in base with the code : 00000
		assertFalse(this.regionRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(delete("/region").contentType(MediaType.APPLICATION_JSON).content(regionToDelete)).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test deleting a {@link Region} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_with_missingRequiredFields() throws Exception {

		// setup
		String regionToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(delete("/region").contentType(MediaType.APPLICATION_JSON).content(regionToDelete)).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	/**
	 * @test Getting the {@link List} of all {@link Region} present in base.
	 * @context pré-inserting 20 {@link Region}
	 * @expected The {@link List} of {@link Region} contains all of the pre-inserted
	 *           {@link Region}
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void getRegions() throws Exception {

		// Pre-insertion of 20 regions for test
		for (int i = 0; i < 20; i++) {
			Region regionForGet = new Region();
			regionForGet.setNom("Region" + i);
			regionForGet.setCode("Code" + i);
			this.regionRepository.insert(regionForGet);
			Optional<Region> regionOptional = this.regionRepository.findByCode("Code" + i);
			assertTrue(regionOptional.isPresent());
			assertThat(regionOptional.get().getNom()).isEqualTo("Region" + i);
		}

		MvcResult result = this.mockMvc.perform(get("/regions").contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		// verifying that we got the same cities as inserted before
		int count = 0;
		for (JsonElement jregion : gson.fromJson(jsonResult, JsonArray.class)) {
			JsonObject jsonRegion = jregion.getAsJsonObject();
			assertThat(jsonRegion.get("code").getAsString()).isEqualTo("Code" + count);
			assertThat(jsonRegion.get("nom").getAsString()).isEqualTo("Region" + count);
			count++;
		}
	}

	/**
	 * @test updating a {@link Region} in base.
	 * @context The {@link Region} to update exist in base. The Json is correctly
	 *          set.
	 * @expected the response contains a {@link OkException} and the
	 *           {@link Region}'s name to update in base has been set with the new
	 *           name.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_with_success() throws Exception {

		// setup
		String updatedRegion = "{" + "\"nom\":\"newRegion\"," + "\"code\":\"00000\"" + "}";
		// Pre-inserting a region to update
		regionRepository.insert(region);
		// Checking pre-insertion
		assertTrue(this.regionRepository.findByCode("00000").isPresent());

		MvcResult result = this.mockMvc
				.perform(put("/region").contentType(MediaType.APPLICATION_JSON).content(updatedRegion)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// Checking the updated region in base
		Optional<Region> regionOptional = this.regionRepository.findByCode("00000");
		assertTrue(regionOptional.isPresent());
		assertThat(regionOptional.get().getNom()).isEqualTo("newRegion");
	}

	/**
	 * @test updating a {@link Region} in base.
	 * @context The {@link Region} to update doesn't exist in base. The Json is
	 *          correctly set.
	 * @expected the response contains a {@link ResourceNotFoundException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_with_resourceNotFound() throws Exception {

		// setup
		String updatedRegion = "{" + "\"nom\":\"newRegion\"," + "\"code\":\"00000\"" + "}";

		MvcResult result = this.mockMvc
				.perform(put("/region").contentType(MediaType.APPLICATION_JSON).content(updatedRegion)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	/**
	 * @test updating a {@link Region} in base.
	 * @context The Json isn't correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException}.
	 * @throws Exception
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_with_missingRequiredFields() throws Exception {

		// setup
		String regionToDelete = "{}";

		MvcResult result = this.mockMvc
				.perform(put("/region").contentType(MediaType.APPLICATION_JSON).content(regionToDelete)).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void y_testIndex() {
		// asserting the collection region exist
		assertTrue(mongoTemplate.collectionExists("region"));

		// asserting all unique index are present

		HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);
		indexPresent.put("code", false);

		// getting all indexed field of the collection "region"
		List<IndexInfo> indexList = mongoTemplate.indexOps("region").getIndexInfo();

		for (IndexInfo index : indexList) {
			for (Map.Entry<String, Boolean> indexInMap : indexPresent.entrySet()) {
				if (index.getName().equals(indexInMap.getKey())) {

					// checking the uniregion of unique indexed fields - except for _id_ because
					// mongoDB consider his uniregion as false
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
