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

import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.entityControllers.EmployerEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 *
 * @author Lucas Royackkers
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class EmployerBusinessControllerTest {

	@InjectMocks
	@Spy
	private final EmployerBusinessController employerBusinessController = new EmployerBusinessController();

	@Mock
	private EmployerEntityController employerEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode mockedJEmployer;
	@Mock
	private List<Employer> mockedEmployerList;

	@MockBean
	private Employer mockedEmployer;

	/**
	 * @test create a {@link Employer}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerEntityController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void createEmployer_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.employerBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.employerEntityController.createEmployer(this.mockedJEmployer)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.employerBusinessController.createEmployer(this.mockedJEmployer, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Employer}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerEntityController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void createEmployer_as_nonAdminUser() {

		// assert
		Mockito.doReturn(false).when(this.employerBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Assertions.assertThat(this.employerBusinessController.createEmployer(this.mockedJEmployer, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test delete a {@link Employer}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerEntityController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteEmployer_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.employerBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.employerEntityController.deleteEmployer(this.mockedJEmployer)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.employerBusinessController.deleteEmployer(this.mockedJEmployer, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Employer}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerEntityController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteEmployer_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.employerBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.employerBusinessController.deleteEmployer(this.mockedJEmployer, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test get all {@link Employer}
	 * @context as a non-Consultant or Deactivated
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerEntityController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void getEmployers_as_notConsultantOrDeactivated() {

		// setup
		Mockito.doReturn(true).when(this.employerBusinessController).isNotConsultantOrDeactivated(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.employerEntityController.getEmployers()).thenReturn(this.mockedEmployerList);
		// assert
		Assertions.assertThat(this.employerBusinessController.getEmployers(UserRole.DEACTIVATED))
				.isEqualTo(this.mockedEmployerList);
	}

	/**
	 * @test get all {@link Employer}
	 * @context as a non-Consultant or Deactivated
	 * @expected throw a {@link ForbiddenException}
	 * @author Lucas Royackkers
	 */
	@Test(expected = ForbiddenException.class)
	public void getEmployers_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.employerBusinessController).isNotConsultantOrDeactivated(ArgumentMatchers.any(UserRole.class));
		// throw
		this.employerBusinessController.getEmployers(UserRole.MANAGER_ADMIN);

	}


	/**
	 * @test update a {@link Employer}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerEntityController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateEmployer_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.employerBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.employerEntityController.updateEmployer(this.mockedJEmployer)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.employerBusinessController.updateEmployer(this.mockedJEmployer, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link Employer}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerEntityController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateEmployer_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.employerBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.employerBusinessController.updateEmployer(this.mockedJEmployer, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

}
