package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.controller.business.ConsultantBusinessController;
import fr.alten.ambroiseJEE.controller.business.EmployerBusinessController;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * 
 * 
 * @author Lucas Royackkers
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsultantRestControllerTest {
	@InjectMocks
	@Spy
	private final ConsultantRestController consultantRestController = new ConsultantRestController();

	@SpyBean
	private final Person spiedPerson = new Person();
	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private ConsultantBusinessController consultantBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * @test testing several Json for integrity when createConsultant
	 * @expected success when the JsonNode is fully filled, otherwise
	 *           {@link UnprocessableEntityException}
	 * @throws IOException
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createConsultant_checkJsonIntegrity() throws IOException, ParseException {
		// global Setup
		Mockito.doReturn(this.mockedHttpException).when(this.consultantBusinessController).createConsultant(
				ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class),
				ArgumentMatchers.anyString());

		// setup : all needed fields present
		final String valid = "{" + "\"mail\" : \"test@gmail.com\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);

		// assert all fields are present
		Assertions.assertThat(this.consultantRestController.createConsultant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing mail of the Person (Consultant)
		final String missingMail = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingMail);
		// assert missing mail
		Assertions.assertThat(this.consultantRestController.createConsultant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Person} (of type Consultant)
	 * @context {@link JsonNode} params containing field "mail"
	 * @expected same {@link HttpException} returned by the
	 *           {@link ConsultantBusinessController}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createConsultant_with_rightParam() throws ParseException {
		// setup
		Mockito.doReturn(true).when(this.consultantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		Mockito.when(this.consultantBusinessController.createConsultant(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any(UserRole.class), ArgumentMatchers.anyString()))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions
				.assertThat(
						this.consultantRestController.createConsultant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Person} (of type Consultant)
	 * @context {@link JsonNode} params not containing field "mail"
	 * @expected {@link UnprocessableEntityException}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createConsultant_with_wrongParam() throws ParseException {
		// setup
		Mockito.doReturn(false).when(this.consultantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions
				.assertThat(
						this.consultantRestController.createConsultant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test delete a {@link Person} (of type Consultant)
	 * @expected success when the Json is fully given, otherwise
	 *           {@link UnprocessableEntityException}
	 * @throws ParseException
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteConsultant_checkJsonIntegrity() throws ParseException, IOException {
		// global Setup
		Mockito.doReturn(this.mockedHttpException).when(this.consultantBusinessController)
				.deleteConsultant(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"mail\" : \"test@gmail.com\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);

		// assert all fields are present
		Assertions.assertThat(this.consultantRestController.deleteConsultant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing mail of the Person (Consultant)
		final String missingMail = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingMail);
		// assert missing mail
		Assertions.assertThat(this.consultantRestController.deleteConsultant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test delete a {@link Person} (of type Consultant)
	 * @context {@link JsonNode} params containing field "mail"
	 * @expected same {@link HttpException} returned by the
	 *           {@link ConsultantBusinessController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteConsultant_with_rightParam() {
		// setup
		Mockito.doReturn(true).when(this.consultantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		Mockito.when(this.consultantBusinessController.deleteConsultant(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any(UserRole.class))).thenReturn(this.mockedHttpException);

		// assert
		Assertions
				.assertThat(
						this.consultantRestController.deleteConsultant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Person} (of type Consultant)
	 * @context {@link JsonNode} params not containing field "mail"
	 * @expected {@link UnprocessableEntityException}
	 * 
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteConsultant_with_wrongParam() {
		// setup
		Mockito.doReturn(false).when(this.consultantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions
				.assertThat(
						this.consultantRestController.deleteConsultant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Person} (of type Consultant)
	 * @expected returning a String
	 * @author Lucas Royackkers
	 */
	@Test
	public void getConsultants_expectingString() {
		// setup
		Mockito.when(this.consultantBusinessController.getConsultants(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Person>());

		// assert
		Assertions.assertThat(this.consultantRestController.getConsultants("", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}
	
	/**
	 * @test get a specific {@link Person} (of type Consultant) given its mail
	 * @expected returning a String
	 * @author Lucas Royackkers
	 */
	@Test
	public void getApplicant_expectingString() {
		// setup
		Mockito.when(this.consultantBusinessController.getConsultant(ArgumentMatchers.anyString(),ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new Person());

		// assert
		Assertions.assertThat(this.consultantRestController.getConsultant("","", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateConsultant
	 * @expected success when the Json is fully given, otherwise
	 *           {@link UnprocessableEntityException}
	 * @throws IOException
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateConsultant_checkJsonIntegrity() throws IOException, ParseException {
		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.consultantBusinessController).updateConsultant(
				ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class),
				ArgumentMatchers.anyString());

		// setup : all needed fields present
		final String valid = "{" + "\"mail\":\"test@gmail.com\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.consultantRestController.updateConsultant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.consultantRestController.updateConsultant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test update a {@link Person} (of type Consultant)
	 * @context {@link JsonNode} params containing field "mail"
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerBusinessController}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateConsultant_with_rightParam() throws ParseException {
		// setup
		Mockito.doReturn(true).when(this.consultantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.consultantBusinessController.updateConsultant(this.mockedJsonNode, UserRole.CDR_ADMIN, "mail"))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions
				.assertThat(
						this.consultantRestController.updateConsultant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link Person} (of type Consultant)
	 * @context {@link JsonNode} params not containing field "mail"
	 * @expected {@link UnprocessableEntityException}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateConsultant_with_wrongParam() throws ParseException {
		// setup
		Mockito.doReturn(false).when(this.consultantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions
				.assertThat(
						this.consultantRestController.updateConsultant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}
}
