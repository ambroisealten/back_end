package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.controller.business.AgencyBusinessController;
import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Test class for AgencyRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AgencyRestControllerTest {

	@InjectMocks
	@Spy
	private final AgencyRestController agencyRestController = new AgencyRestController();

	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private AgencyBusinessController agencyBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * @test testing several Json for integrity when createAgency
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.agencyBusinessController)
				.createAgency(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"agency\"," + "\"place\":\"place\"," + "\"placeType\":\"city\""
				+ "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.agencyRestController.createAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingNom = "{" + "\"place\":\"place\"," + "\"placeType\":\"city\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing name
		Assertions.assertThat(this.agencyRestController.createAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing place
		final String missingPlace = "{" + "\"name\":\"agency\"," + "\"placeType\":\"city\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingPlace);
		// assert missing place
		Assertions.assertThat(this.agencyRestController.createAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing placeType
		final String missingPlaceType = "{" + "\"name\":\"agency\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingPlaceType);
		// assert missing placeType
		Assertions.assertThat(this.agencyRestController.createAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Agency}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link AgencyBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.agencyRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.agencyBusinessController.createAgency(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.agencyRestController.createAgency(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Agency}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.agencyRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.agencyRestController.createAgency(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deleteAgency
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.agencyBusinessController)
				.deleteAgency(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"name\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.agencyRestController.deleteAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.agencyRestController.deleteAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link Agency}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link AgencyBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.agencyRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.agencyBusinessController.deleteAgency(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.agencyRestController.deleteAgency(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Agency}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.agencyRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.agencyRestController.deleteAgency(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Agency}
	 * @expected returning a String
	 * @author Camille Schnell
	 */
	@Test
	public void getAgencies_expectingString() {

		// setup
		Mockito.when(this.agencyBusinessController.getAgencies(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Agency>());

		// assert
		Assertions.assertThat(this.agencyRestController.getAgencies("mail", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateAgency
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.agencyBusinessController)
				.updateAgency(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{\r\n" + "\"name\":\"name\",\r\n" + "\"oldName\":\"oldName\",\r\n"
				+ "\"place\":\"place\"," + "\"placeType\":\"city\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.agencyRestController.updateAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "\"oldName\":\"oldName\",\r\n" + "\"place\":\"place\","
				+ "\"placeType\":\"city\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.agencyRestController.updateAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing oldName
		final String missingOldName = "{\r\n" + "\"name\":\"name\",\r\n" + "\"place\":\"place\","
				+ "\"placeType\":\"city\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingOldName);
		// assert missing oldName
		Assertions.assertThat(this.agencyRestController.updateAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing place
		final String missingPlace = "{\r\n" + "\"name\":\"name\",\r\n" + "\"oldName\":\"oldName\",\r\n"
				+ "\"placeType\":\"city\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingPlace);
		// assert missing place
		Assertions.assertThat(this.agencyRestController.updateAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing placeType
		final String missingPlaceType = "{\r\n" + "\"name\":\"name\",\r\n" + "\"oldName\":\"oldName\",\r\n"
				+ "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingPlaceType);
		// assert missing placeType
		Assertions.assertThat(this.agencyRestController.updateAgency(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test update a {@link Agency}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link AgencyBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.agencyRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.agencyBusinessController.updateAgency(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.agencyRestController.updateAgency(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link Agency}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.agencyRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.agencyRestController.updateAgency(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}
}
