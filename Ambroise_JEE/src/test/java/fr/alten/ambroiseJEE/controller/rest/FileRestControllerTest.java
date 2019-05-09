package fr.alten.ambroiseJEE.controller.rest;

import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.controller.business.FileBusinessController;
import fr.alten.ambroiseJEE.controller.business.FileStorageBusinessController;
import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.dao.FileRepository;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 *
 * @author Andy Chabalier
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class FileRestControllerTest {

	@SpyBean
	private static File spiedFile = new File();

	@BeforeClass
	public static void init() {
		FileRestControllerTest.initFile();
	}

	public static void initFile() {
		FileRestControllerTest.spiedFile.set_id(new ObjectId());
		FileRestControllerTest.spiedFile.setExtension("");
		FileRestControllerTest.spiedFile.setPath("");
	}

	@InjectMocks
	@Spy
	private final FileRestController fileRestController = new FileRestController();

	@Spy
	private JsonNode spiedJsonNode;
	@Spy
	private final ResourceNotFoundException spiedResourceNotFoundException = new ResourceNotFoundException();
	@Mock
	private Resource mockedResource;
	@Mock
	private HttpServletRequest mockedHttpServletRequest;
	@Mock
	private MultipartFile mockedMultipartFile;
	@Mock
	private FileBusinessController fileBusinessController;
	@Mock
	private FileStorageBusinessController fileStorageBusinessController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private OkException mockedOkException;
	@Mock
	private ForbiddenException mockedForbiddenException;
	@Mock
	private UnprocessableEntityException mockedUnprocessableEntityException;
	@Mock
	private ResourceNotFoundException mockedResourceNotFoundException;
	@Mock
	private InternalServerErrorException mockedInternalServerErrorException;

	@Mock
	private NoSuchFileException mockedNoSuchFileException;

	@Mock
	private ResponseEntity<Resource> mockedResponseEntity;

	@Mock
	private FileRepository fileRepository;

	@After
	public void afterEachTest() {
		fileRepository.deleteAll();
	}

	/**
	 * @test testing deletion with valid parameters and errors
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile_with_error() {
		// setup
		Mockito.doReturn(true).when(this.fileRestController).isValid(ArgumentMatchers.anyString());
		Mockito.when(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.thenReturn(this.mockedOkException);
		// return a different exception than OkException
		Mockito.when(this.fileBusinessController.deleteFile("", UserRole.MANAGER_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert the same error than provided previously
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(HttpException.class);
	}

	/**
	 * @test testing deletion with invalid parameters
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile_with_invalidParam() {
		// setup
		Mockito.doReturn(false).when(this.fileRestController).isValid(ArgumentMatchers.anyString());

		// assert UnprocessableEntityException
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing deletion with valid parameters and errors
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile_with_storageError() {
		// setup
		Mockito.doReturn(true).when(this.fileRestController).isValid(ArgumentMatchers.anyString());
		// return a different exception than OkException
		Mockito.when(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.thenReturn(this.mockedResourceNotFoundException);

		// assert the same error than provided previously
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(this.mockedResourceNotFoundException.getClass());
	}

	/**
	 * @test testing deletion with valid parameters
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile_with_sucess() {
		// setup
		Mockito.doReturn(true).when(this.fileRestController).isValid(ArgumentMatchers.anyString());
		Mockito.when(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert HttpException
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(HttpException.class);
	}

	/**
	 * @test testing deletion with valid parameters and errors
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile_with_unsucess() {
		// setup
		Mockito.doReturn(true).when(this.fileRestController).isValid(ArgumentMatchers.anyString());
		// return a different exception than OkException
		Mockito.when(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.thenReturn(this.mockedResourceNotFoundException);

		// assert the same error than provided previously
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(this.mockedResourceNotFoundException.getClass());
	}

	/**
	 * @test get the list of all collection {@link File}
	 * @expected returning a String
	 * @author Andy Chabalier
	 */
	@Test
	public void getCollectionFiles_expectingString() {

		// setup
		Mockito.when(this.fileBusinessController.getCollectionFiles(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(UserRole.class))).thenReturn(new ArrayList<File>());

		// assert
		Assertions.assertThat(this.fileRestController.getCollectionFiles("", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test get a file {@link Files}
	 * @expected returning a String representation of Json file object
	 * @author Andy Chabalier
	 */
	@Test
	public void getFile_expectingString() {
		Mockito.doReturn(true).when(this.fileRestController).isValid(ArgumentMatchers.anyString());
		// setup
		Mockito.when(this.fileBusinessController.getDocument(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(UserRole.class))).thenReturn(FileRestControllerTest.spiedFile);

		// assert
		Assertions.assertThat(this.fileRestController.getFile("", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test get a file {@link File}
	 * @expected returning a String representation of Json file object
	 * @author Andy Chabalier
	 */
	@Test(expected = UnprocessableEntityException.class)
	public void getFile_with_invalid_id() {
		// setup
		Mockito.doReturn(false).when(this.fileRestController).isValid(ArgumentMatchers.anyString());

		// assert
		this.fileRestController.getFile("", "", UserRole.MANAGER_ADMIN);
	}

	/**
	 * @test get the list of all {@link File}
	 * @expected returning a String
	 * @author Andy Chabalier
	 */
	@Test
	public void getFiles_expectingString() {

		// setup
		Mockito.when(this.fileBusinessController.getFiles(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<File>());

		// assert
		Assertions.assertThat(this.fileRestController.getFiles("", UserRole.MANAGER_ADMIN)).isInstanceOf(String.class);
	}

	/**
	 * @test get the list of all forum {@link File}
	 * @expected returning a String
	 * @author Andy Chabalier
	 */
	@Test
	public void getForumFiles_expectingString() {

		// setup
		Mockito.when(this.fileBusinessController.getFilesForum(UserRole.MANAGER_ADMIN))
				.thenReturn(new ArrayList<File>());

		// assert
		Assertions.assertThat(this.fileRestController.getFilesForum("", UserRole.MANAGER_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test testing update with valid parameters and errors
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void updateFile_with_error() {
		// setup
		Mockito.doReturn(true).when(this.fileRestController).isValid(ArgumentMatchers.anyString());
		Mockito.when(this.fileStorageBusinessController.moveFile(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(UserRole.class)))
				.thenReturn(this.mockedOkException);
		// return a different exception than OkException
		Mockito.when(this.fileBusinessController.updateDocument(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(UserRole.class)))
				.thenReturn(this.mockedHttpException);

		// assert the same error than provided previously
		Assertions.assertThat(this.fileRestController.updateFile("", "", "", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(HttpException.class);
	}

	/**
	 * @test testing update with invalid parameters
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void updateFile_with_invalidParam() {
		// setup
		Mockito.doReturn(false).when(this.fileRestController).isValid(ArgumentMatchers.anyString());

		// assert UnprocessableEntityException
		Assertions.assertThat(this.fileRestController.updateFile("", "", "", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test testing update with valid parameters and errors
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void updateFile_with_storageError() {
		// setup
		Mockito.doReturn(true).when(this.fileRestController).isValid(ArgumentMatchers.anyString());
		// return a different exception than OkException
		Mockito.when(this.fileStorageBusinessController.moveFile(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(UserRole.class)))
				.thenReturn(this.spiedResourceNotFoundException);

		// assert the same error than provided previously
		Assertions.assertThat(this.fileRestController.updateFile("", "", "", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(this.mockedResourceNotFoundException.getClass());
	}

	/**
	 * @test testing update with valid parameters
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void updateFile_with_sucess() {
		// setup
		Mockito.doReturn(true).when(this.fileRestController).isValid(ArgumentMatchers.anyString());
		Mockito.when(this.fileRestController.updateFile("", "", "", "", "", "", UserRole.MANAGER_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert HttpException
		Assertions.assertThat(this.fileRestController.updateFile("", "", "", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(HttpException.class);
	}

	@Test
	public void uploadFile_sucess() {
		// setup
		Mockito.when(this.mockedMultipartFile.getOriginalFilename()).thenReturn("");
		Mockito.doReturn(true).when(this.fileRestController).fileNotNullCheck(ArgumentMatchers.any());
		Mockito.doReturn(FileRestControllerTest.spiedFile).when(this.fileBusinessController).createDocument(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(UserRole.class));

		// assert
		Assertions.assertThat(
				this.fileRestController.uploadFile(this.mockedMultipartFile, "", "", UserRole.MANAGER_ADMIN));

		// Verify
		Mockito.verify(this.fileStorageBusinessController, Mockito.atLeastOnce()).storeFile(
				ArgumentMatchers.any(MultipartFile.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.any(UserRole.class));
	}

	@Test(expected = UnprocessableEntityException.class)
	public void uploadFile_with_nullFile() {
		// setup
		Mockito.doReturn(false).when(this.fileRestController).fileNotNullCheck(ArgumentMatchers.any());

		// assert
		this.fileRestController.uploadFile(this.mockedMultipartFile, "", "", UserRole.MANAGER_ADMIN);
	}

	@Test
	public void uploadMultipleFiles() {
		// setup
		final MultipartFile[] files = { null };
		Mockito.doReturn(FileRestControllerTest.spiedFile).when(this.fileRestController).uploadFile(
				ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.any(UserRole.class));

		// assert
		Assertions.assertThat(this.fileRestController.uploadMultipleFiles(files, "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(List.class);
	}
}
