package fr.alten.ambroiseJEE.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.FileStorageBusinessController;
import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.FileRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.TokenIgnore;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

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
public class FileRestIT {

	private static User userAdmin = new User();
	private static File file = new File();

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
		FileRestIT.initAdminUser();
		FileRestIT.initFile();
		FileRestIT.initGson();
	}

	/**
	 * Setting the {@link User} that will be insered in base for tests
	 *
	 * @author Andy Chabalier
	 */
	private static void initAdminUser() {
		FileRestIT.userAdmin.setForname("tempUserAdmin");
		FileRestIT.userAdmin.setMail("tempUserAdmin@mail.com");
		FileRestIT.userAdmin.setName("tempUserAdminName");
	}

	/**
	 * Setting the {@link File} that will be insered in base for tests
	 *
	 * @author Andy Chabalier
	 */
	private static void initFile() {
		FileRestIT.file.set_id(new ObjectId());
		;
	}

	/**
	 * Setting the {@link Gson} use for inserting JSON file in request's body
	 *
	 * @author Andy Chabalier
	 */
	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		FileRestIT.gson = builder.create();
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileStorageBusinessController fileStorageBusinessController;

	@Autowired
	private FileRepository fileRepository;

	/**
	 * Droping both collections user and file after each test.
	 *
	 * @author Andy Chabalier
	 */
	@After
	public void afterEachTest() {
		this.userRepository.deleteAll();
		this.fileRepository.deleteAll();
	}

	/**
	 * Inserting an admin {@link User} in base for incoming tests Creating the file
	 * collection in base with its unique index "code" if its not existing in base
	 * already. Indeed, when the collection is dropped after each test, all the
	 * indexes are drop also and not recreated. So we need to do it manually and
	 * test index unicity in another test @see {@link BeansTest}.
	 *
	 * @author Andy Chabalier
	 */
	@Before
	public void beforeEachTest() {
		this.userRepository.insert(FileRestIT.userAdmin);
	}

	/**
	 * @test delete a {@link File}
	 * @context The param are correctly set.
	 * @expected the response contains a {@link OkException} and the {@link File}
	 *           has not been inserted in base.
	 * @throws Exception
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile() throws Exception {

		final File fileToDelete = this.createFile();

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/file")
				.contentType(MediaType.APPLICATION_JSON).param("_id", fileToDelete.get_id().toHexString())
				.param("path", fileToDelete.getPath()).param("extension", fileToDelete.getExtension())).andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("OkException"));
		// Checking there is no file in base
		Assertions.assertThat(this.fileRepository.findAll()).isEmpty();
	}

	/**
	 * @test delete a {@link File}
	 * @context The param are'nt correctly set.
	 * @expected the response contains a {@link UnprocessableEntityException} and
	 *           the {@link File} has not been deleted.
	 * @throws Exception
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile_with_unprocessableEntityException() throws Exception {

		// send request with a not valid id (empty)
		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/file")
				.contentType(MediaType.APPLICATION_JSON).param("_id", "").param("path", "").param("extension", ""))
				.andReturn();

		// Checking that the ResponseBody contain a UnprocessableEntityException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
		// Checking there is no file in base
		Assertions.assertThat(this.fileRepository.findAll()).isEmpty();
	}

	/**
	 * @test delete a {@link File}
	 * @context The file not exist.
	 * @expected the response contains a {@link ResourceNotFoundException} and the
	 *           {@link File} has not been deleted.
	 * @throws Exception
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile_with_resourceNotFoundException() throws Exception {

		final MvcResult result = this.mockMvc
				.perform(MockMvcRequestBuilders.delete("/file").contentType(MediaType.APPLICATION_JSON)
						.param("_id", new ObjectId().toHexString()).param("path", "").param("extension", ""))
				.andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		Assert.assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
		// Checking there is no file in base
		Assertions.assertThat(this.fileRepository.findAll()).isEmpty();
	}

	@Test
	public void downloadFile() throws Exception {

		File createdFile = this.createFile();

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
				.get("/file/" + createdFile.get_id().toHexString() + "." + createdFile.getExtension())
				.contentType(MediaType.APPLICATION_JSON).param("_id", new ObjectId().toHexString())
				.param("path", createdFile.getPath())).andReturn();

		// Checking that the ResponseBody contain a ResourceNotFoundException
		Assert.assertTrue(
				result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION).contains("attachment; filename=\""));

		// destroy physical file
		this.fileStorageBusinessController.deleteFile(createdFile.get_id().toHexString(), createdFile.getPath(),
				createdFile.getExtension(), UserRole.MANAGER_ADMIN);
	}

	@Test
	public void downloadFile_with_resourceNotFoundException() throws Exception {

		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + "xxxx")
				.contentType(MediaType.APPLICATION_JSON).param("_id", new ObjectId().toHexString()).param("path", "xx"))
				.andReturn();
		Assert.assertTrue(result.getResponse().getStatus() == 404);
	}

	@Test
	public void downloadFile_with_unprocessableEntityException() throws Exception {
		// send request with unauthorized character
		// invalid path
		final MvcResult result = this.mockMvc
				.perform(MockMvcRequestBuilders.get("/file/" + "xx").contentType(MediaType.APPLICATION_JSON)
						.param("_id", new ObjectId().toHexString()).param("path", "://"))
				.andReturn();
		Assert.assertTrue(result.getResponse().getStatus() == 422);

		// invalid filename
		final MvcResult result2 = this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + "://")
				.contentType(MediaType.APPLICATION_JSON).param("_id", new ObjectId().toHexString()).param("path", ""))
				.andReturn();
		Assert.assertTrue(result2.getResponse().getStatus() == 422);
	}

	@Test
	public void getCollectionFiles() throws Exception {
		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/files/collection")
				.contentType(MediaType.APPLICATION_JSON).param("path", "xxx")).andReturn();
		Assert.assertTrue(result.getResponse().getContentAsString().matches("\\[.*\\]"));
	}

	@Test
	public void getFile_with_unprocessableEntityException() throws Exception {
		final MvcResult result = this.mockMvc.perform(
				MockMvcRequestBuilders.get("/file").contentType(MediaType.APPLICATION_JSON).param("fileName", "xx"))
				.andReturn();
		Assert.assertTrue(result.getResponse().getStatus() == 422);
	}

	@Test
	public void getFile_with_resourceNotFoundException() throws Exception {
		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/file")
				.contentType(MediaType.APPLICATION_JSON).param("fileName", (new ObjectId()).toHexString())).andReturn();
		Assert.assertTrue(result.getResponse().getStatus() == 404);
	}

	@Test
	public void getFile() throws Exception {
		File createdFile = this.createFile();
		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/file")
				.contentType(MediaType.APPLICATION_JSON).param("fileName", createdFile.get_id().toHexString()))
				.andReturn();
		Assert.assertTrue(result.getResponse().getStatus() == 200);

		// destroy physical file
		this.fileStorageBusinessController.deleteFile(createdFile.get_id().toHexString(), createdFile.getPath(),
				createdFile.getExtension(), UserRole.MANAGER_ADMIN);
	}

	@Test
	public void getFiles() throws Exception {
		final MvcResult result = this.mockMvc
				.perform(MockMvcRequestBuilders.get("/files").contentType(MediaType.APPLICATION_JSON)).andReturn();
		Assert.assertTrue(result.getResponse().getStatus() == 200);
	}

	@Test
	public void getFilesForum() throws Exception {
		final MvcResult result = this.mockMvc
				.perform(MockMvcRequestBuilders.get("/files/forum").contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		Assert.assertTrue(result.getResponse().getStatus() == 200);
	}

	@Test
	public void updateFile_with_unprocessableEntityException_id() throws Exception {
		// invalid id
		final MvcResult result = this.mockMvc
				.perform(MockMvcRequestBuilders.put("/file").contentType(MediaType.APPLICATION_JSON).param("_id", "")
						.param("extension", "").param("displayName", "").param("newPath", "").param("oldPath", ""))
				.andReturn();
		Assert.assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));
	}

	@Test
	public void updateFile() throws Exception {
		File createdFile = this.createFile();
		// invalid id
		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put("/file")
				.contentType(MediaType.APPLICATION_JSON).param("_id", createdFile.get_id().toHexString())
				.param("extension", createdFile.getExtension()).param("displayName", createdFile.getDisplayName())
				.param("newPath", "/newPath/").param("oldPath", createdFile.getPath())).andReturn();
		Assert.assertTrue(result.getResponse().getContentAsString().contains("OkException"));

		// destroy physical file
		this.fileStorageBusinessController.deleteFile(createdFile.get_id().toHexString(), createdFile.getPath(),
				createdFile.getExtension(), UserRole.MANAGER_ADMIN);
	}

	@Test
	public void updateFile_with_unprocessableEntityException_nameAndPath() throws Exception {
		File createdFile = this.createFile();
		// invalid id
		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put("/file")
				.contentType(MediaType.APPLICATION_JSON).param("_id", createdFile.get_id().toHexString())
				.param("extension", createdFile.getExtension()).param("displayName", createdFile.getDisplayName())
				.param("newPath", "").param("oldPath", createdFile.getPath())).andReturn();
		Assert.assertTrue(result.getResponse().getContentAsString().contains("UnprocessableEntityException"));

		// destroy physical file
		this.fileStorageBusinessController.deleteFile(createdFile.get_id().toHexString(), createdFile.getPath(),
				createdFile.getExtension(), UserRole.MANAGER_ADMIN);
	}

	@Test
	public void updateFile_with_resourceNotFoundException() throws Exception {
		final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put("/file")
				.contentType(MediaType.APPLICATION_JSON).param("_id", (new ObjectId()).toHexString())
				.param("extension", "").param("displayName", "").param("newPath", "/").param("oldPath", "/"))
				.andReturn();
		Assert.assertTrue(result.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

	@Test
	public void uploadMultipleFiles() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.multipart("/file/multiples").param("files", "[]").param("path", "/"))
				.andExpect(status().is(200));
	}

	@Test
	public void uploadFile() throws Exception {
		final byte[] file = new byte[1];
		file[0] = ' ';
		final MockMultipartFile multipartFile = new MockMultipartFile("file", file);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/file").file(multipartFile).param("path", "/"))
				.andExpect(status().is(200)).andReturn();

		File uploadedFile = gson.fromJson(result.getResponse().getContentAsString(), File.class);

		// destroy physical file
		this.fileStorageBusinessController.deleteFile(uploadedFile.get_id().toHexString(), uploadedFile.getPath(),
				uploadedFile.getExtension(), UserRole.MANAGER_ADMIN);
	}

	@Test
	public void uploadFile_with_unprocessableEntityException() throws Exception {
		final byte[] file = null;
		final MockMultipartFile multipartFile = new MockMultipartFile("file", file);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/file").file(multipartFile).param("path", "/"))
				.andExpect(status().is(422));
	}

	@Test
	public void y_testIndex() {
		// asserting the collection file exist
		Assert.assertTrue(this.mongoTemplate.collectionExists("files"));

		// asserting all unique index are present

		final HashMap<String, Boolean> indexPresent = new HashMap<>();
		indexPresent.put("_id_", false);

		// getting all indexed field of the collection "file"
		final List<IndexInfo> indexList = this.mongoTemplate.indexOps("files").getIndexInfo();

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

	/**
	 * create a file in base, store it in physical storage and return it
	 * 
	 * @return the created file
	 * @author Andy Chabalier
	 */
	public File createFile() {
		// setup
		final byte[] fileContent = new byte[1];
		fileContent[0] = ' ';
		final MultipartFile multipartFile = new MockMultipartFile("fileTest", fileContent);

		// save File to database
		final File fileToSave = new File();
		fileToSave.setPath("/test/");
		fileToSave.setExtension(FilenameUtils.getExtension("fileTest.test"));
		fileToSave.setDateOfCreation(System.currentTimeMillis());
		fileToSave.setDisplayName("fileTest.test");

		final File newFile = this.fileRepository.save(fileToSave);
		// save file to physical storage
		this.fileStorageBusinessController.storeFile(multipartFile, newFile.getPath(),
				newFile.get_id() + "." + newFile.getExtension(), UserRole.MANAGER_ADMIN);
		return newFile;
	}
}
