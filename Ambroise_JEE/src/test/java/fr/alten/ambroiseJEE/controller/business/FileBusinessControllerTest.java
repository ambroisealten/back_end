package fr.alten.ambroiseJEE.controller.business;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
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

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.dao.FileRepository;
import fr.alten.ambroiseJEE.model.entityControllers.FileEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 *
 * @author Andy Chabalier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class FileBusinessControllerTest {

	@SpyBean
	private static File spiedFile = new File();

	@BeforeClass
	public static void init() {
		FileBusinessControllerTest.initFile();
	}

	public static void initFile() {
		FileBusinessControllerTest.spiedFile.set_id(new ObjectId());
		FileBusinessControllerTest.spiedFile.setExtension("");
		FileBusinessControllerTest.spiedFile.setPath("");
	}

	@InjectMocks
	@Spy
	private FileBusinessController fileBusinessController;

	@Mock
	private FileEntityController fileEntityController;

	@Mock
	private FileRepository fileRepository;

	@Mock
	private HttpException mockedHttpException;

	@Test
	public void createDocument() {
		Mockito.doReturn(true).when(this.fileBusinessController).haveAccess(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.fileEntityController.pushDocument(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(new File());

		// assert
		Assertions.assertThat(this.fileBusinessController.createDocument("", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(File.class);
	}

	@Test(expected = ForbiddenException.class)
	public void createDocument_with_ForbiddenError() {
		Mockito.doReturn(false).when(this.fileBusinessController).haveAccess(ArgumentMatchers.any(UserRole.class));

		// assert
		this.fileBusinessController.createDocument("", "", UserRole.MANAGER_ADMIN);
	}

	@Test
	public void deleteFile() {
		Mockito.doReturn(true).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.fileEntityController.deleteFile("")).thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.fileBusinessController.deleteFile("", UserRole.MANAGER_ADMIN))
				.isInstanceOf(HttpException.class);
	}

	@Test
	public void deleteFile_with_ForbiddenError() {
		Mockito.doReturn(false).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		// assert
		Assertions.assertThat(this.fileBusinessController.deleteFile("", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void getCollectionFiles() {
		Mockito.doReturn(true).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.fileEntityController.getCollectionFiles(ArgumentMatchers.anyString()))
				.thenReturn(new ArrayList<File>());

		// assert
		Assertions.assertThat(this.fileBusinessController.getCollectionFiles("", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ArrayList.class);
	}

	@Test(expected = ForbiddenException.class)
	public void getCollectionFiles_with_ForbiddenError() {
		Mockito.doReturn(false).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		// assert
		this.fileBusinessController.getCollectionFiles("", UserRole.MANAGER_ADMIN);
	}

	@Test
	public void getDocument() {
		Mockito.doReturn(true).when(this.fileBusinessController).haveAccess(ArgumentMatchers.any(UserRole.class));
		Mockito.doReturn(true).when(this.fileBusinessController).isValid(ArgumentMatchers.anyString());
		Mockito.doReturn(FileBusinessControllerTest.spiedFile).when(this.fileEntityController)
				.getFile(ArgumentMatchers.any(ObjectId.class));

		// assert
		Assertions
				.assertThat(this.fileBusinessController.getDocument(
						FileBusinessControllerTest.spiedFile.get_id().toHexString(), UserRole.MANAGER_ADMIN))
				.isInstanceOf(File.class);
	}

	@Test(expected = ForbiddenException.class)
	public void getDocument_with_ForbiddenException() {
		Mockito.doReturn(false).when(this.fileBusinessController).haveAccess(ArgumentMatchers.any(UserRole.class));

		// assert
		this.fileBusinessController.getDocument("", UserRole.MANAGER_ADMIN);
	}

	@Test(expected = UnprocessableEntityException.class)
	public void getDocument_with_unprocessableEntityException() {
		Mockito.doReturn(true).when(this.fileBusinessController).haveAccess(ArgumentMatchers.any(UserRole.class));
		Mockito.doReturn(false).when(this.fileBusinessController).isValid(ArgumentMatchers.anyString());

		// assert
		this.fileBusinessController.getDocument(new ObjectId().toHexString(), UserRole.MANAGER_ADMIN);
	}

	@Test
	public void getFiles() {
		Mockito.doReturn(true).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.fileEntityController.getFiles()).thenReturn(new ArrayList<File>());

		// assert
		Assertions.assertThat(this.fileBusinessController.getFiles(UserRole.MANAGER_ADMIN))
				.isInstanceOf(ArrayList.class);
	}

	@Test(expected = ForbiddenException.class)
	public void getFiles_with_ForbiddenError() {
		Mockito.doReturn(false).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		// assert
		this.fileBusinessController.getFiles(UserRole.MANAGER_ADMIN);
	}

	@Test
	public void getFilesForum() {
		Mockito.doReturn(true).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.fileEntityController.getFilesForum()).thenReturn(new ArrayList<File>());

		// assert
		Assertions.assertThat(this.fileBusinessController.getFilesForum(UserRole.MANAGER_ADMIN))
				.isInstanceOf(ArrayList.class);
	}

	@Test(expected = ForbiddenException.class)
	public void getFilesForum_with_ForbiddenError() {
		Mockito.doReturn(false).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		// assert
		this.fileBusinessController.getFilesForum(UserRole.MANAGER_ADMIN);
	}

	@Test
	public void updateDocument() {
		Mockito.doReturn(true).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.fileEntityController.updateFile("", "", "")).thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.fileBusinessController.updateDocument("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(HttpException.class);
	}

	@Test
	public void updateDocument_with_ForbiddenError() {
		Mockito.doReturn(false).when(this.fileBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		// assert
		Assertions.assertThat(this.fileBusinessController.updateDocument("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}
}
