package fr.alten.ambroiseJEE.controller.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.JsonNode;

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
	private CityRestController cityRestController;

	@Mock
	private CityBusinessController cityBusinessController;
	@Mock
	private JsonNode mockedjsonNode;
	@Mock
	private HttpException mockedHttpException;

	@MockBean
	private City mockedCity;

	/*private ObjectMapper mapper = new ObjectMapper();
	private JsonNode jCity;
	private City city;*/

	/*@Before
	public void setup() {

	}*/

	/*public void setupCityForNode() {
		this.city = new City();
		city.setCode("code");
		city.setName("name");
		city.setCodeDepartement("codeDepartement");
		;
		city.setCodePostaux("codePostaux");
		city.setCodeRegion("codeRegion");
	}*/

	/*public void setupJsonNode() {

	}*/

	@Test
	public void createCity_with_rightParam() {

		// setup
		doReturn(mockedjsonNode).when(mockedjsonNode).get("name");
		when(cityBusinessController.createCity(mockedjsonNode, UserRole.CDR_ADMIN)).thenReturn(mockedHttpException);

		// assert
		assertThat(cityRestController.createCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(HttpException.class);
	}

	@Test
	public void createCity_with_wrongParam() {

		// setup
		doReturn(null).when(mockedjsonNode).get("name");

		// assert
		assertThat(cityRestController.createCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void deleteCity_with_rightParam() {

		// setup
		doReturn(mockedjsonNode).when(mockedjsonNode).get("name");
		when(cityBusinessController.deleteCity(mockedjsonNode, UserRole.CDR_ADMIN)).thenReturn(mockedHttpException);

		// assert
		assertThat(cityRestController.deleteCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(HttpException.class);
	}
	
	@Test
	public void deleteCity_with_wrongParam() {

		// setup
		doReturn(null).when(mockedjsonNode).get("name");

		// assert
		assertThat(cityRestController.deleteCity(mockedjsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}
}
