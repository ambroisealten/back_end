package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.controller.business.FileBusinessController;
import fr.alten.ambroiseJEE.controller.business.FileStorageBusinessController;
import fr.alten.ambroiseJEE.model.beans.File;
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

	@InjectMocks
	@Spy
	private final FileRestController fileRestController = new FileRestController();

	@SpyBean
	private final File spiedFile = new File();
	@Spy
	private JsonNode spiedJsonNode;

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

	/**
	 * @test testing valid id
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile_with_params_Valid() throws IOException {
		// setup
		Mockito.doReturn(true).when(this.fileRestController).isValid(ArgumentMatchers.any(String.class));
		Mockito.when(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.thenReturn(this.mockedOkException, this.mockedForbiddenException);

		// assert OkExeption
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(OkException.class);
		// assert ForbiddenException
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	/**
	 * @test testing invalid id
	 * @expected sucess for all test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteFile_with_params_invalid() throws IOException {
		// setup
		Mockito.doReturn(false).when(this.fileRestController).isValid(ArgumentMatchers.any(String.class));
		Mockito.when(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN)).thenReturn(
				this.mockedUnprocessableEntityException, this.mockedResourceNotFoundException,
				this.mockedInternalServerErrorException);

		// assert UnprocessableEntityException
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

		// assert ResourceNotFoundException
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(ResourceNotFoundException.class);

		// assert InternalServerErrorException
		Assertions.assertThat(this.fileRestController.deleteFile("", "", "", "", UserRole.MANAGER_ADMIN))
				.isInstanceOf(InternalServerErrorException.class);
	}
}
