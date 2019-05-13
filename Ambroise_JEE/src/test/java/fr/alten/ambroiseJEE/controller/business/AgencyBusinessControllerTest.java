package fr.alten.ambroiseJEE.controller.business;

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

import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.entityControllers.AgencyEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Test class for AgencyBusinessController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AgencyBusinessControllerTest {

	@InjectMocks
	@Spy
	private final AgencyBusinessController agencyBusinessController = new AgencyBusinessController();

	@Mock
	private AgencyEntityController agencyEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode mockedJAgency;
	@Mock
	private List<Agency> mockedAgencyList;

	@MockBean
	private Agency mockedAgency;

	/**
	 * @test create a {@link Agency}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link AgencyEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.agencyBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.agencyEntityController.createAgency(this.mockedJAgency)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.agencyBusinessController.createAgency(this.mockedJAgency, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Agency}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link AgencyEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_as_nonAdminUser() {

		// assert
		Mockito.doReturn(false).when(this.agencyBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Assertions.assertThat(this.agencyBusinessController.createAgency(this.mockedJAgency, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test delete a {@link Agency}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link AgencyEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.agencyBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.agencyEntityController.deleteAgency(this.mockedJAgency)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.agencyBusinessController.deleteAgency(this.mockedJAgency, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Agency}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link AgencyEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.agencyBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.agencyBusinessController.deleteAgency(this.mockedJAgency, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test update a {@link Agency}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link AgencyEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.agencyBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.agencyEntityController.updateAgency(this.mockedJAgency)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.agencyBusinessController.updateAgency(this.mockedJAgency, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link Agency}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link AgencyEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.agencyBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.agencyBusinessController.updateAgency(this.mockedJAgency, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}
}
