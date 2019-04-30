package fr.alten.ambroiseJEE.model.entityControllers;

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
import org.springframework.dao.DuplicateKeyException;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.dao.CityRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
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
	private final CityEntityController cityEntityController = new CityEntityController();

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
	 * @context {@link City} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_conflict() {

		// setup
		Mockito.doReturn(this.mockedJCity).when(this.mockedJCity).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.cityRepository)
				.save(ArgumentMatchers.any(City.class));
		// assert
		Assertions.assertThat(this.cityEntityController.createCity(this.mockedJCity))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.times(1)).save(ArgumentMatchers.any(City.class));
	}

	/**
	 * @test create a {@link City}
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void createCity_with_success() {

		// setup
		Mockito.doReturn(this.mockedJCity).when(this.mockedJCity).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedCity).when(this.cityRepository).save(ArgumentMatchers.any(City.class));
		// assert
		Assertions.assertThat(this.cityEntityController.createCity(this.mockedJCity))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.times(1)).save(ArgumentMatchers.any(City.class));
	}

	/**
	 * @test delete a {@link City}
	 * @context {@link City} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_Conflict() {

		// setup
		final Optional<City> notEmptyCityOptional = Optional.of(new City());
		Mockito.doReturn(this.mockedJCity).when(this.mockedJCity).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJCity).textValue();
		Mockito.doReturn(notEmptyCityOptional).when(this.cityRepository).findByNom(ArgumentMatchers.anyString());
		Mockito.when(this.cityRepository.save(ArgumentMatchers.any(City.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.cityEntityController.deleteCity(this.mockedJCity))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.times(1)).save(ArgumentMatchers.any(City.class));
	}

	/**
	 * @test delete a {@link City}
	 * @context {@link City} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_resourceNotFound() {

		// setup
		final Optional<City> emptyCityOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJCity).when(this.mockedJCity).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJCity).textValue();
		Mockito.doReturn(emptyCityOptional).when(this.cityRepository).findByNom(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.cityEntityController.deleteCity(this.mockedJCity))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.never()).save(ArgumentMatchers.any(City.class));
	}

	/**
	 * @test delete a {@link City}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void deleteCity_with_success() {

		// setup
		final Optional<City> notEmptyCityOptional = Optional.of(new City());
		Mockito.doReturn(this.mockedJCity).when(this.mockedJCity).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJCity).textValue();
		Mockito.doReturn(notEmptyCityOptional).when(this.cityRepository).findByNom(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.cityEntityController.deleteCity(this.mockedJCity)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.times(1)).save(ArgumentMatchers.any(City.class));
	}

	/**
	 * @test get all {@link City}
	 * @expected return instance of {@link List} save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void getCities() {

		// assert
		Assertions.assertThat(this.cityEntityController.getCities()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.times(1)).findAll();
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
		final Optional<City> emptyCityOptional = Optional.ofNullable(null);
		Mockito.when(this.cityRepository.findByNom("name")).thenReturn(emptyCityOptional);
		// throw
		this.cityEntityController.getCity("name");
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
		final Optional<City> notEmptyCityOptional = Optional.of(new City());
		Mockito.when(this.cityRepository.findByNom("name")).thenReturn(notEmptyCityOptional);
		// assert
		Assertions.assertThat(this.cityEntityController.getCity("name")).isInstanceOf(City.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.times(1)).findByNom("name");
	}

	/**
	 * @test update a {@link City}
	 * @context {@link City} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_Conflict() {

		// setup
		final Optional<City> notEmptyCityOptional = Optional.of(new City());
		Mockito.doReturn(this.mockedJCity).when(this.mockedJCity).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJCity).textValue();
		Mockito.doReturn(notEmptyCityOptional).when(this.cityRepository).findByNom(ArgumentMatchers.anyString());
		Mockito.when(this.cityRepository.save(ArgumentMatchers.any(City.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.cityEntityController.updateCity(this.mockedJCity))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.times(1)).save(ArgumentMatchers.any(City.class));
	}

	/**
	 * @test update a {@link City}
	 * @context {@link City} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_resourceNotFound() {

		// setup
		final Optional<City> emptyCityOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJCity).when(this.mockedJCity).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJCity).textValue();
		Mockito.doReturn(emptyCityOptional).when(this.cityRepository).findByNom(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.cityEntityController.updateCity(this.mockedJCity))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.never()).save(ArgumentMatchers.any(City.class));
	}

	/**
	 * @test update a {@link City}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Kylian Gehier
	 */
	@Test
	public void updateCity_with_success() {

		// setup
		final Optional<City> notEmptyCityOptional = Optional.of(new City());
		Mockito.doReturn(this.mockedJCity).when(this.mockedJCity).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJCity).textValue();
		Mockito.doReturn(notEmptyCityOptional).when(this.cityRepository).findByNom(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.cityEntityController.updateCity(this.mockedJCity)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.cityRepository, Mockito.times(1)).save(ArgumentMatchers.any(City.class));
	}

}
