package fr.alten.ambroiseJEE.controller.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
	private City spiedCity = new City();
	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private CityBusinessController cityBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private ObjectMapper mapper = new ObjectMapper();

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
		doReturn(true).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), any());
		when(cityBusinessController.createCity(mockedJsonNode, UserRole.CDR_ADMIN)).thenReturn(mockedHttpException);

		// assert
		assertThat(cityRestController.createCity(mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(mockedHttpException);
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
		doReturn(false).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), any());

		// assert
		assertThat(cityRestController.createCity(mockedJsonNode, "mail", UserRole.CDR_ADMIN))
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
		doReturn(true).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), any());
		when(cityBusinessController.deleteCity(mockedJsonNode, UserRole.CDR_ADMIN)).thenReturn(mockedHttpException);

		// assert
		assertThat(cityRestController.deleteCity(mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(mockedHttpException);
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
		doReturn(false).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), any());

		// assert
		assertThat(cityRestController.deleteCity(mockedJsonNode, "mail", UserRole.CDR_ADMIN))
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
		when(cityBusinessController.getCities(any(UserRole.class))).thenReturn(new ArrayList<City>());

		// assert
		assertThat(cityRestController.getCities("mail", UserRole.CDR_ADMIN)).isInstanceOf(String.class);
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
		doReturn(true).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), any());
		when(cityBusinessController.updateCity(mockedJsonNode, UserRole.CDR_ADMIN)).thenReturn(mockedHttpException);

		// assert
		assertThat(cityRestController.updateCity(mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(mockedHttpException);

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
		doReturn(false).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), any());

		// assert
		assertThat(cityRestController.updateCity(mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test testing several Json for integrity when createCity
	 * @expected sucess for all json test cases
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_checkJsonIntegrity() throws IOException {

		// globalSetup
		doReturn(mockedHttpException).when(cityBusinessController).createCity(any(JsonNode.class), any(UserRole.class));

		// setup : all needed fields present
		String valid = "{\r\n" + "  \"nom\":\"name\",\r\n" + "  \"code\":\"code\"\r\n" + "}";

		spiedJsonNode = mapper.readTree(valid);
		// assert all field present
		assertThat(cityRestController.createCity(spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		String missingName = "{\r\n" + "  \"code\":\"code\"\r\n" + "}";

		spiedJsonNode = mapper.readTree(missingName);
		// assert missing name
		assertThat(cityRestController.createCity(spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		String missingCode = "{\r\n" + "  \"nom\":\"name\"\r\n" + "}";

		spiedJsonNode = mapper.readTree(missingCode);
		// assert missing code
		assertThat(cityRestController.createCity(spiedJsonNode, "mail", UserRole.CDR))
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
		doReturn(mockedHttpException).when(cityBusinessController).deleteCity(any(JsonNode.class), any(UserRole.class));

		// setup : all needed fields present
		String valid = "{\r\n" + "  \"nom\":\"name\"\r\n" + "}";

		spiedJsonNode = mapper.readTree(valid);
		// assert all field present
		assertThat(cityRestController.deleteCity(spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		String missingName = "{}";

		spiedJsonNode = mapper.readTree(missingName);
		// assert missing name
		assertThat(cityRestController.deleteCity(spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test testing several Json for integrity when updateCity
	 * @expected sucess for all json test cases
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_checkJsonIntegrity() throws IOException {

		// globalSetup
		doReturn(mockedHttpException).when(cityBusinessController).updateCity(any(JsonNode.class), any(UserRole.class));

		// setup : all needed fields present
		String valid = "{\r\n" + "  \"nom\":\"name\",\r\n" + "  \"oldname\":\"oldname\"\r\n" + "}";

		spiedJsonNode = mapper.readTree(valid);
		// assert all field present
		assertThat(cityRestController.updateCity(spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		String missingName = "{\r\n" + "  \"oldName\":\"oldname\"\r\n" + "}";

		spiedJsonNode = mapper.readTree(missingName);
		// assert missing name
		assertThat(cityRestController.updateCity(spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		String missingOldName = "{\r\n" + "  \"nom\":\"name\"\r\n" + "}";

		spiedJsonNode = mapper.readTree(missingOldName);
		// assert missing name
		assertThat(cityRestController.updateCity(spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

}
