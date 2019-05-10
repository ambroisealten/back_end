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

import fr.alten.ambroiseJEE.controller.business.geographic.DepartementBusinessController;
import fr.alten.ambroiseJEE.model.beans.Departement;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Test class for DepartementRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DepartementRestControllerTest {
	@InjectMocks
	@Spy
	private final DepartementRestController departementRestController = new DepartementRestController();

	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private DepartementBusinessController departementBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * @test testing several Json for integrity when createDepartement
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.departementBusinessController)
				.createDepartement(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"nom\":\"departement\"," + "\"code\":\"code\"," + "\"codeRegion\":\"codeRegion\""
				+ "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions
				.assertThat(this.departementRestController.createDepartement(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing nom
		final String missingNom = "{" + "\"code\":\"code\"," + "\"codeRegion\":\"codeRegion\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing nom
		Assertions
				.assertThat(this.departementRestController.createDepartement(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCode = "{" + "\"codeRegion\":\"codeRegion\"," + "\"nom\":\"departement\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCode);
		// assert missing code
		Assertions
				.assertThat(this.departementRestController.createDepartement(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing regionCode
		final String missingregionCode = "{" + "\"code\":\"code\"," + "\"nom\":\"departement\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingregionCode);
		// assert missing regionCode
		Assertions
				.assertThat(this.departementRestController.createDepartement(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Departement}
	 * @context {@link JsonNode} params containing field "nom"
	 * @expected same {@link HttpException} returned by the
	 *           {@link DepartementBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.departementRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.departementBusinessController.createDepartement(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(
				this.departementRestController.createDepartement(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Departement}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.departementRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions.assertThat(
				this.departementRestController.createDepartement(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deleteDepartement
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.departementBusinessController)
				.deleteDepartement(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"code\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions
				.assertThat(this.departementRestController.deleteDepartement(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCode = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCode);
		// assert missing code
		Assertions
				.assertThat(this.departementRestController.deleteDepartement(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link Departement}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link DepartementBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.departementRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.departementBusinessController.deleteDepartement(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(
				this.departementRestController.deleteDepartement(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Departement}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.departementRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions.assertThat(
				this.departementRestController.deleteDepartement(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Departement}
	 * @expected returning a String
	 * @author Camille Schnell
	 */
	@Test
	public void getDepartements_expectingString() {

		// setup
		Mockito.when(this.departementBusinessController.getDepartements(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Departement>());

		// assert
		Assertions.assertThat(this.departementRestController.getDepartements("mail", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateDepartement
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.departementBusinessController)
				.updateDepartement(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{\r\n" + "  \"nom\":\"name\",\r\n" + "  \"code\":\"code\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions
				.assertThat(this.departementRestController.updateDepartement(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing nom
		final String missingNom = "{\r\n" + "  \"code\":\"code\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing nom
		Assertions
				.assertThat(this.departementRestController.updateDepartement(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCode = "{\r\n" + "  \"nom\":\"name\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCode);
		// assert missing name
		Assertions
				.assertThat(this.departementRestController.updateDepartement(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test update a {@link Departement}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link DepartementBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.departementRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.departementBusinessController.updateDepartement(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(
				this.departementRestController.updateDepartement(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link Departement}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.departementRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions.assertThat(
				this.departementRestController.updateDepartement(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}
}
