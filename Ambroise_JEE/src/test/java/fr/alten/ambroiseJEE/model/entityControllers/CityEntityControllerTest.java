package fr.alten.ambroiseJEE.model.entityControllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import fr.alten.ambroiseJEE.model.dao.CityRepository;

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
	
}
