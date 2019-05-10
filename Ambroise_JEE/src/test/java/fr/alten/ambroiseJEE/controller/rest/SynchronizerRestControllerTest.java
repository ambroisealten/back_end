package fr.alten.ambroiseJEE.controller.rest;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import fr.alten.ambroiseJEE.controller.business.geographic.CityBusinessController;
import fr.alten.ambroiseJEE.controller.business.geographic.DepartementBusinessController;
import fr.alten.ambroiseJEE.controller.business.geographic.GeographicBusinessController;
import fr.alten.ambroiseJEE.controller.business.geographic.PostalCodeBusinessController;
import fr.alten.ambroiseJEE.controller.business.geographic.RegionBusinessController;
import fr.alten.ambroiseJEE.model.dao.CityRepository;
import fr.alten.ambroiseJEE.model.dao.DepartementRepository;
import fr.alten.ambroiseJEE.model.dao.PostalCodeRepository;
import fr.alten.ambroiseJEE.model.dao.RegionRepository;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 *
 * @author Andy Chabalier
 *
 */
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class SynchronizerRestControllerTest {

	@InjectMocks
	@Spy
	private final GeographicBusinessController geographicBusinessController = new GeographicBusinessController();

	@Mock
	private HttpException mockedHttpException;

	@Mock
	private CityBusinessController cityBusinessController;

	@Mock
	private RegionBusinessController regionBusinessController;

	@Mock
	private DepartementBusinessController departementBusinessController;

	@Mock
	private PostalCodeBusinessController postalCodeBusinessController;

	@Mock
	private CityRepository cityRepository;

	@Mock
	private RegionRepository regionRepository;

	@Mock
	private DepartementRepository departementRepository;

	@Mock
	private PostalCodeRepository postalCodeRepository;

	@After
	public void afterEachTest() {
		cityRepository.deleteAll();
		regionRepository.deleteAll();
		departementRepository.deleteAll();
		postalCodeRepository.deleteAll();
	}

	/**
	 * @test synchronize
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link GeographicBusinessController}
	 * @author Andy Chabalier
	 */
	@Test
	public void synchronize() {

		// setup
		Mockito.doReturn(this.mockedHttpException).when(this.geographicBusinessController)
				.synchronize(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.geographicBusinessController.synchronize(UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}
}
