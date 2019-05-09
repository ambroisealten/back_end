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

import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.model.dao.RegionRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Test class for RegionEntityController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RegionEntityControllerTest {

	@InjectMocks
	@Spy
	private final RegionEntityController regionEntityController = new RegionEntityController();

	@Mock
	private RegionRepository regionRepository;
	@Mock
	private CreatedException mockedCreatedException;
	@Mock
	private ConflictException mockedConflictException;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	@Mock
	private JsonNode mockedJRegion;
	@MockBean
	private Region mockedRegion;

	/**
	 * @test create a {@link Region}
	 * @context {@link Region} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_with_conflict() {
		// setup
		Mockito.doReturn(this.mockedJRegion).when(this.mockedJRegion).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.regionRepository)
				.save(ArgumentMatchers.any(Region.class));
		// assert
		Assertions.assertThat(this.regionEntityController.createRegion(this.mockedJRegion))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.times(1)).save(ArgumentMatchers.any(Region.class));
	}

	/**
	 * @test create a {@link Region}
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void createRegion_with_success() {

		// setup
		Mockito.doReturn(this.mockedJRegion).when(this.mockedJRegion).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedRegion).when(this.regionRepository).save(ArgumentMatchers.any(Region.class));
		// assert
		Assertions.assertThat(this.regionEntityController.createRegion(this.mockedJRegion))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.times(1)).save(ArgumentMatchers.any(Region.class));
	}

	/**
	 * @test delete a {@link Region}
	 * @context {@link Region} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_with_Conflict() {

		// setup
		final Optional<Region> notEmptyRegionOptional = Optional.of(new Region());
		Mockito.doReturn(this.mockedJRegion).when(this.mockedJRegion).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJRegion).textValue();
		Mockito.doReturn(notEmptyRegionOptional).when(this.regionRepository).findByCode(ArgumentMatchers.anyString());
		Mockito.when(this.regionRepository.save(ArgumentMatchers.any(Region.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.regionEntityController.deleteRegion(this.mockedJRegion))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.times(1)).save(ArgumentMatchers.any(Region.class));
	}

	/**
	 * @test delete a {@link Region}
	 * @context {@link Region} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_with_resourceNotFound() {

		// setup
		final Optional<Region> emptyRegionOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJRegion).when(this.mockedJRegion).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJRegion).textValue();
		Mockito.doReturn(emptyRegionOptional).when(this.regionRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.regionEntityController.deleteRegion(this.mockedJRegion))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.never()).save(ArgumentMatchers.any(Region.class));
	}

	/**
	 * @test delete a {@link Region}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void deleteRegion_with_success() {

		// setup
		final Optional<Region> notEmptyRegionOptional = Optional.of(new Region());
		Mockito.doReturn(this.mockedJRegion).when(this.mockedJRegion).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJRegion).textValue();
		Mockito.doReturn(notEmptyRegionOptional).when(this.regionRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.regionEntityController.deleteRegion(this.mockedJRegion))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.times(1)).save(ArgumentMatchers.any(Region.class));
	}

	/**
	 * @test get all {@link Region}
	 * @expected return instance of {@link List} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void getRegions() {

		// assert
		Assertions.assertThat(this.regionEntityController.getRegions()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link Region} by name
	 * @context {@link Region} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Camille Schnell
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getRegion_with_ResourceNotFound() {

		// setup
		final Optional<Region> emptyRegionOptional = Optional.ofNullable(null);
		Mockito.when(this.regionRepository.findByNom("name")).thenReturn(emptyRegionOptional);
		// throw
		this.regionEntityController.getRegion("name");
	}

	/**
	 * @test get a {@link Region} by name
	 * @context success
	 * @expected return instance of {@link Region}
	 * @author Camille Schnell
	 */
	@Test
	public void getRegion_with_success() {

		// setup
		final Optional<Region> notEmptyRegionOptional = Optional.of(new Region());
		// Mockito.when(this.regionRepository.findByName("name")).thenReturn(notEmptyRegionOptional);
		Mockito.doReturn(notEmptyRegionOptional).when(this.regionRepository).findByNom("name");
		// assert
		Assertions.assertThat(this.regionEntityController.getRegion("name")).isInstanceOf(Region.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.times(1)).findByNom("name");
	}

	/**
	 * @test update a {@link Region}
	 * @context {@link Region} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_with_Conflict() {

		// setup
		final Optional<Region> notEmptyRegionOptional = Optional.of(new Region());
		Mockito.doReturn(this.mockedJRegion).when(this.mockedJRegion).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJRegion).textValue();
		Mockito.doReturn(notEmptyRegionOptional).when(this.regionRepository).findByCode(ArgumentMatchers.anyString());
		Mockito.when(this.regionRepository.save(ArgumentMatchers.any(Region.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.regionEntityController.updateRegion(this.mockedJRegion))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.times(1)).save(ArgumentMatchers.any(Region.class));
	}

	/**
	 * @test update a {@link Region}
	 * @context {@link Region} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_with_resourceNotFound() {

		// setup
		final Optional<Region> emptyRegionOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJRegion).when(this.mockedJRegion).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJRegion).textValue();
		Mockito.doReturn(emptyRegionOptional).when(this.regionRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.regionEntityController.updateRegion(this.mockedJRegion))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.never()).save(ArgumentMatchers.any(Region.class));
	}

	/**
	 * @test update a {@link Region}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void updateRegion_with_success() {

		// setup
		final Optional<Region> notEmptyRegionOptional = Optional.of(new Region());
		Mockito.doReturn(this.mockedJRegion).when(this.mockedJRegion).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJRegion).textValue();
		Mockito.doReturn(notEmptyRegionOptional).when(this.regionRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.regionEntityController.updateRegion(this.mockedJRegion))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.regionRepository, Mockito.times(1)).save(ArgumentMatchers.any(Region.class));
	}
}
