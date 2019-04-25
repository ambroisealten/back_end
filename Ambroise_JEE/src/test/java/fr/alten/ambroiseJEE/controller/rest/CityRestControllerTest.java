package fr.alten.ambroiseJEE.controller.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.controller.business.geographic.CityBusinessController;
import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
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
	private final CityRestController cityRestController = new CityRestController();;

	@Mock
	private CityBusinessController cityBusinessController;
	@Mock
	private JsonNode mockedjsonNode;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonUtils test;
	
	/**
	 * @test create a {@link City}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the {@link CityBusinessController}
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_rightParam() {

		// setup
		doReturn(true).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), anyString());
		when(cityBusinessController.createCity(mockedjsonNode, UserRole.CDR_ADMIN)).thenReturn(mockedHttpException);

		// assert
		assertThat(cityRestController.createCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
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
		doReturn(false).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), anyString());

		// assert
		assertThat(cityRestController.createCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
			.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test delete a {@link City}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the {@link CityBusinessController}
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_rightParam() {

		// setup
		doReturn(true).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), anyString());
		when(cityBusinessController.deleteCity(mockedjsonNode, UserRole.CDR_ADMIN)).thenReturn(mockedHttpException);

		// assert
		assertThat(cityRestController.deleteCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
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
		doReturn(false).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), anyString());

		// assert
		assertThat(cityRestController.deleteCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
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
		when(cityBusinessController.getCities(any(UserRole.class)))
			.thenReturn(new ArrayList<City>());

		// assert
		assertThat(cityRestController.getCities("mail", UserRole.CDR_ADMIN))
			.isInstanceOf(String.class);
	}

	
	/**
	 * @test update a {@link City}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the {@link CityBusinessController}
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_rightParam() {

		// setup
		doReturn(true).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), anyString());
		when(cityBusinessController.updateCity(mockedjsonNode, UserRole.CDR_ADMIN))
			.thenReturn(mockedHttpException);

		// assert
		assertThat(cityRestController.updateCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
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
		doReturn(false).when(cityRestController).checkJsonIntegrity((any(JsonNode.class)), anyString());

		// assert
		assertThat(cityRestController.updateCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
			.isInstanceOf(UnprocessableEntityException.class);

	}
}
