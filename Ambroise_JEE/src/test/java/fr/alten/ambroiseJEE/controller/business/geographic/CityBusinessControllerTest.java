package fr.alten.ambroiseJEE.controller.business.geographic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.entityControllers.CityEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Kylian Gehier
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class CityBusinessControllerTest {

	@InjectMocks
	@Spy
	private CityBusinessController cityBusinessController = new CityBusinessController();

	@Mock
	private CityEntityController cityEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode mockedJCity;
	@Mock
	private List<City> mockedCityList;

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
		doReturn(true).when(cityBusinessController).isAdmin(any(UserRole.class));
		when(cityEntityController.createCity(mockedJCity)).thenReturn(mockedHttpException);
		// assert
		assertThat(cityBusinessController.createCity(mockedJCity, UserRole.DEACTIVATED))
				.isEqualTo(mockedHttpException);
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
		doReturn(false).when(cityBusinessController).isAdmin(any(UserRole.class));
		assertThat(cityBusinessController.createCity(mockedJCity, UserRole.MANAGER_ADMIN))
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
		doReturn(true).when(cityBusinessController).isAdmin(any(UserRole.class));
		when(cityEntityController.deleteCity(mockedJCity)).thenReturn(mockedHttpException);
		// assert
		assertThat(cityBusinessController.deleteCity(mockedJCity, UserRole.MANAGER))
				.isEqualTo(mockedHttpException);
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
		doReturn(false).when(cityBusinessController).isAdmin(any(UserRole.class));
		// assert
		assertThat(cityBusinessController.deleteCity(mockedJCity, UserRole.MANAGER_ADMIN))
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
		doReturn(true).when(cityBusinessController).isAdmin(any(UserRole.class));
		when(cityEntityController.getCities()).thenReturn(mockedCityList);
		// assert
		assertThat(cityBusinessController.getCities(UserRole.DEACTIVATED)).isEqualTo(mockedCityList);
	}

	@Test(expected = ForbiddenException.class)
	public void getCities_as_nonAdminUser() {

		// setup
		doReturn(false).when(cityBusinessController).isAdmin(any(UserRole.class));
		// throw
		cityBusinessController.getCities(UserRole.MANAGER_ADMIN);

	}

	/**
	 * @test create a {@link City}
	 * @expected same {@link Optional} returned by the {@link CityEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void getCity() {
		// setup
		Optional<City> cityOptional = Optional.of(new City());
		when(cityEntityController.getCity("name")).thenReturn(cityOptional);
		// assert
		assertThat(cityBusinessController.getCity("name")).isEqualTo(cityOptional);
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
		doReturn(true).when(cityBusinessController).isAdmin(any(UserRole.class));
		when(cityEntityController.updateCity(mockedJCity)).thenReturn(mockedHttpException);
		// assert
		assertThat(cityBusinessController.updateCity(mockedJCity, UserRole.DEACTIVATED))
				.isEqualTo(mockedHttpException);
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

		//setup
		doReturn(false).when(cityBusinessController).isAdmin(any(UserRole.class));
		// assert
		assertThat(cityBusinessController.updateCity(mockedJCity, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

}
