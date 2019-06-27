package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
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

import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.entityControllers.PersonEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.personRole.PersonRole;

/**
 * 
 * @author Lucas Royackkers
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsultantBusinessControllerTest {

	@InjectMocks
	@Spy
	private final ConsultantBusinessController consultantBusinessController = new ConsultantBusinessController();

	@Mock
	private PersonEntityController personEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode mockedJPerson;
	@Mock
	private List<Person> mockedPersonList;

	@MockBean
	private Person mockedPerson;
	
	/**
	 * @test create a {@link Person} (of type Consultant)
	 * @context as an admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link PersonEntityController}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createConsultant_as_AdminUser() throws ParseException {
		// setup
		Mockito.doReturn(true).when(this.consultantBusinessController)
				.isManager(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.personEntityController.createPerson(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any(PersonRole.class), ArgumentMatchers.anyString()))
				.thenReturn(this.mockedHttpException);
		// assert
		Assertions
				.assertThat(this.consultantBusinessController.createConsultant(this.mockedJPerson, UserRole.DEACTIVATED, ""))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Person} (of type Consultant)
	 * @context as an admin user
	 * @expected {@link ForbiddenException}
	 * @author Lucas Royackkers
	 */
	@Test
	public void createConsultant_as_NonAdminUserOrCdr() throws ParseException {
		// setup
		Mockito.doReturn(false).when(this.consultantBusinessController)
				.isManager(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions
				.assertThat(
						this.consultantBusinessController.createConsultant(this.mockedJPerson, UserRole.MANAGER_ADMIN, ""))
				.isInstanceOf(ForbiddenException.class);
	}

	/**
	 * @test delete a {@link Person} (of type Consultant)
	 * @context as an admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link PersonEntityController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteConsultant_as_AdminUser() {
		// setup
		Mockito.doReturn(true).when(this.consultantBusinessController)
				.isManager(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.personEntityController.deletePersonByRole(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any(PersonRole.class)))
				.thenReturn(this.mockedHttpException);
		// assert
		Assertions
				.assertThat(this.consultantBusinessController.deleteConsultant(this.mockedJPerson, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Person} (of type Consultant)
	 * @context as an admin user
	 * @expected {@link ForbiddenException}
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteConsultant_as_NonAdminUserOrCdr() {
		// setup
		Mockito.doReturn(false).when(this.consultantBusinessController)
				.isManager(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions
				.assertThat(
						this.consultantBusinessController.deleteConsultant(this.mockedJPerson, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	/**
	 * @test get a {@link Person} (of type Consultant)
	 * @context as a connected user (manager or cdr)
	 * @expected a {@link Person} that match the given filter (mail)
	 * @author Lucas Royackkers
	 */
	@Test
	public void getConsultant_as_ConnectedUser() {
		// setup
		Mockito.doReturn(true).when(this.consultantBusinessController).isConnected(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.personEntityController.getPersonByMailAndType(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(PersonRole.class))).thenReturn(this.mockedPerson);
		// assert
		Assertions.assertThat(this.consultantBusinessController.getConsultant("", UserRole.DEACTIVATED))
				.isEqualTo(this.mockedPerson);
	}

	/**
	 * @test get a {@link Person} (of type Consultant)
	 * @context as a non-connected user
	 * @expected {@link ForbiddenException}
	 * @author Lucas Royackkers
	 */
	@Test(expected = ForbiddenException.class)
	public void getConsultant_as_NonConnectedUser() {
		// setup
		Mockito.doReturn(false).when(this.consultantBusinessController)
				.isConnected(ArgumentMatchers.any(UserRole.class));
		// assert
		this.consultantBusinessController.getConsultant("", UserRole.MANAGER_ADMIN);
	}

	/**
	 * @test get all {@link Person} (of type Consultant)
	 * @context as a connected user
	 * @expected a List of all {@link Person}
	 * @author Lucas Royackkers
	 */
	@Test
	public void getConsultants_as_ConnectedUser() {
		// setup
		Mockito.doReturn(true).when(this.consultantBusinessController).isConnected(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.personEntityController.getPersonsByRole(ArgumentMatchers.any(PersonRole.class)))
				.thenReturn(this.mockedPersonList);
		// assert
		Assertions.assertThat(this.consultantBusinessController.getConsultants(UserRole.DEACTIVATED))
				.isEqualTo(this.mockedPersonList);
	}

	/**
	 * @test get all {@link Person} (of type Consultant)
	 * @context as a non-connected user
	 * @expected {@link ForbiddenException}
	 * @author Lucas Royackkers
	 */
	@Test(expected = ForbiddenException.class)
	public void getConsultants_as_NonConnectedUser() {
		// setup
		Mockito.doReturn(false).when(this.consultantBusinessController)
				.isConnected(ArgumentMatchers.any(UserRole.class));
		// assert
		this.consultantBusinessController.getConsultants(UserRole.MANAGER_ADMIN);
	}

	/**
	 * @test update a {@link Person} (of type Consultant)
	 * @context as an admin user (manager or cdr) or a manager
	 * @expected same {@link HttpException} returned by the
	 *           {@link PersonEntityController}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateConsultant_as_AdminUser() throws ParseException {
		// setup
		Mockito.doReturn(true).when(this.consultantBusinessController)
				.isManager(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.personEntityController.updatePerson(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any(PersonRole.class), ArgumentMatchers.anyString()))
				.thenReturn(this.mockedHttpException);
		// assert
		Assertions
				.assertThat(this.consultantBusinessController.updateConsultant(this.mockedJPerson, UserRole.DEACTIVATED, ""))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link Person} (of type Consultant)
	 * @context as a cdr or any other user (not an admin or manager)
	 * @expected {@link ForbiddenException}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateConsultant_as_NonAdminUser() throws ParseException {
		// setup
		Mockito.doReturn(false).when(this.consultantBusinessController)
				.isManager(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions
				.assertThat(
						this.consultantBusinessController.updateConsultant(this.mockedJPerson, UserRole.MANAGER_ADMIN, ""))
				.isInstanceOf(ForbiddenException.class);
	}
}
