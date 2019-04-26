package fr.alten.ambroiseJEE.model.entityControllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DuplicateKeyException;

import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.dao.CityRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * 
 * @author Kylian Gehier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CityEntityControllerTest {

	@InjectMocks
	@Spy
	private CityEntityController cityEntityController = new CityEntityController();

	@Mock
	private CityRepository cityRepository;
	@Mock
	private CreatedException mockedCreatedException;
	@Mock
	private ConflictException mockedConflictException;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	@Mock
	private JsonNode mockedJCity;
	@MockBean
	private City mockedCity;

	/**
	 * @test create a {@link City}
	 * @context sucess
	 * @expected {@link CreatedException}
	 * 			 save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_success() {

		// setup
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doReturn(mockedCity).when(cityRepository).save(any(City.class));
		// assert
		assertThat(cityEntityController.createCity(mockedJCity)).isInstanceOf(CreatedException.class);
		// verify
		verify(cityRepository, times(1)).save(any(City.class));
	}

	/**
	 * @test create a {@link City}
	 * @context {@link City} already existing in base
	 * @expected {@link ConflictException}
	 * 			 save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_conflict() {

		// setup
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doThrow(mockedDuplicateKeyException).when(cityRepository).save(any(City.class));
		// assert
		assertThat(cityEntityController.createCity(mockedJCity)).isInstanceOf(ConflictException.class);
		// verify
		verify(cityRepository, times(1)).save(any(City.class));
	}

	/**
	 * @test delete a {@link City}
	 * @context success
	 * @expected {@link OkException}
	 * 			 save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_success() {

		// setup
		Optional<City> notEmptyCityOptional = Optional.of(new City());
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doReturn("anyString").when(mockedJCity).textValue();
		doReturn(notEmptyCityOptional).when(cityRepository).findByNom(anyString());
		// assert
		assertThat(cityEntityController.deleteCity(mockedJCity)).isInstanceOf(OkException.class);
		// verify
		verify(cityRepository, times(1)).save(any(City.class));
	}

	/**
	 * @test delete a {@link City}
	 * @context {@link City} already existing in base
	 * @expected {@link ConflictException}
	 * 			 save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_Conflict() {

		// setup
		Optional<City> notEmptyCityOptional = Optional.of(new City());
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doReturn("anyString").when(mockedJCity).textValue();
		doReturn(notEmptyCityOptional).when(cityRepository).findByNom(anyString());
		when(cityRepository.save(any(City.class))).thenThrow(mockedDuplicateKeyException);
		// assert
		assertThat(cityEntityController.deleteCity(mockedJCity)).isInstanceOf(ConflictException.class);
		// verify
		verify(cityRepository, times(1)).save(any(City.class));
	}

	/**
	 * @test delete a {@link City}
	 * @context {@link City} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * 			 save() never called
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_resourceNotFound() {

		// setup
		Optional<City> emptyCityOptional = Optional.ofNullable(null);
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doReturn("anyString").when(mockedJCity).textValue();
		doReturn(emptyCityOptional).when(cityRepository).findByNom(anyString());
		// assert
		assertThat(cityEntityController.deleteCity(mockedJCity)).isInstanceOf(ResourceNotFoundException.class);
		// verify
		verify(cityRepository, never()).save(any(City.class));
	}

	/**
	 * @test get all {@link City}
	 * @expected return instance of {@link List}
	 * 			 save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void getCities() {

		// assert
		assertThat(cityEntityController.getCities()).isInstanceOf(List.class);
		// verify
		verify(cityRepository, times(1)).findAll();
	}

	/**
	 * @test get a {@link City} by name
	 * @context success
	 * @expected return instance of {@link City}
	 * @author Kylian Gehier
	 */
	@Test
	public void getCity_with_success() {

		// setup
		Optional<City> notEmptyCityOptional = Optional.of(new City());
		when(cityRepository.findByNom("name")).thenReturn(notEmptyCityOptional);
		// assert
		assertThat(cityEntityController.getCity("name")).isInstanceOf(City.class);
		// verify
		verify(cityRepository, times(1)).findByNom("name");
	}

	/**
	 * @test get a {@link City} by name
	 * @context {@link City} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Kylian Gehier
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getCity_with_ResourceNotFound() {

		// setup
		Optional<City> emptyCityOptional = Optional.ofNullable(null);
		when(cityRepository.findByNom("name")).thenReturn(emptyCityOptional);
		// throw
		cityEntityController.getCity("name");
	}

	/**
	 * @test update a {@link City}
	 * @context success
	 * @expected {@link OkException}
	 * 			 save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_success() {

		// setup
		Optional<City> notEmptyCityOptional = Optional.of(new City());
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doReturn("anyString").when(mockedJCity).textValue();
		doReturn(notEmptyCityOptional).when(cityRepository).findByNom(anyString());
		// assert
		assertThat(cityEntityController.updateCity(mockedJCity)).isInstanceOf(OkException.class);
		// verify
		verify(cityRepository, times(1)).save(any(City.class));
	}

	/**
	 * @test update a {@link City}
	 * @context {@link City} already existing in base
	 * @expected {@link ConflictException}
	 * 			 save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_Conflict() {

		// setup
		Optional<City> notEmptyCityOptional = Optional.of(new City());
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doReturn("anyString").when(mockedJCity).textValue();
		doReturn(notEmptyCityOptional).when(cityRepository).findByNom(anyString());
		when(cityRepository.save(any(City.class))).thenThrow(mockedDuplicateKeyException);
		// assert
		assertThat(cityEntityController.updateCity(mockedJCity)).isInstanceOf(ConflictException.class);
		// verify
		verify(cityRepository, times(1)).save(any(City.class));
	}

	/**
	 * @test update a {@link City}
	 * @context {@link City} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * 			 save() never called
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_resourceNotFound() {

		// setup
		Optional<City> emptyCityOptional = Optional.ofNullable(null);
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doReturn("anyString").when(mockedJCity).textValue();
		doReturn(emptyCityOptional).when(cityRepository).findByNom(anyString());
		// assert
		assertThat(cityEntityController.updateCity(mockedJCity)).isInstanceOf(ResourceNotFoundException.class);
		// verify
		verify(cityRepository, never()).save(any(City.class));
	}

}
