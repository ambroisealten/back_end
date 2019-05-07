package fr.alten.ambroiseJEE.controller.business;

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

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Sector;
import fr.alten.ambroiseJEE.model.entityControllers.SectorEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 *
 * @author Andy Chabalier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SectorBusinessControllerTest {

	@InjectMocks
	@Spy
	private final SectorBusinessController sectorBusinessController = new SectorBusinessController();

	@Mock
	private SectorEntityController sectorEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode mockedJSector;
	@Mock
	private List<Sector> mockedSectorsList;

	/**
	 * @test create a {@link Sector}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SectorEntityController}
	 * @author Andy Chabalier
	 */
	@Test
	public void createSector_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.sectorBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.sectorEntityController.createSector(this.mockedJSector)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.sectorBusinessController.createSector(this.mockedJSector, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Sector}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SectorEntityController}
	 * @author Andy Chabalier
	 */
	@Test
	public void createSector_as_nonAdminUser() {

		// assert
		Mockito.doReturn(false).when(this.sectorBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Assertions.assertThat(this.sectorBusinessController.createSector(this.mockedJSector, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test delete a {@link Sector}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SectorEntityController}
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteSector_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.sectorBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.sectorEntityController.deleteSector(this.mockedJSector)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.sectorBusinessController.deleteSector(this.mockedJSector, UserRole.MANAGER))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Sector}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SectorEntityController}
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteSector_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.sectorBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.sectorBusinessController.deleteSector(this.mockedJSector, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test create a {@link Sector}
	 * @expected same {@link Optional} returned by the
	 *           {@link SectorEntityController}
	 * @author Andy Chabalier
	 */
	@Test
	public void getSectors() {
		// setup
		Mockito.when(this.sectorEntityController.getSectors()).thenReturn(this.mockedSectorsList);
		// assert
		Assertions.assertThat(this.sectorBusinessController.getSectors(UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedSectorsList);
	}

	/**
	 * @test get all {@link Sector}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SectorEntityController}
	 * @author Andy Chabalier
	 */
	@Test
	public void getSectors_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.sectorBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.sectorEntityController.getSectors()).thenReturn(this.mockedSectorsList);
		// assert
		Assertions.assertThat(this.sectorBusinessController.getSectors(UserRole.DEACTIVATED))
				.isEqualTo(this.mockedSectorsList);
	}

	/**
	 * @test get all {@link Sector}
	 * @context as non admin user
	 * @expected throw a {@link ForbiddenException}
	 * @author Andy Chabalier
	 */
	@Test(expected = ForbiddenException.class)
	public void getSectors_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.sectorBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// throw
		this.sectorBusinessController.getSectors(UserRole.DEACTIVATED);

	}

	/**
	 * @test update a {@link Sector}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SectorEntityController}
	 * @author Andy Chabalier
	 */
	@Test
	public void updateSector_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.sectorBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.sectorEntityController.updateSector(this.mockedJSector)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.sectorBusinessController.updateSector(this.mockedJSector, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link Sector}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SectorEntityController}
	 * @author Andy Chabalier
	 */
	@Test
	public void updateSector_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.sectorBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.sectorBusinessController.updateSector(this.mockedJSector, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

}
