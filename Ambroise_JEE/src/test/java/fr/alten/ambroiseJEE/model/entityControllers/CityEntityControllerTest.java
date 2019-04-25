package fr.alten.ambroiseJEE.model.entityControllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;

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
	private JsonNode mockedJCity;
	
	@Test
	public void createCity_with_Conflict() {
		
		// setup
		doThrow(mockedCreatedException).when(cityRepository).save(any(City.class));
		// assert
		assertThat(cityEntityController.createCity(mockedJCity))
			.isInstanceOf(ConflictException.class);
		
	}
	
}
