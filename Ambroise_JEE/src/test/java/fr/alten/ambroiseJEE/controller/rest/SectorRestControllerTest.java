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

import fr.alten.ambroiseJEE.controller.business.SectorBusinessController;
import fr.alten.ambroiseJEE.model.beans.Sector;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 *
 * @author Andy Chabalier
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SectorRestControllerTest {

	@InjectMocks
	@Spy
	private final SectorRestController sectorRestController = new SectorRestController();

	@Mock
	private SectorBusinessController sectorBusinessController;

	@Mock
	private HttpException mockedHttpException;

	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private JsonNode mockedJsonNode;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * @test testing several Json for integrity when createSector
	 * @expected sucess for all json test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void createSector_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.sectorBusinessController)
				.createSector(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"name\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.sectorRestController.createSector(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingNom = "{}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing name
		Assertions.assertThat(this.sectorRestController.createSector(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Sector}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link SectorBusinessController}
	 * @author Andy Chabalier.
	 */
	@Test
	public void createSector_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.sectorRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.sectorBusinessController.createSector(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.sectorRestController.createSector(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Sector}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Andy Chabalier
	 */
	@Test
	public void createSector_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.sectorRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.sectorRestController.createSector(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deleteSector
	 * @expected sucess for all json test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteSector_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.sectorBusinessController)
				.deleteSector(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "  \"name\":\"name\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.sectorRestController.deleteSector(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.sectorRestController.deleteSector(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link Sector}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link sectorBusinessController}
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteSector_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.sectorRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.sectorBusinessController.deleteSector(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.sectorRestController.deleteSector(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Sector}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteSector_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.sectorRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.sectorRestController.deleteSector(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Sector}
	 * @expected returning a String
	 * @author Andy Chabalier
	 */
	@Test
	public void getSectors_expectingString() {

		// setup
		Mockito.when(this.sectorBusinessController.getSectors(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Sector>());

		// assert
		Assertions.assertThat(this.sectorRestController.getSectors("mail", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateSector
	 * @expected sucess for all json test cases
	 * @author Andy Chabalier
	 */
	@Test
	public void updateSector_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.sectorBusinessController)
				.updateSector(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{\r\n" + "  \"name\":\"name\",\r\n" + "  \"oldName\":\"oldName\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.sectorRestController.updateSector(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{\r\n" + "  \"oldName\":\"oldName\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.sectorRestController.updateSector(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingOldName = "{\r\n" + "  \"name\":\"name\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingOldName);
		// assert missing name
		Assertions.assertThat(this.sectorRestController.updateSector(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test update a {@link Sector}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link SectorBusinessController}
	 * @author Andy Chabalier
	 */
	@Test
	public void updateSector_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.sectorRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.sectorBusinessController.updateSector(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.sectorRestController.updateSector(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link Sector}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Andy Chabalier
	 */
	@Test
	public void updateSector_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.sectorRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.sectorRestController.updateSector(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}
}
