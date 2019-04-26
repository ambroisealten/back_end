package fr.alten.ambroiseJEE.controller.business.geographic;

import java.util.List;
import java.util.Optional;

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

import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.entityControllers.CityEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 *
 * @author Kylian Gehier
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class CityBusinessControllerTest {

	@InjectMocks
	@Spy
	private final CityBusinessController cityBusinessController = new CityBusinessController();

	@Mock
	private CityEntityController cityEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode mockedJCity;
	@Mock
	private List<City> mockedCityList;

	@MockBean
	private City mockedCity;

	/**
	 * @test create a {@link City}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.cityBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.cityEntityController.createCity(this.mockedJCity)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.cityBusinessController.createCity(this.mockedJCity, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link City}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_as_nonAdminUser() {

		// assert
		Mockito.doReturn(false).when(this.cityBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Assertions.assertThat(this.cityBusinessController.createCity(this.mockedJCity, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test delete a {@link City}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.cityBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.cityEntityController.deleteCity(this.mockedJCity)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.cityBusinessController.deleteCity(this.mockedJCity, UserRole.MANAGER))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link City}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.cityBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.cityBusinessController.deleteCity(this.mockedJCity, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test get all {@link City}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void getCities_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.cityBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.cityEntityController.getCities()).thenReturn(this.mockedCityList);
		// assert
		Assertions.assertThat(this.cityBusinessController.getCities(UserRole.DEACTIVATED))
				.isEqualTo(this.mockedCityList);
	}

	/**
	 * @test get all {@link City}
	 * @context as non admin user
	 * @expected throw a {@link ForbiddenException}
	 * @author Kylian Gehier
	 */
	@Test(expected = ForbiddenException.class)
	public void getCities_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.cityBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// throw
		this.cityBusinessController.getCities(UserRole.MANAGER_ADMIN);

	}

	/**
	 * @test create a {@link City}
	 * @expected same {@link Optional} returned by the {@link CityEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void getCity() {
		// setup
		Mockito.when(this.cityEntityController.getCity("name")).thenReturn(this.mockedCity);
		// assert
		Assertions.assertThat(this.cityBusinessController.getCity("name")).isEqualTo(this.mockedCity);
	}

	/**
	 * @test update a {@link City}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.cityBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.cityEntityController.updateCity(this.mockedJCity)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.cityBusinessController.updateCity(this.mockedJCity, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link City}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link CityEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.cityBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.cityBusinessController.updateCity(this.mockedJCity, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

}
