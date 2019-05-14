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

import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.entityControllers.PersonEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * 
 * @author Lucas Royackkers
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonBusinessControllerTest {
	
	@InjectMocks
	@Spy
	private final PersonBusinessController personBusinessController = new PersonBusinessController();

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
	 * @test get a {@link Person} (of any type) given its mail
	 * @context as a connected user
	 * @expected a {@link Person} that match the given filter (mail)
	 * @author Lucas Royackkers
	 */
	@Test
	public void getPerson_as_ConnectedUser() {
		// setup
		Mockito.doReturn(true).when(this.personBusinessController).isConnected(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.personEntityController.getPersonByMail(ArgumentMatchers.anyString()))
				.thenReturn(this.mockedPerson);
		// assert
		Assertions.assertThat(this.personBusinessController.getPerson("", UserRole.DEACTIVATED))
				.isEqualTo(this.mockedPerson);
	}
	
	/**
	 * @test get a {@link Person} (of any type) given its mail
	 * @context as a connected user, but without a matching person
	 * @expected a {@link Person} (empty object, without any properties)
	 * @author Lucas Royackkers
	 */
	@Test
	public void getPerson_as_ConnectedUser_With_rnfe() {
		// setup
		Mockito.doReturn(true).when(this.personBusinessController).isConnected(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.personEntityController.getPersonByMail(ArgumentMatchers.anyString()))
				.thenThrow(new ResourceNotFoundException());
		// assert
		Assertions.assertThat(this.personBusinessController.getPerson("", UserRole.DEACTIVATED))
				.isInstanceOf(Person.class);
	}
	
	/**
	 * @test get a {@link Person} (of any type) given its mail
	 * @context as a non-connected user
	 * @expected {@link ForbiddenException}
	 * @author Lucas Royackkers
	 */
	@Test(expected = ForbiddenException.class)
	public void getPerson_as_NonConnectedUser() {
		// setup
		Mockito.doReturn(false).when(this.personBusinessController).isConnected(ArgumentMatchers.any(UserRole.class));

		// assert
		this.personBusinessController.getPerson("", UserRole.DEACTIVATED);
	}
}
