package fr.alten.ambroiseJEE.model.entityControllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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

	@Test
	public void createCity_with_conflict() {

		// setup
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doThrow(mockedDuplicateKeyException).when(cityRepository).save(any(City.class));
		// assert
		assertThat(cityEntityController.createCity(mockedJCity)).isInstanceOf(ConflictException.class);
		// verify
		verify(cityRepository).save(any(City.class));
	}

	@Test
	public void createCity_with_success() {

		// setup
		doReturn(mockedJCity).when(mockedJCity).get(anyString());
		doReturn(mockedCity).when(cityRepository).save(any(City.class));
		// assert
		assertThat(cityEntityController.createCity(mockedJCity)).isInstanceOf(CreatedException.class);
		// verify
		verify(cityRepository).save(any(City.class));
	}

}
