package fr.alten.ambroiseJEE.controller.business.geographic;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.entityControllers.PostalCodeEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Test class for PostalCodeBusinessController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PostalCodeBusinessControllerTest {

	@InjectMocks
	@Spy
	private final PostalCodeBusinessController postalCodeBusinessController = new PostalCodeBusinessController();

	@Mock
	private PostalCodeEntityController postalCodeEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode mockedJPostalCode;
	@Mock
	private List<PostalCode> mockedPostalCodeList;

	@MockBean
	private PostalCode mockedPostalCode;

	/**
	 * @test create a {@link PostalCode}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link PostalCodeEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.postalCodeBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.postalCodeEntityController.createPostalCode(this.mockedJPostalCode))
				.thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(
				this.postalCodeBusinessController.createPostalCode(this.mockedJPostalCode, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link PostalCode}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link PostalCodeEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_as_nonAdminUser() {

		// assert
		Mockito.doReturn(false).when(this.postalCodeBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Assertions.assertThat(
				this.postalCodeBusinessController.createPostalCode(this.mockedJPostalCode, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test delete a {@link PostalCode}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link PostalCodeEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.postalCodeBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.postalCodeEntityController.deletePostalCode(this.mockedJPostalCode))
				.thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(
				this.postalCodeBusinessController.deletePostalCode(this.mockedJPostalCode, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link PostalCode}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link PostalCodeEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.postalCodeBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(
				this.postalCodeBusinessController.deletePostalCode(this.mockedJPostalCode, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test update a {@link PostalCode}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link PostalCodeEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.postalCodeBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.postalCodeEntityController.updatePostalCode(this.mockedJPostalCode))
				.thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(
				this.postalCodeBusinessController.updatePostalCode(this.mockedJPostalCode, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link PostalCode}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link PostalCodeEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.postalCodeBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(
				this.postalCodeBusinessController.updatePostalCode(this.mockedJPostalCode, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}
}
