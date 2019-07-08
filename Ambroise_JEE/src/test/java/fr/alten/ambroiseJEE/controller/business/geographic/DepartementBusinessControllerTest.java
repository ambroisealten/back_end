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

import fr.alten.ambroiseJEE.model.beans.Departement;
import fr.alten.ambroiseJEE.model.entityControllers.DepartementEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Test class for DepartementBusinessController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DepartementBusinessControllerTest {

	@InjectMocks
	@Spy
	private final DepartementBusinessController departementBusinessController = new DepartementBusinessController();

	@Mock
	private DepartementEntityController departementEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode mockedJDepartement;
	@Mock
	private List<Departement> mockedDepartementList;

	@MockBean
	private Departement mockedDepartement;

	/**
	 * @test create a {@link Departement}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DepartementEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.departementBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.departementEntityController.createDepartement(this.mockedJDepartement)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.departementBusinessController.createDepartement(this.mockedJDepartement, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Departement}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DepartementEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_as_nonAdminUser() {

		// assert
		Mockito.doReturn(false).when(this.departementBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Assertions.assertThat(this.departementBusinessController.createDepartement(this.mockedJDepartement, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test delete a {@link Departement}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DepartementEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.departementBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.departementEntityController.deleteDepartement(this.mockedJDepartement)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.departementBusinessController.deleteDepartement(this.mockedJDepartement, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Departement}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DepartementEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.departementBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.departementBusinessController.deleteDepartement(this.mockedJDepartement, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test update a {@link Departement}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DepartementEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.departementBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.departementEntityController.updateDepartement(this.mockedJDepartement)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.departementBusinessController.updateDepartement(this.mockedJDepartement, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link Departement}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DepartementEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.departementBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.departementBusinessController.updateDepartement(this.mockedJDepartement, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}
}
