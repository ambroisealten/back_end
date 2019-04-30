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

import fr.alten.ambroiseJEE.controller.business.geographic.CityBusinessController;
import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 *
 * @author Kylian Gehier
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class CityRestControllerTest {

	@InjectMocks
	@Spy
	private final CityRestController cityRestController = new CityRestController();

	@SpyBean
	private final City spiedCity = new City();
	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private CityBusinessController cityBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * @test testing several Json for integrity when createCity
	 * @expected sucess for all json test cases
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.cityBusinessController)
				.createCity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"nom\":\"city\"," + "\"code\":\"code\"," + "\"codeRegion\":\"code\","
				+ "\"codeDepartement\":\"code\"," + "\"codesPostaux\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.cityRestController.createCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing nom
		final String missingNom = "{" + "\"code\":\"code\"," + "\"codeRegion\":\"code\","
				+ "\"codeDepartement\":\"code\"," + "\"codesPostaux\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing nom
		Assertions.assertThat(this.cityRestController.createCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCode = "{" + "\"nom\":\"city\"," + "\"codeRegion\":\"code\","
				+ "\"codeDepartement\":\"code\"," + "\"codesPostaux\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCode);
		// assert missing code
		Assertions.assertThat(this.cityRestController.createCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCodeRegion = "{" + "\"nom\":\"city\"," + "\"code\":\"code\","
				+ "\"codeDepartement\":\"code\"," + "\"codesPostaux\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCodeRegion);
		// assert missing code
		Assertions.assertThat(this.cityRestController.createCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCodeDepartement = "{" + "\"nom\":\"city\"," + "\"code\":\"code\","
				+ "\"codeRegion\":\"code\"," + "\"codesPostaux\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCodeDepartement);
		// assert missing code
		Assertions.assertThat(this.cityRestController.createCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCodesPostaux = "{" + "\"nom\":\"city\"," + "\"code\":\"code\"," + "\"codeRegion\":\"code\","
				+ "\"codeDepartement\":\"code\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCodesPostaux);
		// assert missing code
		Assertions.assertThat(this.cityRestController.createCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link City}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityBusinessController}
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.cityRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.cityBusinessController.createCity(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.cityRestController.createCity(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link City}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.cityRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.cityRestController.createCity(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deleteCity
	 * @expected sucess for all json test cases
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.cityBusinessController)
				.deleteCity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{\r\n" + "  \"nom\":\"name\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.cityRestController.deleteCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.cityRestController.deleteCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link City}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityBusinessController}
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.cityRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.cityBusinessController.deleteCity(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.cityRestController.deleteCity(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link City}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.cityRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.cityRestController.deleteCity(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link City}
	 * @expected returning a String
	 * @author Kylian Gehier
	 */
	@Test
	public void getCities_expectingString() {

		// setup
		Mockito.when(this.cityBusinessController.getCities(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<City>());

		// assert
		Assertions.assertThat(this.cityRestController.getCities("mail", UserRole.CDR_ADMIN)).isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateCity
	 * @expected sucess for all json test cases
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.cityBusinessController)
				.updateCity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{\r\n" + "  \"nom\":\"name\",\r\n" + "  \"oldname\":\"oldname\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.cityRestController.updateCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{\r\n" + "  \"oldName\":\"oldname\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.cityRestController.updateCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingOldName = "{\r\n" + "  \"nom\":\"name\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingOldName);
		// assert missing name
		Assertions.assertThat(this.cityRestController.updateCity(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test update a {@link City}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityBusinessController}
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.cityRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.cityBusinessController.updateCity(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.cityRestController.updateCity(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link City}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.cityRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.cityRestController.updateCity(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}

}
