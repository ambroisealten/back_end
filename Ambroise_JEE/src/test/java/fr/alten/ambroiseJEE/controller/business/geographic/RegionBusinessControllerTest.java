package fr.alten.ambroiseJEE.controller.business.geographic;

import java.util.List;

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

import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.model.entityControllers.RegionEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Test class for RegionBusinessController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RegionBusinessControllerTest {
	@InjectMocks
	@Spy
	private final RegionBusinessController regionBusinessController = new RegionBusinessController();

	@Mock
	private RegionEntityController regionEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode mockedJRegion;
	@Mock
	private List<Region> mockedRegionList;

	@MockBean
	private Region mockedRegion;

	/**
	 * @test create a {@link Region}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link RegionEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.regionBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.regionEntityController.createRegion(this.mockedJRegion)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.regionBusinessController.createRegion(this.mockedJRegion, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Region}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link RegionEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_as_nonAdminUser() {

		// assert
		Mockito.doReturn(false).when(this.regionBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Assertions.assertThat(this.regionBusinessController.createRegion(this.mockedJRegion, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test delete a {@link Region}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link RegionEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.regionBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.regionEntityController.deleteRegion(this.mockedJRegion)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.regionBusinessController.deleteRegion(this.mockedJRegion, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Region}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link RegionEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.regionBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.regionBusinessController.deleteRegion(this.mockedJRegion, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

	/**
	 * @test update a {@link Region}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link RegionEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_as_adminUser() {

		// setup
		Mockito.doReturn(true).when(this.regionBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.regionEntityController.updateRegion(this.mockedJRegion)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.regionBusinessController.updateRegion(this.mockedJRegion, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link Region}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link RegionEntityController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.regionBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.regionBusinessController.updateRegion(this.mockedJRegion, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}

}
