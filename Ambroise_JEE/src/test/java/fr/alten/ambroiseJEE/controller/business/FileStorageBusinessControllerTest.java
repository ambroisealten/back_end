package fr.alten.ambroiseJEE.controller.business;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.File;
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
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;

import fr.alten.ambroiseJEE.security.UserRole;
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

	@InjectMocks
	@Spy
	private FileStorageBusinessController fileStorageBusinessController;

	@Spy
	private static FileSystemResource spiedResourceNotExist = new FileSystemResource("");// spy(Resource.class);

	@Test
	public void deleteFile_with_forbidden() {
		Mockito.doReturn(false).when(this.fileStorageBusinessController).isAdmin(any(UserRole.class));

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void deleteFile_with_resourceNotFoundException() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(any(UserRole.class));
		Mockito.doThrow(NoSuchFileException.class).when(this.fileStorageBusinessController)
				.deleteFileInStorage(anyString(), anyString(), anyString());

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteFile_with_internalServerErrorException() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(any(UserRole.class));
		Mockito.doThrow(SecurityException.class, IOException.class).when(this.fileStorageBusinessController)
				.deleteFileInStorage(anyString(), anyString(), anyString());

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);
	}

	@Test
	public void deleteFile_sucess() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(any(UserRole.class));
		Mockito.doNothing().when(this.fileStorageBusinessController).deleteFileInStorage(anyString(), anyString(),
				anyString());

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.deleteFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(OkException.class);
	}

	@Test(expected = UnprocessableEntityException.class)
	public void loadFileAsResource_with_unprocessableEntityException() throws MalformedURLException {
		Mockito.doThrow(MalformedURLException.class).when(this.fileStorageBusinessController).loadResource(anyString());

		this.fileStorageBusinessController.loadFileAsResource("");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void loadFileAsResource_with_resourceNotFoundException() throws MalformedURLException {
		Mockito.doReturn(spiedResourceNotExist).when(this.fileStorageBusinessController).loadResource(anyString());

		this.fileStorageBusinessController.loadFileAsResource("");
	}

	@Test
	public void loadFileAsResource_sucess() throws IOException {
		File file = new File("/");
		FileSystemResource resource = new FileSystemResource(file);

		Mockito.doReturn(resource).when(this.fileStorageBusinessController).loadResource(anyString());

		Assertions.assertThat(this.fileStorageBusinessController.loadFileAsResource("")).isEqualTo(resource);
	}

	@Test
	public void moveFile_with_forbidden() {
		Mockito.doReturn(false).when(this.fileStorageBusinessController).isAdmin(any(UserRole.class));

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void moveFile_with_unprocessableEntityException() {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(any(UserRole.class));
		Mockito.doReturn(true).when(this.fileStorageBusinessController).fileNameAndPathIntegrity(anyString(),
				anyString());

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void moveFile_with_internalServerErrorException() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(any(UserRole.class));
		Mockito.doReturn(false).when(this.fileStorageBusinessController).fileNameAndPathIntegrity(anyString(),
				anyString());
		doReturn(Paths.get("/")).when(this.fileStorageBusinessController).getFileStorageLocationAbsolutePath();
		doThrow(SecurityException.class, UnsupportedOperationException.class, AtomicMoveNotSupportedException.class,
				DirectoryNotEmptyException.class).doNothing().when(this.fileStorageBusinessController)
						.moveFileFromTo(anyString(), any(Path.class), any(Path.class));

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
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(any(UserRole.class));
		Mockito.doReturn(false).when(this.fileStorageBusinessController).fileNameAndPathIntegrity(anyString(),
				anyString());
		doReturn(Paths.get("/")).when(this.fileStorageBusinessController).getFileStorageLocationAbsolutePath();
		doThrow(IOException.class).doNothing().when(this.fileStorageBusinessController).moveFileFromTo(anyString(),
				any(Path.class), any(Path.class));

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void moveFile_sucess() throws IOException {
		Mockito.doReturn(true).when(this.fileStorageBusinessController).isAdmin(any(UserRole.class));
		Mockito.doReturn(false).when(this.fileStorageBusinessController).fileNameAndPathIntegrity(anyString(),
				anyString());
		doNothing().when(this.fileStorageBusinessController).moveFileFromTo(anyString(), any(Path.class),
				any(Path.class));
		doReturn(Paths.get("/")).when(this.fileStorageBusinessController).getFileStorageLocationAbsolutePath();

		// assert
		Assertions.assertThat(this.fileStorageBusinessController.moveFile("", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(OkException.class);
	}
}
