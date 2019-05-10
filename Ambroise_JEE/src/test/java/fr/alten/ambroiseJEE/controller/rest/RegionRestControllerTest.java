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
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.controller.business.geographic.RegionBusinessController;
import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Test class for RegionRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RegionRestControllerTest {

	@InjectMocks
	@Spy
	private final RegionRestController regionRestController = new RegionRestController();

	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private RegionBusinessController regionBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * @test testing several Json for integrity when createRegion
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.regionBusinessController)
				.createRegion(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"nom\":\"region\"," + "\"code\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.regionRestController.createRegion(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing nom
		final String missingNom = "{" + "\"code\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing nom
		Assertions.assertThat(this.regionRestController.createRegion(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCode = "{" + "\"nom\":\"region\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCode);
		// assert missing code
		Assertions.assertThat(this.regionRestController.createRegion(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Region}
	 * @context {@link JsonNode} params containing field "nom"
	 * @expected same {@link HttpException} returned by the
	 *           {@link RegionBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.regionRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.regionBusinessController.createRegion(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.regionRestController.createRegion(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Region}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.regionRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.regionRestController.createRegion(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deleteRegion
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.regionBusinessController)
				.deleteRegion(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"code\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.regionRestController.deleteRegion(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCode = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCode);
		// assert missing code
		Assertions.assertThat(this.regionRestController.deleteRegion(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link Region}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link RegionBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.regionRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.regionBusinessController.deleteRegion(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.regionRestController.deleteRegion(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Region}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.regionRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.regionRestController.deleteRegion(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Region}
	 * @expected returning a String
	 * @author Camille Schnell
	 */
	@Test
	public void getRegions_expectingString() {

		// setup
		Mockito.when(this.regionBusinessController.getRegions(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Region>());

		// assert
		Assertions.assertThat(this.regionRestController.getRegions("mail", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateRegion
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.regionBusinessController)
				.updateRegion(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{\r\n" + "  \"nom\":\"name\",\r\n" + "  \"code\":\"code\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.regionRestController.updateRegion(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing nom
		final String missingNom = "{\r\n" + "  \"code\":\"code\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing nom
		Assertions.assertThat(this.regionRestController.updateRegion(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCode = "{\r\n" + "  \"nom\":\"name\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCode);
		// assert missing name
		Assertions.assertThat(this.regionRestController.updateRegion(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test update a {@link Region}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link RegionBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.regionRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.regionBusinessController.updateRegion(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.regionRestController.updateRegion(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link Region}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.regionRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.regionRestController.updateRegion(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}
}
