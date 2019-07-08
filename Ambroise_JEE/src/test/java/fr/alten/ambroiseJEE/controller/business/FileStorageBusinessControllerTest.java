package fr.alten.ambroiseJEE.controller.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
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
public class FileStorageBusinessControllerTest {

	@Spy
	private static FileSystemResource spiedResourceNotExist = new FileSystemResource("");

	@InjectMocks
	@Spy
	private FileStorageBusinessController fileStorageBusinessController;

	@Mock
	private MultipartFile mockedMultipartFile;

	@Mock
	private IOException mockedIOException;

	@Test
	public void deleteFile_sucess() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doNothing().when(this.fileStorageBusinessController).deleteFileInStorage(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(OkException.class);
	}

	@Test
	public void deleteFile_with_forbidden() {
		Mockito.doReturn(false).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteFile_with_internalServerErrorException() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doThrow(SecurityException.class, IOException.class).when(this.fileStorageBusinessController)
				.deleteFileInStorage(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
						ArgumentMatchers.anyString());

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);
	}

	@Test
	public void deleteFile_with_resourceNotFoundException() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doThrow(NoSuchFileException.class).when(this.fileStorageBusinessController).deleteFileInStorage(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void loadFileAsResource_sucess() throws IOException {
		final File file = new File("/");
		final FileSystemResource resource = new FileSystemResource(file);

		Mockito.doReturn(resource).when(this.fileStorageBusinessController).loadResource(ArgumentMatchers.anyString());

		Assertions.assertThat(this.fileStorageBusinessController.loadFileAsResource("")).isEqualTo(resource);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void loadFileAsResource_with_resourceNotFoundException() throws MalformedURLException {
		Mockito.doReturn(FileStorageBusinessControllerTest.spiedResourceNotExist)
				.when(this.fileStorageBusinessController).loadResource(ArgumentMatchers.anyString());

		this.fileStorageBusinessController.loadFileAsResource("");
	}

	@Test(expected = UnprocessableEntityException.class)
	public void loadFileAsResource_with_unprocessableEntityException() throws MalformedURLException {
		Mockito.doThrow(MalformedURLException.class).when(this.fileStorageBusinessController)
				.loadResource(ArgumentMatchers.anyString());

		this.fileStorageBusinessController.loadFileAsResource("");
	}

	@Test
	public void moveFile_sucess() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doReturn(false).when(this.fileStorageBusinessController)
				.fileNameAndPathIntegrity(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
		Mockito.doNothing().when(this.fileStorageBusinessController).moveFileFromTo(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Path.class));
		Mockito.doReturn(Paths.get("/")).when(this.fileStorageBusinessController).getFileStorageLocationAbsolutePath();

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(OkException.class);
	}

	@Test
	public void moveFile_with_forbidden() {
		Mockito.doReturn(false).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void moveFile_with_internalServerErrorException() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doReturn(false).when(this.fileStorageBusinessController)
				.fileNameAndPathIntegrity(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
		Mockito.doReturn(Paths.get("/")).when(this.fileStorageBusinessController).getFileStorageLocationAbsolutePath();
		Mockito.doThrow(SecurityException.class, UnsupportedOperationException.class,
				AtomicMoveNotSupportedException.class, DirectoryNotEmptyException.class).doNothing()
				.when(this.fileStorageBusinessController).moveFileFromTo(ArgumentMatchers.anyString(),
						ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Path.class));

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);
	}

	@Test
	public void moveFile_with_resourceNotFoundException() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doReturn(false).when(this.fileStorageBusinessController)
				.fileNameAndPathIntegrity(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
		Mockito.doReturn(Paths.get("/")).when(this.fileStorageBusinessController).getFileStorageLocationAbsolutePath();
		Mockito.doThrow(IOException.class).doNothing().when(this.fileStorageBusinessController).moveFileFromTo(
				ArgumentMatchers.anyString(), ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Path.class));

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void moveFile_with_unprocessableEntityException() {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doReturn(true).when(this.fileStorageBusinessController)
				.fileNameAndPathIntegrity(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void storeFile_sucess() throws FileNotFoundException, IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doReturn(false).when(this.fileStorageBusinessController)
				.fileNameAndPathIntegrity(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
		Mockito.doReturn(new CreatedException()).when(this.fileStorageBusinessController).storeFileTo(
				ArgumentMatchers.any(MultipartFile.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

		// assert with role different of admin and manager
		Assertions.assertThat(
				this.fileStorageBusinessController.storeFile(this.mockedMultipartFile, "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(CreatedException.class);
	}

	@Test
	public void storeFile_with_forbiddenException() {
		Mockito.doReturn(false).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		// assert with role different of admin and manager
		Assertions
				.assertThat(
						this.fileStorageBusinessController.storeFile(this.mockedMultipartFile, "", "", UserRole.CDR))
				.isInstanceOf(ForbiddenException.class);

	}

	@Test
	public void storeFile_with_internalServerErrorException() throws FileNotFoundException, IOException {
		// Setup new multipartFile to allow file storage test
		final byte[] file = new byte[1];
		file[0] = ' ';
		final MultipartFile multipartFile = new MockMultipartFile("file", file);

		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doReturn(false).when(this.fileStorageBusinessController)
				.fileNameAndPathIntegrity(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
		Mockito.doReturn(Paths.get("/")).when(this.fileStorageBusinessController).getFileStorageLocationAbsolutePath();

		// assert with role different of admin and manager
		Assertions
				.assertThat(this.fileStorageBusinessController.storeFile(multipartFile, "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);
	}

	@Test
	public void storeFile_with_unprocessableEntityException() {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.doReturn(true).when(this.fileStorageBusinessController)
				.fileNameAndPathIntegrity(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

		// assert with role different of admin and manager
		Assertions.assertThat(
				this.fileStorageBusinessController.storeFile(this.mockedMultipartFile, "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}
}
